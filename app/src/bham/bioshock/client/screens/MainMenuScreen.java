package bham.bioshock.client.screens;

import bham.bioshock.*;
import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.MainMenuController;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;

public class MainMenuScreen extends ScreenMaster {

    //buttons
    private TextButton host;
    private TextButton howto;
    private TextButton preferences;
    private TextButton exit;
    private TextButton join;


    public MainMenuScreen(final MainMenuController controller) {
        this.controller = controller;

        // determine screen size
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenwidth = screensize.width;
        int screenheight = screensize.height;

        // camera = new OrthographicCamera(screenwidth, screenheight);

        // set the stage, which will react to user inputs
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        // calls act with Graphics.getDeltaTime()

    }

    @Override
    public void show() {


    }

    @Override
    public void render(float delta) {
        drawBackground(delta);
        drawButtons();
        addListeners();
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



    private void drawButtons() {
        // Table to hold menu button, will change this to a better style
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // adding button
        // skins to be styled later

         host = new TextButton("Host Game", skin);
         join = new TextButton("Join Game", skin);
         howto = new TextButton("How to Play", skin);
         preferences = new TextButton("Preferences", skin);
         exit = new TextButton("Exit", skin);

        // add the buttons to the table
        table.add(host).fillX().uniform();
        table.row();
        table.add(join).fillX().uniform();
        table.add(howto).fillX().uniform();
        table.row();
        table.add(preferences).fillX().uniform();
        table.row();
        table.add(exit).fillX().uniform();
    }

    private void addListeners(){
        // add change listeners for the buttons
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        host.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.changeScreen(Client.View.HOST_SCREEN);
            }
        });

        howto.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.changeScreen(Client.View.HOW_TO);
            }
        });

        preferences.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.changeScreen(Client.View.PREFERENCES);
            }
        });

        join.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //This is where the networking stuff will go
            }
        });

        Gdx.input.setInputProcessor(stage);
    }
}
