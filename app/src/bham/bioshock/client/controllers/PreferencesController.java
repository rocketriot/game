package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.XMLReader;

public class PreferencesController extends Controller {

  private XMLReader reader;

  private float volume;
  private boolean sound_on;
  private boolean music_on;
  private String name;
  private int difficulty;

  public PreferencesController(Client client) {
    this.client = client;
    this.model = client.getModel();

    reader = new XMLReader("app/assets/Preferences/Preferences.XML");
  }

  private void readPreferences() {}
}
