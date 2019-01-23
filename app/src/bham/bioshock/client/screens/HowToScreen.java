package bham.bioshock.client.screens;

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

public class HowToScreen implements Screen {
    private HowToController controller;
    private Stage stage;

    public HowToScreen(HowToController controller) {
        this.controller = controller;

        stage = new Stage(new ScreenViewport());

    }

    @Override
    public void show() {

        // create text
        BitmapFont font;
        font = new BitmapFont(Gdx.files.internal("app/assets/skins/default.fnt"));

        stage.getBatch().begin();
        font.setColor(Color.WHITE);
        font.draw(stage.getBatch(), "How to Play the Game...", 10, 10);
        stage.getBatch().end();

        stage.act();
        stage.draw();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width, height, true);
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

    @Override
    public void dispose() {
        stage.dispose();
    }
}
