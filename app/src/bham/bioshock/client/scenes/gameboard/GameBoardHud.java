package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** HUD for the game board */
public class GameBoardHud extends Hud {
  /** Displays the player's fuel */
  private FuelBar fuelBar;

  /** Displays the scores of all the players */
  private ScoreBoard scoreBoard;

  /** Displays turns and minigame results */
  private Notification notification;

  /** Displays a button to end the turn */
  private BottomButtonBar bottomButtonBar;

  public GameBoardHud(SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    super(batch, assets, store, router);

    notification = new Notification(batch, store);
    scoreBoard = new ScoreBoard(stage, batch, assets, store, router);
    fuelBar = new FuelBar(stage, batch, assets, store, router);
    bottomButtonBar = new BottomButtonBar(stage, batch, assets, store, router);
  }

  /** Update the elements of the hud */
  @Override
  public void update() {
    super.update();

    Player mainPlayer = store.getMainPlayer();

    scoreBoard.render(store.getRound(), store.getPlayers(), store.getMovingPlayer());
    fuelBar.render(mainPlayer.getFuel());
    notification.render();
    bottomButtonBar.render();
  }
}
