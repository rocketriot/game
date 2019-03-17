package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class MinigameHud extends Hud {
    private MinigameInstructions minigameInstructions;
    private WeaponContainer weaponContainer;
    private Clock clock;

    public MinigameHud(SpriteBatch batch, Skin skin, Store store, Router router) {
        super(batch, skin, store, router);

        minigameInstructions = new MinigameInstructions(batch, store);
        weaponContainer = new WeaponContainer(stage, batch, skin, store, router);
        clock = new Clock(stage, batch, skin, store, router);
    }

    @Override
    public void update() {
        super.update();

        minigameInstructions.render();
        weaponContainer.render();
        clock.render();
    }
}
