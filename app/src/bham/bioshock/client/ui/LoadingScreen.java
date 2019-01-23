package bham.bioshock.client.ui;
import com.badlogic.gdx.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;

import java.awt.*;

public class LoadingScreen implements Screen {

    private SceneController scene_controller;
    private OrthographicCamera camera;

    public LoadingScreen(SceneController scene_controller){
        this.scene_controller = scene_controller;


        //determine screen size
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        float screenwidth = screensize.width;
        float screenheight = screensize.height;

        //camera = new OrthographicCamera(screenwidth, screenheight);
        scene_controller.changeScreen(1);
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

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
