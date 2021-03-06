package bham.bioshock.client.controllers;

import bham.bioshock.client.AppPreferences;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.XMLInteraction;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.audio.Sound;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/** The Sound controller. */
@Singleton
public class SoundController extends Controller {

  private static Sound menuSelectSound;
  private static Sound jumpSound;
  private static Sound laserSound;
  private static Sound healthPickupSound;
  private static Sound blackHoleSound;
  private static Sound fuelSound;
  private static Sound upgradeSound;
  private static float soundsVolume;
  private static boolean soundsEnabled;
  private static HashMap<String, Sound> sounds = new HashMap<>();
  /** Sound variables that contain the music used in the game */
  private Sound mainMenuMusic;
  private Sound boardGameMusic;
  private Sound minigameMusic;
  /** Sound variables that contain the sound effects used in the game */
  private Sound rocketSound;
  /** Variables controlling volumes and enabling sounds */
  private float musicVolume;
  private boolean musicEnabled;
  /** Variables to do with interacting with the preferences file */
  private AppPreferences preferences;
  private XMLInteraction xmlInteraction = new XMLInteraction();
  /** Future use for music fade-out concurrency */
  private CompletableFuture<Void> fade;
  /**
   * Hashmaps that store the sound variables with their names, whether they are playing and the id
   * if they are playing
   */
  private HashMap<String, Sound> music = new HashMap<>();
  private HashMap<String, Long> musicIds = new HashMap<>();
  private HashMap<String, Boolean> musicPlaying = new HashMap<>();
  private HashMap<String, Long> soundsIds = new HashMap<>();
  private HashMap<String, Boolean> soundsPlaying = new HashMap<>();

  /**
   * Instantiates a new Sound controller.
   *
   * @param store the store
   * @param router the router
   * @param game the current BoardGame
   * @param assets the assets
   */
  @Inject
  public SoundController(Store store, Router router, BoardGame game, AssetContainer assets) {
    super(store, router, game);

    mainMenuMusic = assets.get(Assets.mainMenuMusic, Sound.class);
    boardGameMusic = assets.get(Assets.gameBoardMusic, Sound.class);
    minigameMusic = assets.get(Assets.miniGameMusic, Sound.class);

    menuSelectSound = assets.get(Assets.menuSelectSound, Sound.class);
    rocketSound = assets.get(Assets.rocketSound, Sound.class);
    jumpSound = assets.get(Assets.jumpSound, Sound.class);
    laserSound = assets.get(Assets.laserSound, Sound.class);
    healthPickupSound = assets.get(Assets.healthPickupSound, Sound.class);
    blackHoleSound = assets.get(Assets.blackHoleSound, Sound.class);
    fuelSound = assets.get(Assets.blackHoleSound, Sound.class);
    upgradeSound = assets.get(Assets.upgradeSound, Sound.class);

    preferences = xmlInteraction.xmlToPreferences();

    musicEnabled = preferences.getMusicEnabled();
    musicVolume = preferences.getMusicVolume();
    soundsEnabled = preferences.getSoundsEnabled();
    soundsVolume = preferences.getSoundsVolume();

    addMusic();
    addSounds();
  }

  /**
   * Method to play a sound that does not need to loop
   *
   * @param sound The name of the sound
   */
  public static void playSound(String sound) {
    if (soundsEnabled) {
      sounds.get(sound).play(soundsVolume);
    }
  }

  /**
   * Method to start a specified music
   *
   * @param music The name of the music that you want to start
   */
  public void startMusic(String music) {
    if (!musicPlaying.get(music) && musicEnabled) {

      Runnable start =
          () -> {
            long id = this.music.get(music).loop(musicVolume);
            musicIds.put(music, id);

            if (musicPlaying.keySet().contains(music)) {
              musicPlaying.replace(music, true);
            } else {
              musicPlaying.put(music, true);
            }
          };

      if (fade == null || fade.isDone()) {
        start.run();
      } else {
        fade.thenRun(
            () -> {
              start.run();
            });
      }
    }
  }

  /**
   * Method to stop a specified music
   *
   * @param music The name of the music that you want to stop
   */
  public void stopMusic(String music) {
    if (musicPlaying.get(music)) {
      this.music.get(music).stop();
      musicPlaying.replace(music, false);
      musicIds.remove(music);
    }
  }

