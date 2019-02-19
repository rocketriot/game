package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import javax.inject.Singleton;

@Singleton
public class SoundController extends Controller {

    private Sound mainMenuMusic;
    private float musicVolume;
    private long musicID;
    private boolean menuPlaying;

    public SoundController(Store store, Router router, BoardGame game) {
        super(store, router, game);
        mainMenuMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MainMenuMusic.mp3"));
        menuPlaying = false;
    }

    private void menuMusic() {
        if (!menuPlaying){
            musicVolume = 1.0f;
            musicID = mainMenuMusic.loop();
            menuPlaying = true;
        }
    }

    public void setMusicVolume(float volume, long id) {
        musicVolume = volume;
        mainMenuMusic.setVolume(id, musicVolume);
    }

}
