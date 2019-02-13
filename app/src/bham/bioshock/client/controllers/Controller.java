package bham.bioshock.client.controllers;

import bham.bioshock.client.AppPreferences;
import bham.bioshock.client.Client;
import bham.bioshock.client.screens.ScreenMaster;
import bham.bioshock.common.models.Model;
import bham.bioshock.communication.client.ClientService;

/** Root controller used by all other controllers */
public abstract class Controller {

  protected AppPreferences preferences;
  protected Client client;
  protected Model model;
  protected ClientService server;
  protected ScreenMaster screen;

  public Controller() {
    preferences = new AppPreferences();
  }

  public void changeScreen(Client.View screen) {
    client.changeScreen(screen);
  }

  public AppPreferences getPreferences() {
    return preferences;
  }

  public void setScreen(ScreenMaster Screen) {
    this.screen = screen;
  }
}
