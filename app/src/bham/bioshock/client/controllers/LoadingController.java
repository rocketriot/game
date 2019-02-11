package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.screens.LoadingScreen;
import com.badlogic.gdx.Screen;

public class LoadingController extends Controller {
  private Client client;

  public LoadingController(Client client) {
    this.client = client;
  }

  public void setScreen(Screen screen) {
    this.screen = (LoadingScreen) screen;
  }
}