  /**
   * Method to adjust the overall music volume of the game
   *
   * @param volume The volume you want to set the music to
   */
  public void setMusicVolume(float volume) {
    if (volume != musicVolume) {
      musicVolume = volume;

      for (String key : musicPlaying.keySet()) {
        if (musicPlaying.get(key)) {
          adjustCurrentVolume(key, musicIds.get(key), musicVolume);
        }
      }
    }
  }

  /**
   * Method to adjust the volume of a music track while it is playing
   *
   * @param music The name of the music you want to adjust
   * @param id The ID of the music that you want to adjust
   * @param volume The volume you want to set the music to
   */
  public void adjustCurrentVolume(String music, long id, float volume) {
    this.music.get(music).setVolume(id, volume);
  }

  /**
   * Method to enable music to be played in the game
   *
   * @param enable Whether the music is enabled or not
   */
  public void enableMusic(Boolean enable) {
    musicEnabled = enable;

    if (!musicEnabled) {
      for (String key : music.keySet()) {
        stopMusic(key);
      }
    }
  }

  /**
   * Method to fade out music so that another can start in a better sounding way
   *
   * @param music The name of the music that you want to fade out
   * @throws InterruptedException the interrupted exception
   */
  public void fadeOut(String music) {
    if (musicPlaying.get(music)) {

      fade =
          CompletableFuture.runAsync(
              () -> {
                try {
                  int fadeTime = 10;
                  float currentVolume = musicVolume;
                  float step = currentVolume / fadeTime;

                  for (int i = 0; i < fadeTime; i++) {
                    currentVolume -= step;
                    adjustCurrentVolume(music, musicIds.get(music), currentVolume);
                    Thread.sleep(100);
                  }
                } catch (InterruptedException e) {
                }
                stopMusic(music);
              });
    }
  }

  /**
   * Method to loop a sound effect
   *
   * @param sound The sound to loop
   */
  public void loopSound(String sound) {
    if (soundsEnabled && !soundsPlaying.get(sound)) {
      long id = sounds.get(sound).loop(soundsVolume);
      soundsIds.put(sound, id);

      if (soundsPlaying.keySet().contains(sound)) {
        soundsPlaying.replace(sound, true);
      } else {
        soundsPlaying.put(sound, true);
      }
    }
  }

  /**
   * Method to stop a looping sound
   *
   * @param sound The looping sound to stop
   */
  public void stopSound(String sound) {
    if (soundsPlaying.get(sound) && soundsIds.get(sound) != null) {
      sounds.get(sound).stop();
      soundsPlaying.replace(sound, false);
      soundsIds.remove(sound);
    }
  }

  /**
   * Method to set the overall volume for sounds in a game
   *
   * @param volume The volume to set the sounds to
   */
  public void setSoundsVolume(float volume) {
    soundsVolume = volume;
  }

  /**
   * Method to enable sounds to be played in the game
   *
   * @param enable Whether sounds should be enabled or not
   */
  public void enableSounds(Boolean enable) {
    soundsEnabled = enable;
  }

  /**
   * Method to add music tracks to the hashmap of music, as well as add them to the hashmap that
   * holds whether the music is playing or not
   */
  private void addMusic() {
    music.put("mainMenu", mainMenuMusic);
    musicPlaying.put("mainMenu", false);
    music.put("boardGame", boardGameMusic);
    musicPlaying.put("boardGame", false);
    music.put("minigame", minigameMusic);
    musicPlaying.put("minigame", false);
  }

  /**
   * Method to add sounds to the hashmap of sounds and whether the sounds are playing or not if the
   * sound effect is one to be looped
   */
  private void addSounds() {
    sounds.put("menuSelect", menuSelectSound);
    sounds.put("rocket", rocketSound);
    soundsPlaying.put("rocket", false);
    sounds.put("jump", jumpSound);
    sounds.put("laser", laserSound);
    sounds.put("health", healthPickupSound);
    sounds.put("blackHole", blackHoleSound);
    sounds.put("fuel", fuelSound);
    sounds.put("upgrade", upgradeSound);
  }
}
