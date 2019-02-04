package bham.bioshock.client.scenes;

import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.Controller;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Hud implements Disposable {
    private final HorizontalGroup topBar;
    private final SelectBox optionsMenu;
    public Stage stage;
    public FitViewport viewport;

    public Hud(SpriteBatch batch, Skin skin, int gameWidth, int gameHeight, Controller controller) {
        viewport = new FitViewport(gameWidth, gameHeight, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        topBar = new HorizontalGroup();
        topBar.setFillParent(true);
        topBar.top();

        // Adds widgets to the topBar
        optionsMenu = new SelectBox(skin);
        String[] menuOptions = {"Options Menu", "Settings", "Quit to main menu", "Quit to Desktop"};
        optionsMenu.setItems(menuOptions);
        optionsMenu.setSelected(menuOptions[0]);
        topBar.addActor(optionsMenu);
        topBar.setPosition(0, 0);
        stage.addActor(topBar);

        // Add listeners for each option
        optionsMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int selected = optionsMenu.getSelectedIndex();
                switch (selected) {
                    case 1:
                        controller.changeScreen(Client.View.PREFERENCES);
                        optionsMenu.setSelected(menuOptions[0]);
                        break;
                    case 2:
                        controller.changeScreen(Client.View.MAIN_MENU);
                        optionsMenu.setSelected(menuOptions[0]);
                        break;
                    case 3:
                        Gdx.app.exit();
                        optionsMenu.setSelected(menuOptions[0]);
                        break;
                }

            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    public void printDebugInfo() {
        System.out.println("Space: " + topBar.getSpace());
        System.out.println("PrefHeight: " + topBar.getPrefHeight());
        System.out.println("PrefWidth: " + topBar.getPrefWidth());
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
