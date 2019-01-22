package bham.bioshock.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.sun.javafx.tk.ScreenConfigurationAccessor;
import sun.jvm.hotspot.utilities.BitMap;

public class HowToScreen implements Screen {
    private SceneController scene_controller;
    private Stage stage;

    public HowToScreen(SceneController scene_controller) {
        this.scene_controller = scene_controller;

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }
    @Override
    public void show() {

        //create text
        BitmapFont font;
        font = new BitmapFont(Gdx.files.internal("core/assets/skins/default.fnt"));
        SpriteBatch batch = new SpriteBatch();
        batch.begin();
        font.draw(batch, "How to Play the Game...", 100, 100);
        batch.end();
        //clear the screen
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

        stage.getViewport().update(width,height,true);
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

    }
}
