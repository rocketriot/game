package bham.bioshock.client.controllers;

import bham.bioshock.client.AppPreferences;
import bham.bioshock.client.screens.PreferencesScreen;
import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.XMLInteraction;
import bham.bioshock.common.models.store.Store;

public class PreferencesController extends Controller {

  BoardGame game;
  Router router;

  private XMLInteraction xmlInteraction;
  private AppPreferences preferences;

  @Inject
  public PreferencesController(Store store, Router router, BoardGame game) {
    super(store, router, game);

    this.game = game;
    this.router = router;
    xmlInteraction = new XMLInteraction();
  }

  public void show() {
    preferences = xmlInteraction.xmlToPreferences();
    setScreen(new PreferencesScreen(router, preferences));
  }
}
