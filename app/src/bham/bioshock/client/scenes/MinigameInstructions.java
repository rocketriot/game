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
  private boolean showPrompt = false;
  private float duration;
  private boolean displayed;
  private FontGenerator fontGenerator;
  private int fontSize = 72;
  private float timer = 0;
  private int initialMinute =2;


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
    int xTimer = Config.GAME_WORLD_WIDTH / 2;
    int yTimer = Config.GAME_WORLD_HEIGHT;

    timer += Gdx.graphics.getDeltaTime();
    float displayedTime = 60 - timer;
    System.out.println(displayedTime);

    if(timer > 59){
      initialMinute --;
      timer =1;
    }

    DecimalFormat df = new DecimalFormat("##");

    String displayedSeconds = df.format(displayedTime);

    if(displayedSeconds.length() ==1 )
      displayedSeconds = "0" + displayedSeconds;

    batch.begin();
    font.draw(batch, "0" + initialMinute +":" + displayedSeconds, xTimer, yTimer);
    batch.end();
  }
}


