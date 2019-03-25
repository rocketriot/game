package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.Config;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class FadingNotificationText {

  private final SpriteBatch batch;
  private final Store store;
  private BitmapFont font;
  private String winnerString;
  private float duration;
  private boolean miniGameEnded = false;
  private boolean showWinner = false;
  private boolean showUpgradeText = false;
  private String upgradeText;

  public FadingNotificationText(SpriteBatch batch, Store store,
      BitmapFont font) {
    this.batch = batch;
    this.store = store;
    this.font = font;
  }

  public void render() {
    checkMinigameEnd();
    checkUpgradePickup();
    if (showWinner || showUpgradeText) {
      batch.begin();
      if (showWinner) {
        int x = Config.GAME_WORLD_WIDTH / 2 - winnerString.length() * 15;
        int y = Config.GAME_WORLD_HEIGHT - 100;
        font.draw(batch, winnerString, x, y);
      } else {
        int x = Config.GAME_WORLD_WIDTH / 2 - upgradeText.length() * 15;
        int y = Config.GAME_WORLD_HEIGHT - 100;
        font.draw(batch, upgradeText, x, y);
      }

      duration -= Gdx.graphics.getDeltaTime();
      if (duration <= 0) {
        showWinner = false;
        showUpgradeText = false;
      } else {
        font.setColor(1, 1, 1, duration/2);
      }
      batch.end();
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

  /** Creates the string to display if client has just picked up an upgrade */
  private void checkUpgradePickup() {
    if (store.getMainPlayer().getLastUpgradeText() != null) {
      duration = 3f;
      showUpgradeText = true;
      upgradeText = store.getMainPlayer().getLastUpgradeText();
      store.getMainPlayer().setLastUpgradeText(null);
      font.setColor(1, 1, 1, 1);
    }
  }
}
