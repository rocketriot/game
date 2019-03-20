package bham.bioshock.client.scenes.gameboard.hud;

import bham.bioshock.client.FontGenerator;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TurnStartText {

  private SpriteBatch batch;
  private Store store;
  private BitmapFont font;
  private boolean showTurnPrompt;
  private float duration;
  private boolean wasMainPlayerTurn;
  private FontGenerator fontGenerator;
  private int fontSize = 72;

  public TurnStartText(SpriteBatch batch, Store store) {
    this.batch = batch;
    this.store = store;
    this.setup();
  }

  private void setup() {
    fontGenerator = new FontGenerator();
    font = fontGenerator.generate(fontSize, Color.WHITE);
  }

  public void render() {
    checkTurnChange();
    if (store.isMainPlayersTurn() && showTurnPrompt) {
      duration -= Gdx.graphics.getDeltaTime();
      if (duration <= 0) {
        showTurnPrompt = false;
      } else {
        font.setColor(1, 1, 1, duration/2);
      }

      batch.begin();
      int x = Config.GAME_WORLD_WIDTH / 2 - 200;
      int y = Config.GAME_WORLD_HEIGHT / 2;
      font.draw(batch, "Your Turn", x, y);
      batch.end();
    }
  }

  /** sets showTurnPrompt to true if it has just switch to the main player's turn */
  private void checkTurnChange() {
    if (!wasMainPlayerTurn && store.isMainPlayersTurn()) {
      duration = 2f;
      wasMainPlayerTurn = true;
      showTurnPrompt = true;
    } else if (!store.isMainPlayersTurn() && wasMainPlayerTurn) {
      wasMainPlayerTurn = false;
    }
  }
}
