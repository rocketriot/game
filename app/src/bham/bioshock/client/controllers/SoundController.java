package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SoundController extends Controller {

    private Sound mainMenuMusic;
    private Sound menuSelect;
    private float musicVolume;
    private boolean musicEnabled;
    private long menuMusicID;
    private boolean menuPlaying;
    private float soundsVolume;
    private boolean soundsEnabled;

    @Inject
    public SoundController(Store store, Router router, BoardGame game) {
        super(store, router, game);
        mainMenuMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MainMenuMusic.mp3"));
        menuSelect = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MenuSelect.wav"));

        menuPlaying = false;
        musicVolume = 0.4f;
        musicEnabled = true;
        soundsEnabled = true;
        soundsVolume = 0.4f;
    }

    public void startMenuMusic() {
        if (!menuPlaying && musicEnabled){
            menuMusicID = mainMenuMusic.loop();
            menuPlaying = true;
        }
    }

    public void setMusicVolume(float volume) {
        musicVolume = volume;
        mainMenuMusic.setVolume(menuMusicID, musicVolume);
    }

    public void enableMusic(Boolean enable){
        musicEnabled = enable;

        if (!enable){
            menuPlaying = false;
            stopMainMusic();
        } else {
            startMenuMusic();
        }
    }

    public void stopMainMusic(){
        mainMenuMusic.stop();
    }

    public void selectSound(){
        if (soundsEnabled) {
            menuSelect.play(soundsVolume);
        }
    }

    public void setSoundsVolume(float volume) {
        soundsVolume = volume;
    }

    public void enableSounds(Boolean enable){
        soundsEnabled = enable;
    }

}
