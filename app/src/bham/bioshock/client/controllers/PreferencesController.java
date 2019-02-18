package bham.bioshock.client.controllers;

import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.XMLReader;
import bham.bioshock.common.models.store.Store;

public class PreferencesController extends Controller {

  private XMLReader reader;

  private float volume;
  private boolean sound_on;
  private boolean music_on;
  private String name;
  private int difficulty;

  @Inject
  public PreferencesController(Store store, Router router, BoardGame game) {
    super(store, router, game);

    reader = new XMLReader("app/assets/Preferences/Preferences.XML");
  }

  private void readPreferences() {}
}
