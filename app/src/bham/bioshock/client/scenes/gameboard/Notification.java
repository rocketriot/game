package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.Config;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Displays turns and minigame notifications */
public class Notification {
  /** Display notifications for 3 seconds */
  private final float NOTIFICATION_DURATION = 3f;

  /** Used to draw the notification */
  private SpriteBatch batch;

  /** Store for data */
  private Store store;

  /** Used to generate fonts */
  private FontGenerator fontGenerator;

  /** Notification font */
  private BitmapFont font;

  /** How long the current notification has been displayed */
  private float duration;

  /** Specifies if it was just the main player's turn */
  private boolean wasMainPlayerTurn = false;

  /** Specifies whether to show a turn notification */
  private boolean showTurn = false;

  /** Text to display it's the player's turn */
  private String turnText = "It's your turn!";

  /** Specifies whether to show a minigame winner notification */
  private boolean showWinner = false;

  /** Text to display it a minigame has ended */
  private String winnerText;

  /** Specifies whether to show an upgrade notification */
  private boolean showUpgrade = false;

  /** Text to display when an upgrade happens */
  private String upgradeText;

  public Notification(SpriteBatch batch, Store store) {
    this.batch = batch;
    this.store = store;

    // Generate a font
    fontGenerator = new FontGenerator();
    font = fontGenerator.generate(72, Color.WHITE);
  }

  /** Renders potential notifications */
  public void render() {
    renderMinigameEndNotifiation();
    renderTurnNotifcation();
    renderUpgradePickup();
  }

  /** Draws text in the center of the screen */
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

  /** Renders a turn notification */
  private void renderTurnNotifcation() {
    checkTurnChange();

    if (store.isMainPlayersTurn() && showTurn) {
      duration -= Gdx.graphics.getDeltaTime();

      // Hide turn prompt after duration
      if (duration <= 0) showTurn = false;

      // Fade out text
      font.setColor(1, 1, 1, duration / 2);

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

  /** Renders a minigame winner notification */
  private void renderMinigameEndNotifiation() {
    checkMinigameEnd();

    if (!showWinner) return;

    duration -= Gdx.graphics.getDeltaTime();

    if (duration <= 0) showWinner = false;

    font.setColor(1, 1, 1, duration / 2);

    drawNotification(winnerText);
  }

  /**
   * Creates the string to display if someone has just won a minigame and sets showWinner to true
   */
  private void checkMinigameEnd() {
    if (store.getMinigameWinner() == null) return;

    duration = NOTIFICATION_DURATION;
    showWinner = true;
    winnerText = store.getMinigameWinner() + " won the minigame!";

    store.setMinigameWinner(null);
  }

  /** Renders an upgrade notification */
  private void renderUpgradePickup() {
    checkUpgradePickup();

    if (!showUpgrade) return;

    duration -= Gdx.graphics.getDeltaTime();

    if (duration <= 0) showUpgrade = false;

    font.setColor(1, 1, 1, duration / 2);

    drawNotification(upgradeText);
  }

  /** Checks and creates the string to display if the client has just picked up an upgrade */
  private void checkUpgradePickup() {
    if (store.getMainPlayer().getLastUpgradeText() == null) return;

    duration = NOTIFICATION_DURATION;
    showUpgrade = true;
    upgradeText = store.getMainPlayer().getLastUpgradeText();

    store.getMainPlayer().setLastUpgradeText(null);
  }
}
