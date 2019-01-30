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

    protected float screen_width;
    protected float screen_height;

    protected TextButton back_button;

    protected Skin skin = new Skin(Gdx.files.internal("app/assets/skins/neon/skin/neon-ui.json"));

    public ScreenMaster() {
        screen_width = Gdx.graphics.getWidth();
        screen_height = Gdx.graphics.getHeight();
    }

    @Override
    public void show() {
        addBackButton();
        //set the back button to take you to main menu - for now
        setPrevious(Client.View.MAIN_MENU);
        //drawBackground();
    }


    protected void drawBackground() {
        //render background
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Create background
        Texture background = new Texture(Gdx.files.internal("app/assets/backgrounds/menu.png"));

        batch.begin();
        batch.draw(background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();


        //Gdx.input.setInputProcessor(stage);

        //stage.act();
        //stage.draw();
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
    public void render(float delta) {
        drawBackground();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
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
        batch.dispose();
    }
}
