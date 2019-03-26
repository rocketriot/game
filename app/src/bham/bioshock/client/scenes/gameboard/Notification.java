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
  
  private boolean wasMainPlayerTurn = false;
  private boolean showTurn = false;
  private String turnText = "It's your turn!";

  private boolean showWinner = false;
  private String winnerText;

  private boolean showUpgrade = false;
  private String upgradeText;

  public Notification(SpriteBatch batch, Store store) {
    this.batch = batch;
    this.store = store;
    
    fontGenerator = new FontGenerator();
    font = fontGenerator.generate(72, Color.WHITE);
  }

  public void render() {
    renderMinigameEndNotifiation();
    renderTurnNotifcation();
    renderUpgradePickup();
  }

  private void drawNotification(String text) {
    // Calculate coordinates of the text
    float xOffset = fontGenerator.getOffset(font, text);
    float x = (Config.GAME_WORLD_WIDTH / 2) - xOffset;
    float y = Config.GAME_WORLD_HEIGHT / 2;
    
    // Draw text
    batch.begin();
    font.draw(batch, text, x, y);
    batch.end();
  }

  private void renderTurnNotifcation() {
    checkTurnChange();

    if (store.isMainPlayersTurn() && showTurn) {
      duration -= Gdx.graphics.getDeltaTime();
      
      // Hide turn prompt after duration
      if (duration <= 0)
        showTurn = false;

      // Fade out text
      font.setColor(1, 1, 1, duration/2);
      
      drawNotification(turnText);
    }
  }

  /** sets showTurn to true if it has just switch to the main player's turn */
  private void checkTurnChange() {
    // Don't show the turn prompt is the minigame prompt is visible
    if (showWinner) return;

    // If it's the main players turn show the notification
    if (store.isMainPlayersTurn() && !wasMainPlayerTurn) {
      duration = NOTIFICATION_DURATION;
      wasMainPlayerTurn = true;
      showTurn = true;
    }
    
    // If it's no longer the main players turn reset the notification
    if (!store.isMainPlayersTurn() && wasMainPlayerTurn) {
      wasMainPlayerTurn = false;
    }
  }

  private void renderMinigameEndNotifiation() {
    checkMinigameEnd();
    
    if (!showWinner) return;

    duration -= Gdx.graphics.getDeltaTime();

    if (duration <= 0)
      showWinner = false;

    font.setColor(1, 1, 1, duration / 2);

    drawNotification(winnerText);
  }

  /** Creates the string to display if someone has just won a minigame and sets showWinner to true */
  private void checkMinigameEnd() {
    if (store.getMinigameWinner() == null) return;

    duration = NOTIFICATION_DURATION;
    showWinner = true;
    winnerText = store.getMinigameWinner() + " won the minigame!";
    
    store.setMinigameWinner(null);
  }

  private void renderUpgradePickup() {
    checkUpgradePickup();
    
    if (!showUpgrade) return;

    duration -= Gdx.graphics.getDeltaTime();

    if (duration <= 0)
      showUpgrade = false;

    font.setColor(1, 1, 1, duration / 2);

    drawNotification(upgradeText);
  }

  /** Creates the string to display if client has just picked up an upgrade */
  private void checkUpgradePickup() {
    if (store.getMainPlayer().getLastUpgradeText() == null) return;
    
    duration = NOTIFICATION_DURATION;
    showUpgrade = true;
    upgradeText = store.getMainPlayer().getLastUpgradeText();
    
    store.getMainPlayer().setLastUpgradeText(null);
  }
}
