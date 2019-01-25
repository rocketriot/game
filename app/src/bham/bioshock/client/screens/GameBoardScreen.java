package bham.bioshock.client.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import bham.bioshock.client.controllers.GameBoardController;

public class GameBoardScreen extends ScreenMaster {

    private GameBoardController controller;
    private SpriteBatch batch;
    private Texture background;
    private OrthographicCamera camera;
    private ExtendViewport viewport;

    public GameBoardScreen(final GameBoardController controller) {
        this.controller = controller;
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1920, 1080, camera);
        viewport.apply();

        background = new Texture(Gdx.files.internal("app/assets/game-background.png"));
    }

    @Override
    public void show() {

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
    public void render(float delta) {
        drawBackground(delta);
        /*Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(background, 0, 0);
        batch.end();*/
    }


}
