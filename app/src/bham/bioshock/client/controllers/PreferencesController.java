package bham.bioshock.client.controllers;

import com.badlogic.gdx.Screen;

import bham.bioshock.client.Client;
import bham.bioshock.client.XMLReader;
import bham.bioshock.client.screens.PreferencesScreen;
import bham.bioshock.common.models.Model;

public class PreferencesController implements Controller {
    private Client client;
    private PreferencesScreen screen;
    private Model model;

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

    private void readPreferences() {

    }

    public void setScreen(Screen screen) {
        this.screen = (PreferencesScreen) screen;
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}
