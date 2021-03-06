package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.Config;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.Upgrade;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/** Stores all the buttons to use on the bottom of the game board screen */
public class BottomButtonBar extends HudElement {
  /** Button to let the player end their turn */
  TextButton endTurnButton;

  /** Button to let the player place a black hole if they have the upgrade */
  TextButton blackHoleButton;

  public BottomButtonBar(
      Stage stage, SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    super(stage, batch, assets, store, router);
  }

  /** Setup the bottom button bar */
  protected void setup() {
    endTurnButton = new TextButton("End Turn", skin);
    blackHoleButton = new TextButton("Black Hole", skin);

    endTurnButton.setX((Config.GAME_WORLD_WIDTH - endTurnButton.getWidth()) / 2);
    endTurnButton.setY(15);

    blackHoleButton.setX((Config.GAME_WORLD_WIDTH - endTurnButton.getWidth()) / 2 - 300);
    blackHoleButton.setY(15);

    endTurnButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            router.call(Route.END_TURN);
          }
        });

    blackHoleButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            store.getMainPlayer().toggleAddingBlackHole();
          }
        });

    stage.addActor(endTurnButton);
    stage.addActor(blackHoleButton);
  }

  /** Render the bottom button bar */
  protected void render() {
    Player mainPlayer = store.getMainPlayer();

    // Show the end turn button if it's the main players turn
    boolean isMainPlayersTurn = store.isMainPlayersTurn();
    boolean isMainPlayerMoving =
        mainPlayer.getBoardMove() == null || !mainPlayer.getBoardMove().isEmpty();
    endTurnButton.setVisible(isMainPlayersTurn && isMainPlayerMoving);

    // Show the black hole button if the player has a black hole
    blackHoleButton.setVisible(mainPlayer.hasUpgrade(Upgrade.Type.BLACK_HOLE));
  }
}
