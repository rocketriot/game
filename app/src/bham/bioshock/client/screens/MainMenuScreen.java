package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.MainMenuController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.awt.*;

public class MainMenuScreen extends ScreenMaster {
    private MainMenuController controller;

    // buttons
    private TextButton host;
    private TextButton howto;
    private TextButton preferences;
    private TextButton exit;
    private TextButton join;

    public MainMenuScreen(final MainMenuController controller) {
        this.controller = controller;

        // set the stage, which will react to user inputs
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        // calls act with Graphics.getDeltaTime()

    }

    @Override
    public void show() {
        super.show();
        drawButtons();
        addListeners();

        Gdx.input.setInputProcessor(stage);
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
        super.render(delta);
    }

    private void drawButtons() {
        Container<Table> tableContainer = new Container<>();
        float container_width = screen_width * 0.8f;
        float container_height = screen_height * 0.9f;
        tableContainer.setSize(container_width, container_height);
        tableContainer.setPosition((screen_width - container_width) / 2.0f, (screen_height - container_height) / 2.0f);

        // Table to hold menu button, will change this to a better style
        Table table = new Table(skin);

        // adding buttons
        // skins to be styled later

        host = new TextButton("Host Game", skin);
        join = new TextButton("Join Game", skin);
        howto = new TextButton("How to Play", skin);
        preferences = new TextButton("Preferences", skin);
        exit = new TextButton("Exit", skin);

        // add the buttons to the table
        table.row();
        table.add(host).fillX().uniform();
        table.row();
        table.add(join).fillX().uniform();
        table.row();
        table.add(howto).fillX().uniform();
        table.row();
        table.add(preferences).fillX().uniform();
        table.row();
        table.add(exit).fillX().uniform();

        tableContainer.setActor(table);
        stage.addActor(tableContainer);
    }

    private void addListeners() {
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
                controller.createServer();
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
                controller.changeScreen(Client.View.GAME_BOARD);
            }
        });

        Gdx.input.setInputProcessor(stage);
    }
}
