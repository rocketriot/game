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
  private long menuMusicID;
  private Sound boardGameMusic;
  private long boardMusicID;
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


  public void startMusic(String music) {
    if (!playing.get(music)) {
      long id = this.music.get(music).loop(musicVolume);
      ids.put(music, id);
      playing.replace(music, true);
    }
  }

  public void stopMusic(String music) {
    this.music.get(music).pause();
    playing.replace(music, false);
    ids.remove(music);
  }

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

  private void adjustCurrentVolume(String music, long id, float volume) {
    this.music.get(music).setVolume(id, volume);
  }

  public void enableMusic(Boolean enable) {
    musicEnabled = enable;

    if (!musicEnabled) {
      for (String key : music.keySet()) {
        stopMusic(key);
      }
    }
  }

  public void playSound(String sound) {
    if (soundsEnabled) {
      sounds.get(sound).play(soundsVolume);
    }
  }

  public void setSoundsVolume(float volume) {
    soundsVolume = volume;
  }

  public void enableSounds(Boolean enable) {
    soundsEnabled = enable;
  }

  private void addMusic() {
    music.put("mainMenu", mainMenuMusic);
    playing.put("mainMenu", false);
    music.put("boardGame", boardGameMusic);
    playing.put("boardGame", false);
  }

  private void addSounds() {
    sounds.put("menuSelect", menuSelectSound);
  }

}
