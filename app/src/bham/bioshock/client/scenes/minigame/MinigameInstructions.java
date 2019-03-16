package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.FontGenerator;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.text.DecimalFormat;

public class MinigameInstructions {

  private SpriteBatch batch;
  private Store store;
  private BitmapFont font;
  private boolean showPrompt = false;
  private boolean showPrompt2 = false;
  private float duration;
  private float duration2;
  private boolean displayed;
  private FontGenerator fontGenerator;
  private int fontSize = 72;


  public MinigameInstructions(SpriteBatch batch, Store store) {
    this.batch = batch;
    this.store = store;
    this.setup();
  }

  /**
   * Generate the font
   */
  private void setup() {
    fontGenerator = new FontGenerator();
    font = fontGenerator.generate(fontSize, Color.WHITE);
  }

  /**
   * Render the instructions once at the beginning
   * Render the timer for the game
   * Render message for the last 10 seconds
   */
  public void render() {
    checkIfDisplayed();
    if (showPrompt) {
      batch.begin();
      int x = Config.GAME_WORLD_WIDTH / 8;
      int y = Config.GAME_WORLD_HEIGHT / 2;

      String instructions = store.getMinigameStore().getObjective().instructions();
      font.draw(batch, instructions, x, y);
      batch.end();

      duration -= Gdx.graphics.getDeltaTime();

      if (duration <= 0) {
        showPrompt = false;
      }
    }

    if(showPrompt2){
      batch.begin();

      int x = Config.GAME_WORLD_WIDTH / 8;
      int y = Config.GAME_WORLD_HEIGHT / 2;

      font.draw(batch, "Hurry up! \n Only 10 seconds left!", x, y);
      batch.end();

      duration2  -= Gdx.graphics.getDeltaTime();

      if (duration2 <= 0) {
        showPrompt2 = false;
      }
    }
  }


  /**
   * Checks if the minigame had started
   * If yes sets the showpromt to true so the instructions will be displayed in render
   */
  private void checkIfDisplayed() {
    if (!displayed && store.getMinigameStore().isStarted()) {
      duration = 3f;
      displayed = true;
      showPrompt = true;
    }
  }
}


