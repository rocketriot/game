package bham.bioshock.client.scenes;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.GameBoardController;
import bham.bioshock.client.screens.StatsContainer;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class MinigameHud implements Disposable {

    private MinigameStore store;
    private Router router;

    private final Skin skin;
    private final float gameWidth;
    private final float gameHeight;
    public Stage stage;
    public FitViewport viewport;
    private HorizontalGroup topBar;
    //private SelectBox optionsMenu;
    private ProgressBar fuelBar;
    private String fuelString;
    private Label fuelLabel;
    private Table table;
    private ArrayList<Label> labels;

    private StatsContainer statsContainer;
    private HashMap<bham.bioshock.minigame.models.Player, Player> minigames_players_map;

    private final int PADDING = 20;


    public MinigameHud(SpriteBatch batch, Skin skin, int gameWidth, int gameHeight, MinigameStore store, Router router) {
        this.store = store;
        this.router = router;
        this.skin = skin;
        this.gameWidth = gameWidth / 1.5f;
        this.gameHeight = gameHeight / 1.5f;
        viewport = new FitViewport(this.gameWidth, this.gameHeight, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        minigames_players_map = store.getPlayerMap();

        setupTopBar();
        //setupStatsContainer();
        setupFuelBar();
        setupScoreList();
    }
    private void setupStatsContainer() {
        statsContainer = new StatsContainer(store);
        topBar.addActor(statsContainer);
    }

    private void setupFuelBar() {
        VerticalGroup fuelGroup = new VerticalGroup();
        fuelBar = new ProgressBar(0, 100, 1, false, skin);
        float fuel = minigames_players_map.get(store.getMainPlayer()).getFuel();
        fuelBar.setValue(fuel);
        fuelString = "Fuel: " + fuel + "/100.0";
        fuelLabel = new Label(fuelString, skin);
        fuelGroup.addActor(fuelLabel);
        fuelGroup.addActor(fuelBar);
        topBar.addActor(fuelGroup);
    }

    private void setupScoreList() {
        table = new Table();
        table.pad(PADDING);
        table.top();
        table.setPosition(0, 0);
        table.setFillParent(true);
        stage.addActor(table);

        float maxPad = 0;

        Label l1 = new Label("SCORES  ", skin);
        table.add(l1);
        if(maxPad < l1.getPrefWidth()) {
            maxPad = l1.getPrefWidth();
        }
        table.row();

        labels = new ArrayList<>();
        Iterator<Player> iterator = minigames_players_map.values().iterator();

        while(iterator.hasNext()) {
            Player p = iterator.next();
            String pointsString = p.getUsername() + ": " + p.getPoints();
            labels.add(new Label(pointsString, skin));
        }

        for (Label l : labels) {
            table.add(l);
            if(maxPad < l.getPrefWidth()) {
                maxPad = l.getPrefWidth();
            }
            table.row();
        }
        table.padLeft(gameWidth - maxPad);
    }

    private void updateFuel() {
        float fuel = minigames_players_map.get(store.getMainPlayer()).getFuel();
        fuelBar.setValue(fuel);
        fuelString = "Fuel: " + fuel + "/100.0";
        fuelLabel.setText(fuelString);
    }

    private void updatePoints() {
        table.clearChildren();
        setupScoreList();
    }

    private void setupTopBar() {
        topBar = new HorizontalGroup();
        topBar.setFillParent(true);
        topBar.top();
        topBar.setPosition(0, 0);
        topBar.pad(PADDING);
        stage.addActor(topBar);

        // Adds widgets to the topBar
        /*optionsMenu = new SelectBox(skin);
        String[] menuOptions = {"Options Menu", "Settings", "Quit to main menu", "Quit to Desktop"};
        optionsMenu.setItems(menuOptions);
        optionsMenu.setSelected(menuOptions[0]);
        topBar.addActor(optionsMenu);


        // Add listeners for each option
        optionsMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int selected = optionsMenu.getSelectedIndex();
                switch (selected) {
                    case 1:
                        optionsMenu.setSelected(menuOptions[0]);
                        router.call(Route.PREFERENCES);
                        break;
                    case 2:
                        optionsMenu.setSelected(menuOptions[0]);
                        router.call(Route.MAIN_MENU);
                        break;
                    case 3:
                        optionsMenu.setSelected(menuOptions[0]);
                        Gdx.app.exit();
                        break;
                }
            }
        });*/
    }

    public void updateHud() {
        updateFuel();
        updatePoints();
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
