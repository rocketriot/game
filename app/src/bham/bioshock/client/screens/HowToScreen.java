package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import bham.bioshock.client.controllers.HowToController;

public class HowToScreen extends ScreenMaster {


    public HowToScreen(HowToController controller) {
        this.controller = controller;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

    }

    @Override
    public void show() {


    }

    @Override
    public void render(float delta) {
        drawBackground(delta);
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
