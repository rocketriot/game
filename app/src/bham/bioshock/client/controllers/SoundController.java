package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SoundController extends Controller {

  private Sound mainMenuMusic;
  private Sound boardGameMusic;
  private Sound menuSelectSound;

  private float musicVolume;
  private boolean musicEnabled;
  private float soundsVolume;
  private boolean soundsEnabled;

  private HashMap<String, Sound> music = new HashMap<>();
  private HashMap<String, Long> ids = new HashMap<>();
  private HashMap<String, Boolean> playing = new HashMap<>();
  private HashMap<String, Sound> sounds = new HashMap<>();

  @Inject
  public SoundController(Store store, Router router, BoardGame game) {
    super(store, router, game);

    mainMenuMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MainMenuMusic.mp3"));
    boardGameMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/GameBoardMusic.mp3"));
    menuSelectSound = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MenuSelect.wav"));

    musicVolume = 0.4f;
    musicEnabled = true;
    soundsEnabled = true;
    soundsVolume = 0.4f;

    addMusic();
    addSounds();
  }

  /**
   * Method to start a specified music
   * @param music The name of the music that you want to start
   */
  public void startMusic(String music) {
    if (!playing.get(music)) {
      long id = this.music.get(music).loop(musicVolume);
      ids.put(music, id);
      if (playing.keySet().contains(music)) {
        playing.replace(music, true);
      } else {
        playing.put(music, true);
      }
    }
  }

  /**
   * Method to stop a specified music
   * @param music The name of the music that you want to stop
   */
  public void stopMusic(String music) {
    this.music.get(music).pause();
    playing.replace(music, false);
    ids.remove(music);
  }

  /**
   * Method to adjust the overall music volume of the game
   * @param volume The volume you want to set the music to
   */
  public void setMusicVolume(float volume) {
    if (volume != musicVolume) {
      musicVolume = volume;

      for (String key : playing.keySet()) {
        if (playing.get(key)) {
          adjustCurrentVolume(key, ids.get(key), musicVolume);
        }
      }
    }
  }

  /**
   * Method to adjust the volume of a music track while it is playing
   * @param music   The name of the music you want to adjust
   * @param id      The ID of the music that you want to adjust
   * @param volume  The volume you want to set the music to
   */
  private void adjustCurrentVolume(String music, long id, float volume) {
    this.music.get(music).setVolume(id, volume);
  }

  /**
   * Method to enable music to be played in the game
   * @param enable  Whether the music is enabled or not
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
   * @param music The name of the music that you want to fade out
   */
  public void fadeOut(String music){
    float currentVolume = musicVolume;
    float step = musicVolume / 3;

    for (int i = 0; i < 3; i++){
      currentVolume -= step;
      adjustCurrentVolume(music, ids.get(music), currentVolume);
    }
    stopMusic(music);
  }

  /**
   * Method to play a sound
   * @param sound The sound that you want to play
   */
  public void playSound(String sound) {
    if (soundsEnabled) {
      sounds.get(sound).play(soundsVolume);
    }
  }

  /**
   * Method to set the overall volume for sounds in a game
   * @param volume  The volume to set the sounds to
   */
  public void setSoundsVolume(float volume) {
    soundsVolume = volume;
  }

  /**
   * Method to enable sounds to be played in the game
   * @param enable  Whether sounds should be enabled or not
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
    playing.put("mainMenu", false);
    music.put("boardGame", boardGameMusic);
    playing.put("boardGame", false);
  }

  /**
   * Method to add sounds to the hashmap of sounds
   */
  private void addSounds() {
    sounds.put("menuSelect", menuSelectSound);
  }

}
