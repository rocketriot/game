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
      } else {
        font.setColor(1, 1, 1, duration/2);
      }
    }
  }

  /** Creates the string to display if someone has just won a minigame and sets showWinner to true */
  private void checkMinigameEnd() {
    if (store.getMinigameWinner() != null) {
      duration = 3f;
      winnerString = store.getMinigameWinner() + " won the minigame!";
      showWinner = true;
      store.setMinigameWinner(null);
      font.setColor(1, 1, 1, 1);
    }
  }
}
