package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameBoardHud extends Hud {
  private FuelBar fuelBar;
  private ScoreBoard scoreBoard;
  private Notification notification;
  private SkipTurnButton skipTurnButton;

  public GameBoardHud(SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    super(batch, assets, store, router);

    notification = new Notification(batch, store);
    scoreBoard = new ScoreBoard(stage, batch, assets, store, router);
    fuelBar = new FuelBar(stage, batch, assets, store, router);
    skipTurnButton = new SkipTurnButton(stage, batch, assets, store, router);
  }

  @Override
  public void update() {
    super.update();
    
    Player mainPlayer = store.getMainPlayer();

    scoreBoard.render(store.getRound(), store.getPlayers(), store.getMovingPlayer());
    fuelBar.render(mainPlayer.getFuel());
    notification.render();
    skipTurnButton.render();
  }
}
