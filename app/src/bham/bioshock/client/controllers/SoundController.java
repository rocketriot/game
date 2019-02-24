package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.util.HashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SoundController extends Controller {

  private Sound mainMenuMusic;
  private Sound boardGameMusic;
  private Sound minigameMusic;
  private Sound menuSelectSound;
  private Sound rocketSound;

  private float musicVolume;
  private boolean musicEnabled;
  private float soundsVolume;
  private boolean soundsEnabled;

  private HashMap<String, Sound> music = new HashMap<>();
  private HashMap<String, Long> musicIds = new HashMap<>();
  private HashMap<String, Boolean> musicPlaying = new HashMap<>();
  private HashMap<String, Sound> sounds = new HashMap<>();
  private HashMap<String, Boolean> soundsPlaying = new HashMap<>();
  private HashMap<String, Long> soundsIds = new HashMap<>();

  @Inject
  public SoundController(Store store, Router router, BoardGame game) {
    super(store, router, game);

    mainMenuMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MainMenuMusic.mp3"));
    boardGameMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/GameBoardMusic.mp3"));
    minigameMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MinigameMusic.mp3"));
    menuSelectSound = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MenuSelect.wav"));
    rocketSound = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/RocketSound.wav"));

    musicVolume = 0.4f;
    musicEnabled = true;
    soundsEnabled = true;
    soundsVolume = 0.4f;

    addMusic();
    addSounds();
  }

  /**
   * Method to start a specified music
   *
   * @param music The name of the music that you want to start
   */
  public void startMusic(String music) {
    if (!musicPlaying.get(music)) {
      long id = this.music.get(music).loop(musicVolume);
      musicIds.put(music, id);

      if (musicPlaying.keySet().contains(music)) {
        musicPlaying.replace(music, true);
      } else {
        musicPlaying.put(music, true);
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
   */
  public void fadeOut(String music) throws InterruptedException {
    if (musicPlaying.get(music)) {
      int fadeTime = 30;
      float currentVolume = musicVolume;
      float step = currentVolume / fadeTime;

      for (int i = 0; i < fadeTime; i++) {
        currentVolume -= step;
        adjustCurrentVolume(music, musicIds.get(music), currentVolume);
        Thread.sleep(100);
      }
      stopMusic(music);
    }
  }

  /**
   * Method to play a sound
   *
   * @param sound The sound that you want to play
   */
  public void playSound(String sound) {
    if (soundsEnabled) {
      sounds.get(sound).play(soundsVolume);
    }
  }

  /**
   * Method to stop a looping sound
   *
   * @param sound The looping sound to stop
   */
  public void stopSound(String sound) {
    if (soundsPlaying.get(sound)) {
      sounds.get(sound).stop();
      soundsPlaying.replace(sound, false);
      soundsPlaying.remove(sound);
    }
  }

  /**
   * Method to loop a sound effect
   *
   * @param sound The sound to loop
   */
  public void loopSound(String sound) {
    if (soundsEnabled) {
      long id = sounds.get(sound).loop(soundsVolume);
      soundsIds.put(sound, id);

      if (soundsPlaying.keySet().contains(music)) {
        soundsPlaying.replace(sound, true);
      } else {
        soundsPlaying.put(sound, true);
      }
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
  }
}
