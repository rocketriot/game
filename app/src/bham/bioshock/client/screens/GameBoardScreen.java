package bham.bioshock.client.screens;

import bham.bioshock.common.consts.GridPoint;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import bham.bioshock.client.controllers.GameBoardController;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;

public class GameBoardScreen extends ScreenMaster {

    private Sprite p1Sprite;
    private Pixmap player1Pixmap;
    private float coordRatio;
    private Image p1;
    private Texture player1;
    private GameBoardController controller;
    private SpriteBatch batch;
    private Texture background;
    private OrthographicCamera camera;
    private FitViewport viewport;

    public GameBoardScreen(final GameBoardController controller) {
        this.controller = controller;
        batch = new SpriteBatch();

        int screenwidth = 1920;
        int screenheight = 1080;
        coordRatio = screenheight/36f;

        camera = new OrthographicCamera();
        viewport = new FitViewport(screenwidth, screenheight, camera);
        viewport.apply();

        background = new Texture(Gdx.files.internal("app/assets/backgrounds/game.png"));
        player1 = new Texture(Gdx.files.internal("app/assets/entities/rockets/1.png"));
        p1Sprite = new Sprite(player1);
        p1Sprite.setSize(18, 30);

        stage = new Stage(viewport, batch);

    }

    public void drawBoardObjects() {
        GridPoint[][] grid = controller.getGrid();
        for(int x = 0; x < grid.length; x++) {
            for(int y = 0; y < grid[x].length; y++) {
                GridPoint.Type pType = grid[x][y].getType();
                if (pType == GridPoint.Type.PLAYER) {
                    //TODO get the object rather than the type
                    p1Sprite.setX(x * coordRatio);
                    p1Sprite.setY(y * coordRatio);
                    System.out.println(p1Sprite.getX() + ", " + p1Sprite.getY());
                    p1Sprite.draw(stage.getBatch());
                    //stage.getBatch().draw(player1, x * coordRatio, y * coordRatio);
                }
            }
        }
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
    public void resize(int width, int height) {
        batch.setProjectionMatrix(camera.combined);
        viewport.update(width, height);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0);
        drawBoardObjects();
        stage.getBatch().end();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
        background.dispose();
    }

}
