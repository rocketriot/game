package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.Hud;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class GameBoardHud extends Hud {
  private FuelBar fuelBar;
  private ScoreBoard scoreBoard;
  private TurnStartText turnStartText;
  private SkipTurnButton skipTurnButton;

  public GameBoardHud(SpriteBatch batch, Skin skin, Store store, Router router) {
    super(batch, skin, store, router);

    turnStartText = new TurnStartText(batch, store);
    scoreBoard = new ScoreBoard(stage, batch, skin, store, router);
    fuelBar = new FuelBar(stage, batch, skin, store, router);
    skipTurnButton = new SkipTurnButton(stage, batch, skin, store, router);
  }

  @Override
  public void update() {
    super.update();
    
    Player mainPlayer = store.getMainPlayer();

    scoreBoard.render(store.getRound(), store.getPlayers(), store.getMovingPlayer());
    fuelBar.render(mainPlayer.getFuel());
    turnStartText.render();
    skipTurnButton.render();
  }
}