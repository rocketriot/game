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
    private float masterVolume;
    private long musicID;
    private boolean menuPlaying;

    @Inject
    public SoundController(Store store, Router router, BoardGame game) {
        super(store, router, game);
        mainMenuMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MainMenuMusic.mp3"));
        menuSelect = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MenuSelect.wav"));
        menuPlaying = false;
        musicVolume = 1.0f;
        masterVolume = 1.0f;
    }

    public void menuMusic() {
        if (!menuPlaying){
            musicID = mainMenuMusic.loop();
            menuPlaying = true;

            setMusicVolume(0f, musicID);
        }
    }

    public void setMusicVolume(float volume, long id) {
        musicVolume = volume;
        mainMenuMusic.setVolume(id, musicVolume);
    }

    public void selectSound(){
        menuSelect.play();
    }

}
