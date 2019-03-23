package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MinigameWinnerText {

  private final SpriteBatch batch;
  private final Store store;
  private BitmapFont font;
  private String winnerString;
  private float duration;
  private boolean miniGameEnded = false;
  private boolean showWinner;

  public MinigameWinnerText(SpriteBatch batch, Store store,
      BitmapFont font) {
    this.batch = batch;
    this.store = store;
    this.font = font;
  }

  public void render() {
    checkMinigameEnd();
    if (showWinner) {
      batch.begin();
      int x = Config.GAME_WORLD_WIDTH / 2 - winnerString.length() * 15;
      int y = Config.GAME_WORLD_HEIGHT - 100;
      font.draw(batch, winnerString, x, y);
      batch.end();

      duration -= Gdx.graphics.getDeltaTime();
      if (duration <= 0) {
        showWinner = false;
        store.setMinigameWinner(null);
      } else {
        font.setColor(1, 1, 1, duration/2);
      }
    }
  }

  /** Creates the string to display if someone has just won a minigame and sets showWinner to true */
  private void checkMinigameEnd() {
    if (!miniGameEnded && store.getMinigameWinner() != null) {
      duration = 2f;
      winnerString = store.getMinigameWinner() + " won the minigame!";
      showWinner = true;
      font.setColor(1, 1, 1, 1);
    } else if (!store.isMainPlayersTurn() && miniGameEnded) {
      miniGameEnded = false;
    }
  }
}
