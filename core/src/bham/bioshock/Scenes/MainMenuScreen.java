package bham.bioshock.Scenes;

import bham.bioshock.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


import java.awt.*;

public class MainMenuScreen implements Screen {

    private SceneController scene_controller;
    //private OrthographicCamera camera;
    private Stage stage;

    public MainMenuScreen(final SceneController scene_controller){
        this.scene_controller = scene_controller;


        //determine screen size
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenwidth = screensize.width;
        int screenheight = screensize.height;

        //camera = new OrthographicCamera(screenwidth, screenheight);

        //set the stage, which will react to user inputs
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        //calls act with Graphics.getDeltaTime()



    }
    @Override
    public void show() {
        //Table to hold menu button, will change this to a better style
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        //adding button
        //skins to be styled later
        Skin skin = new Skin(Gdx.files.internal("core/assets/skins/neon/skin/neon-ui.json"));
        TextButton host = new TextButton("Host Game", skin);
        TextButton howto = new TextButton("How to Play", skin);
        TextButton exit = new TextButton("Exit", skin);

        //add the buttons to the table
        table.add(host).fillX().uniform();
        table.row().pad(10,0,10,0);
        table.add(howto).fillX().uniform();
        table.row();
        table.add(exit).fillX().uniform();

        //add change listeners for the buttons
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        host.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //start the code that creates a new server
            }
        });

        howto.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                scene_controller.changeScreen(2);
            }
        });
    }

    @Override
    public void render(float delta) {
        //clear the screen
        Gdx.gl.glClearColor(0,0,0,0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        //camera.update();


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
        stage.dispose();
    }
}
