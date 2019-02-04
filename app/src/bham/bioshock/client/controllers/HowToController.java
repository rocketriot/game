package bham.bioshock.client.controllers;

import com.badlogic.gdx.Screen;

import bham.bioshock.client.Client;
import bham.bioshock.client.screens.HowToScreen;

public class HowToController implements Controller {
    private Client client;
    private HowToScreen screen;

    public HowToController(Client client) {
        this.client = client;
    }

    public void setScreen(Screen screen) {
        this.screen = (HowToScreen) screen;
    }

    public void changeScreen(Client.View screen) {
        client.changeScreen(screen);
    }
}