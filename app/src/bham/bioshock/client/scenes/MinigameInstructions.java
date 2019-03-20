package bham.bioshock.client.scenes;

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
  private BitmapFont timerFont;
  private boolean showPrompt = false;
  private boolean showPrompt2 = false;
  private float duration;
  private float duration2;
  private boolean displayed;
  private FontGenerator fontGenerator;
  private int fontSize = 72;
  private float timer = 0;


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
    timerFont = fontGenerator.generate(fontSize, Color.YELLOW);
  }

  /**
   * Render the instructions once at the beginning
   * Render the timer for the game
   * Render message for the last 10 seconds
   */
  public void render() {
    checkIfDisplayed();
    displayTimer();
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

  /**
   * Displays timer for the minigame.
   */
  private void displayTimer() {
    int xTimer = Config.GAME_WORLD_WIDTH / 2 - 100;
    int yTimer = Config.GAME_WORLD_HEIGHT - 100;

    timer += Gdx.graphics.getDeltaTime();
    float displayedTime = 60 - timer;


    DecimalFormat df = new DecimalFormat("##");

    String displayedSeconds = df.format(displayedTime);

    if(displayedSeconds.length() <2)
      displayedSeconds = "0" + displayedSeconds;


    batch.begin();
    timerFont.draw(batch, "00:" + displayedSeconds, xTimer, yTimer);

    if(displayedTime <=10)
      showPrompt2 =true;
    batch.end();
  }
}


