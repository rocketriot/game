package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.Controller;
import bham.bioshock.client.controllers.PreferencesController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public abstract class ScreenMaster implements Screen {
    protected Controller controller;
    protected Stage stage;
    protected Batch batch;
    protected Stack stack;

    protected TextButton back_button;

    protected Skin skin = new Skin(Gdx.files.internal("app/assets/skins/neon/skin/neon-ui.json"));

    @Override
    public void show() {

    }


    protected void drawBackground(float delta) {
        //render background
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Create background
        Texture background = new Texture(Gdx.files.internal("app/assets/backgrounds/menu.png"));

        batch.begin();
        batch.draw(background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        addBackButton();

        Gdx.input.setInputProcessor(stage);

        stage.act();
        stage.draw();
    }

    protected void addBackButton(){
        //add a button that takes the user back to the previous screen
        back_button = new TextButton("Back", skin);
        stage.addActor(back_button);
    }

    protected void setPrevious(final Client.View previous) {
        back_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.changeScreen(previous);
            }
        });
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
        batch.dispose();
    }
}
