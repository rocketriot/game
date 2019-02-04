package bham.bioshock.client.controllers;

import com.badlogic.gdx.Screen;

import bham.bioshock.client.Client;

/** Root controller used by all other controllers */
public interface Controller {
    /** Sets the screen related to the view */
    public void setScreen(Screen screen);

    public void changeScreen(Client.View view);
}