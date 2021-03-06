package bham.bioshock.client.controllers;

import bham.bioshock.client.*;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.PreferencesScreen;
import bham.bioshock.common.models.store.Store;
import com.google.inject.Inject;

/** The Preferences controller. */
public class PreferencesController extends Controller {

  /** Store the current BoardGame */
  BoardGame game;

  /** Store the router */
  Router router;

  /** Instance of the xmlInteraction class that allows reading from the Preferences XML file */
  private XMLInteraction xmlInteraction;

  /** Keep track of the users current preferences */
  private AppPreferences preferences;

  private AssetContainer assets;

  /**
   * Instantiates a new Preferences controller.
   *
   * @param store the store
   * @param router the router
   * @param game the current BoardGame
   */
  @Inject
  public PreferencesController(Store store, Router router, BoardGame game, AssetContainer assets) {
    super(store, router, game);
    this.assets = assets;
    this.game = game;
    this.router = router;
    xmlInteraction = new XMLInteraction();
  }

  /**
   * Method to show the preferences screen - before we set the screen we need to get the users
   * preferences from the XML file so that the sliders and check boxes have the correct values
   */
  public void show() {
    preferences = xmlInteraction.xmlToPreferences();
    setScreen(new PreferencesScreen(router, preferences, assets));
  }

  public void showWithBackRoute(Route backRoute) {
    preferences = xmlInteraction.xmlToPreferences();
    setScreen(new PreferencesScreen(router, preferences, backRoute, assets));
  }
}
