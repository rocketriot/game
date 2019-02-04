package bham.bioshock.client.screens;

import java.net.ConnectException;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import bham.bioshock.client.controllers.JoinScreenController;

public class JoinScreen extends ScreenMaster {
    JoinScreenController controller;

    public JoinScreen(JoinScreenController controller) {
        this.controller = controller;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        super.show();

        try {
            controller.connectToServer("test");
        } catch (ConnectException e) {
            // Handle connection error
        }
    }

}