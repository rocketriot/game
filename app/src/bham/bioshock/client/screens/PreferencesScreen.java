package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.PreferencesController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ScreenViewport;



public class PreferencesScreen extends ScreenMaster {


    public PreferencesScreen(PreferencesController controller) {
        this.controller = controller;
        stage = new Stage(new ScreenViewport());
        batch = stage.getBatch();


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        drawBackground(delta);

        drawButtons();
    }

    private void drawButtons(){

        //sound on or off

        //volume control


    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }


}
