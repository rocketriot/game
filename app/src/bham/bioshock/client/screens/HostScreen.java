package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.HostScreenController;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HostScreen extends ScreenMaster {

    public HostScreen(HostScreenController controller) {

        this.controller = controller;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();


    }

    @Override
    public void render(float delta) {
        drawBackground(delta);
    }

    private void drawButtons(){
        //button for start new game


    }

    private void configureNewGame() {
        //get the name of the host
        //ask how many players
        HostScreenController contr = (HostScreenController) controller;
        //contr.configureGame();
    }
}