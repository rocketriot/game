package bham.bioshock.client.ui;

import com.badlogic.gdx.Game;
import bham.bioshock.client.controller.MainController;

public class SceneController extends Game {
    private MainMenuScreen menu_screen;
    private LoadingScreen loading_screen;
    private HowToScreen howto_screen;
    private MainController mainController;

    @Override
    public SceneController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void create() {
        loading_screen = new LoadingScreen(this);
        menu_screen = new MainMenuScreen(this);
        howto_screen = new HowToScreen(this);
        setScreen(menu_screen);
    }

    public void changeScreen(int screen) {
        switch (screen) {
        case 0:
            this.setScreen(loading_screen);
            break;
        case 1:
            this.setScreen(menu_screen);
            break;
        case 2:
            this.setScreen(howto_screen);
            break;
        default:
            this.setScreen(menu_screen);
            break;
        }
    }
}
