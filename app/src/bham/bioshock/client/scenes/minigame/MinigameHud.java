package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.client.screens.StatsContainer;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class MinigameHud extends Hud {
    private StatsContainer statsContainer;
    private MinigameInstructions startText;

    private final int PADDING = 50;

    private Table topTable;
    private VerticalGroup weaponsGroup;
    Image weaponImage;

    public MinigameHud(SpriteBatch batch, Skin skin, Store store, Router router) {
        super(batch, skin, store, router);

        startText = new MinigameInstructions(batch, store);

        setupTopBar();
        setupWeaponContainer();
        setupStatsContainer();

    }

    private void setupTopBar() {
        topTable = new Table();
        topTable.setFillParent(true);
        topTable.top();
        // topTable.setDebug(true);
        topTable.padTop(PADDING);
        stage.addActor(topTable);

    }

    private void setupStatsContainer() {
        statsContainer = new StatsContainer(store);
        System.out.println("stats X Pos: " + statsContainer.getX() + "width: " + statsContainer.getWidth() + " height: "
                + statsContainer.getHeight() + "Y POS: " + statsContainer.getY());
        topTable.add(statsContainer).padRight(PADDING).right().top().expand();
    }

    private void setupWeaponContainer() {

        weaponsGroup = new VerticalGroup();
        weaponImage = new Image(new Texture(Gdx.files.internal("app/assets/minigame/gun.png")));
        weaponImage.setSize(20, 20);

        topTable.add(weaponsGroup).padLeft(PADDING).width(stage.getWidth() / 8).left().top();
        ;
    }

    public void update() {
        super.update();

        if (store.getMinigameStore().getMainPlayer().haveGun()) {
            showWeapon(true);
        }

        statsContainer.updateAll();
        startText.render();
    }

    private void showWeapon(boolean hasGun) {
        if (hasGun) {
            weaponsGroup.addActor(weaponImage);
        }
    }
}
