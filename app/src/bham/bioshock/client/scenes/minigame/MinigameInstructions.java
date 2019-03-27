package bham.bioshock.client.scenes.minigame;

import bham.bioshock.Config;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Renders minigame instructions defined by the objective
 */
public class MinigameInstructions {

  private SpriteBatch batch;
  private Store store;
  private BitmapFont font;
  private boolean showPrompt = false;
  private float duration;
  private boolean displayed;
  private int fontSize = 72;
  private AssetContainer assets;

  /**
   * Create minigame instructions container
   * 
   * @param batch
   * @param store
   */
  public MinigameInstructions(SpriteBatch batch, Store store, AssetContainer assets) {
    this.assets = assets;
    this.batch = batch;
    this.store = store;
    this.setup();
  }

  /** Generate the font */
  private void setup() {
    font = assets.getFont(fontSize);
  }

  /**
   * Render the instructions once at the beginning Render the timer for the game Render message for
   * the last 10 seconds
   */
  public void render() {
    checkIfDisplayed();

    if (!showPrompt) return;

    duration -= Gdx.graphics.getDeltaTime();

    if (duration <= 0)
      showPrompt = false;
  
    String instructions = store.getMinigameStore().getObjective().instructions();
    
    int xOffset = (int) fontGenerator.getOffset(font, instructions);
    int x = Config.GAME_WORLD_WIDTH / 2 - xOffset;
    int y = Config.GAME_WORLD_HEIGHT / 2;
  
    batch.begin();
    font.draw(batch, instructions, x, y);
    batch.end();
  }


  /**
   * Checks if the minigame had started If yes sets the showpromt to true so the instructions will
   * be displayed in render
   */
  private void checkIfDisplayed() {
    if (!displayed && store.getMinigameStore().isStarted()) {
      duration = 5f;
      displayed = true;
      showPrompt = true;
    }
  }
}


