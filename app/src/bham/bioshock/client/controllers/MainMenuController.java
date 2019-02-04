package bham.bioshock.client.controllers;

import com.badlogic.gdx.Screen;

import bham.bioshock.client.Client;
import bham.bioshock.client.screens.MainMenuScreen;

public class MainMenuController implements Controller {
    private Client client;
    private MainMenuScreen screen;

    public MainMenuController(Client client) {
        this.client = client;
    }

    public void setScreen(Screen screen) {
        this.screen = (MainMenuScreen) screen;
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}