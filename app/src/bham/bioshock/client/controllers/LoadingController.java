package bham.bioshock.client.controllers;

import com.badlogic.gdx.Screen;

import bham.bioshock.client.Client;
import bham.bioshock.client.screens.LoadingScreen;

public class LoadingController extends Controller {
    private Client client;

    public LoadingController(Client client) {
        this.client = client;
    }

    public void setScreen(Screen screen) {
        this.screen = (LoadingScreen) screen;
    }
}
