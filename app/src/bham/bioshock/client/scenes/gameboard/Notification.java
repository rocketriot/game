package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.Config;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Notification {
  private SpriteBatch batch;
  private Store store;
  private FontGenerator fontGenerator;
  private BitmapFont font;

  /** Display notifications for 3 seconds */
  private final float NOTIFICATION_DURATION = 3f;
  
  /** How long the current notification has been displayed */
  private float duration;
  
  private boolean showTurnPrompt;
  
  private boolean wasMainPlayerTurn;

  private String turnText = "It's your turn!";

  private String winnerString;
  
  private boolean showWinner;

  public Notification(SpriteBatch batch, Store store) {
    this.batch = batch;
    this.store = store;
    
    fontGenerator = new FontGenerator();
    font = fontGenerator.generate(72, Color.WHITE);
  }

  public void render() {
    renderTurnNotifcation();
    renderMinigameEndNotifiation();
  }

  private void renderTurnNotifcation() {
    checkTurnChange();

    if (store.isMainPlayersTurn() && showTurnPrompt) {
      duration -= Gdx.graphics.getDeltaTime();
      
      // Hide turn prompt after duration
      if (duration <= 0)
        showTurnPrompt = false;

      // Fade out text
      font.setColor(1, 1, 1, duration/2);

      // Calculate coordinates of the text
//      float xOffset = fontGenerator.getOffset(font, turnText);
      float x = Config.GAME_WORLD_WIDTH / 2 ;
      float y = Config.GAME_WORLD_HEIGHT / 2;
      
      batch.begin();
      font.draw(batch, turnText, x, y);
      batch.end();
    }
  }

  /** sets showTurnPrompt to true if it has just switch to the main player's turn */
  private void checkTurnChange() {
    // If it's the main players turn show the notification
    if (store.isMainPlayersTurn() && !wasMainPlayerTurn) {
      duration = NOTIFICATION_DURATION;
      wasMainPlayerTurn = true;
    }
    
    // If it's no longer the main players turn reset the notification
    if (!store.isMainPlayersTurn() && wasMainPlayerTurn)
      wasMainPlayerTurn = false;
  }

  private void renderMinigameEndNotifiation() {
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
