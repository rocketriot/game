package bham.bioshock.client.controllers;

import bham.bioshock.client.AppPreferences;
import bham.bioshock.client.screens.PreferencesScreen;
import bham.bioshock.server.Server;
import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.XMLReader;
import bham.bioshock.common.models.store.Store;

public class PreferencesController extends Controller {

  Server server;
  BoardGame game;
  Router router;

  private XMLReader reader;
  private AppPreferences preferences;

  private float volume;
  private boolean sound_on;
  private boolean music_on;
  private String name;
  private int difficulty;

  @Inject
  public PreferencesController(Store store, Router router, BoardGame game) {
    super(store, router, game);

    reader = new XMLReader("app/assets/Preferences/Preferences.XML");
    this.game = game;
    this.router = router;
  }

  public void show() {
    preferences = readPreferences();
    setScreen(new PreferencesScreen(router, preferences));
  }

  private AppPreferences readPreferences() {
    // reader.printNodes("sound");
    return new AppPreferences();
  }
}
