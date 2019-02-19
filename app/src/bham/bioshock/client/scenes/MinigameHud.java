package bham.bioshock.client.scenes;

import bham.bioshock.client.Router;
import bham.bioshock.client.screens.StatsContainer;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MinigameHud implements Disposable {

    private Store store;
    private Router router;

    private final Skin skin;
    private final float gameWidth;
    private final float gameHeight;
    public Stage stage;
    public FitViewport viewport;

    private HorizontalGroup topBar;
    private ProgressBar fuelBar;
    private String fuelString;
    private Label fuelLabel;
    private Table table;
    private ArrayList<Label> labels;

    private StatsContainer statsContainer;
    private ArrayList<Player> players;

    private final int PADDING = 20;


    public MinigameHud(SpriteBatch batch, Skin skin, int gameWidth, int gameHeight, Store store, Router router) {
        this.store = store;
        this.router = router;
        this.skin = skin;
        this.gameWidth = gameWidth / 1.5f;
        this.gameHeight = gameHeight / 1.5f;
        viewport = new FitViewport(this.gameWidth, this.gameHeight, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        players = store.getPlayers();

        setupTopBar();
        setupFuelBar();
        setupStatsContainer();
        //setupScoreList();
    }

    private void setupTopBar() {
        topBar = new HorizontalGroup();
        topBar.setFillParent(true);
        topBar.top();
        topBar.setWidth(gameWidth);
        topBar.setPosition(0, 0);
        topBar.pad(PADDING);
        stage.addActor(topBar);

    }
    private void setupStatsContainer() {
        statsContainer = new StatsContainer(store);
        statsContainer.setWidth(topBar.getWidth()/2);
        statsContainer.setPosition(topBar.getWidth() - (statsContainer.getWidth() + PADDING), PADDING);
        System.out.println("stats container X Pos: "+ statsContainer.getX() + "width: "+statsContainer.getWidth());
        topBar.addActor(statsContainer);
    }

    private void setupFuelBar() {
        VerticalGroup fuelGroup = new VerticalGroup();
        fuelGroup.setWidth(topBar.getWidth()/2);
        fuelBar = new ProgressBar(0, 100, 1, false, skin);
        float fuel = store.getMainPlayer().getFuel();
        fuelBar.setValue(fuel);
        fuelString = "Fuel: " + fuel + "/100.0";
        fuelLabel = new Label(fuelString, skin);
        fuelGroup.addActor(fuelLabel);
        fuelGroup.addActor(fuelBar);
        fuelGroup.setPosition(PADDING, PADDING);

        topBar.addActor(fuelGroup);
    }

    private void setupScoreList() {
        table = new Table();
        table.pad(PADDING);
        table.top();
        table.setPosition(0, 0);
        table.setFillParent(true);
        stage.addActor(table);

        statsContainer = new StatsContainer(store);
        table.add(statsContainer).right();

        /*float maxPad = 0;

        Label l1 = new Label("SCORES  ", skin);
        table.add(l1);
        if(maxPad < l1.getPrefWidth()) {
            maxPad = l1.getPrefWidth();
        }
        table.row();

        labels = new ArrayList<>();
        Iterator<Player> iterator = players.iterator();

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
        */
    }

    private void updateFuel() {
        float fuel = store.getMainPlayer().getFuel();
        fuelBar.setValue(fuel);
        fuelString = "Fuel: " + fuel + "/100.0";
        fuelLabel.setText(fuelString);
    }

    private void updatePoints() {
        table.clearChildren();
        setupScoreList();
    }



    public void updateHud() {
        updateFuel();
        //statsContainer.updateAll();
        //updatePoints();
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
