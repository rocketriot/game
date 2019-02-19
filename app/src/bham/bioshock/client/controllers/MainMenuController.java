package bham.bioshock.client.controllers;

import bham.bioshock.client.screens.ScreenMaster;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MainMenuScreen;
import bham.bioshock.server.Server;

public class MainMenuController extends Controller {

  Server server;
  BoardGame game;
  Sound mainMenuMusic;
  float musicVolume;
  long musicID;

  @Inject
  public MainMenuController(Store store, Router router, BoardGame game, Server server) {
    super(store, router, game);
    this.server = server;
    this.game = game;
    startMusic();
  }

  /** Creates a server */
  private void startServer() {
    if(!server.isAlive()) {
      server.start();      
    }
  }

  public void hostGame(String hostName) {
    startServer();
    
    router.call(Route.JOIN_SCREEN, hostName);
  }
  
  /** Renders main menu */
  public void show() {
    setScreen(new MainMenuScreen(router));
  }
  
  public void alert(String message) {
    ((ScreenMaster)store.getScreen()).alert(message);
  }

  private void startMusic(){
    mainMenuMusic = Gdx.audio.newSound(Gdx.files.internal("app/assets/music/MainMenuMusic.mp3"));
    musicID = mainMenuMusic.loop();
    setMusicVolume(0.5f, musicID);
  }

  public void setMusicVolume(float volume, long id){
    musicVolume = volume;
    mainMenuMusic.setVolume(id, musicVolume);
  }

}
