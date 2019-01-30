package bham.bioshock.client.controllers;

import bham.bioshock.client.Client;
import bham.bioshock.client.XMLReader;
import bham.bioshock.common.models.Model;

public class PreferencesController extends Controller {


    private XMLReader reader;

    private float volume;
    private boolean sound_on;
    private boolean music_on;
    private String name;
    private int difficulty;

    public PreferencesController(Client client, Model model) {
        this.client = client;
        this.model = model;

        reader = new XMLReader("app/assets/Preferences/Preferences.XML");
    }

    private void readPreferences() {

    }
}
