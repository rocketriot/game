package bham.bioshock.client.scenes;

import bham.bioshock.client.Router;
import bham.bioshock.client.screens.StatsContainer;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MinigameHud implements Disposable {

    private Store store;
    private Router router;

    private final Skin skin;
    private final float gameWidth;
    private final float gameHeight;
    public Stage stage;
    public FitViewport viewport;

    private ProgressBar fuelBar;
    private String fuelString;
    private Label fuelLabel;

    private StatsContainer statsContainer;
    private MinigameInstructions startText;



    private final int PADDING = 50;


    private Table topTable;
    private VerticalGroup weaponsGroup;
    Image weaponImage;


    public MinigameHud(SpriteBatch batch, Skin skin, int gameWidth, int gameHeight, Store store, Router router) {
        this.store = store;
        this.router = router;
        this.skin = skin;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;

        viewport = new FitViewport(this.gameWidth, this.gameHeight, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        startText = new MinigameInstructions(batch, store);

        setupTopBar();
        setupFuelBar();
        setupWeaponContainer();
        setupStatsContainer();

    }

    private void setupTopBar() {
        topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();
        //topTable.setDebug(true);
        topTable.padTop(PADDING);
        stage.addActor(topTable);

    }
    private void setupStatsContainer() {
        statsContainer = new StatsContainer(store);
        System.out.println("stats X Pos: "+ statsContainer.getX() + "width: "+statsContainer.getWidth() + " height: "+statsContainer.getHeight()+ "Y POS: "+ statsContainer.getY());
        topTable.add(statsContainer).padRight(PADDING).right().top().expand();
    }

    private void setupFuelBar() {
        VerticalGroup fuelGroup = new VerticalGroup();
        fuelBar = new ProgressBar(0, 100, 1, false, skin);
        float fuel = store.getMainPlayer().getFuel();
        fuelBar.setValue(fuel);
        fuelString = "Fuel: " + fuel + "/100.0";
        fuelLabel = new Label(fuelString, skin);
        fuelGroup.addActor(fuelLabel);
        fuelGroup.addActor(fuelBar);

        topTable.add(fuelGroup).padLeft(PADDING).width(stage.getWidth()/4).left().top();
    }

    private void setupWeaponContainer() {

        weaponsGroup = new VerticalGroup();
        weaponImage = new Image(new Texture(Gdx.files.internal("app/assets/minigame/gun.png")));
        weaponImage.setSize(20,20);

        topTable.add(weaponsGroup).padLeft(PADDING).width(stage.getWidth()/8).left().top();;
    }

    private void updateFuel() {
        float fuel = store.getMainPlayer().getFuel();
       fuelBar.setValue(fuel);
        fuelString = "Fuel: " + fuel + "/100.0";
       fuelLabel.setText(fuelString);
    }


    public void updateHud() {
        updateFuel();
        if(store.getMinigameStore().getMainPlayer().haveGun()) {
            showWeapon(true);
        }

        statsContainer.updateAll();
        startText.render();
    }

    private void showWeapon(boolean hasGun) {
        if(hasGun) {
            weaponsGroup.addActor(weaponImage);
        }
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }




}
