package bham.bioshock.client.interfaces;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import bham.bioshock.client.assets.Assets.GameType;

public interface AssetContainer {

  /**
   * Queue an asset for loading for certain game
   * 
   * @param string
   * @param class1
   */
  <T> void load(String string, Class<T> c, GameType game);

  /**
   * Get requested asset
   * @param <T>
   * 
   * @param string
   * @param class1
   * @return
   */
  <T> T get(String string, Class<T> c);

  /**
   * Continue loading
   * 
   * @return
   */
  boolean update();

  /**
   * Get assets loading progress
   * 
   * @return progress
   */
  float getProgress();

  /**
   * Dispose all assets for this game type
   * 
   * @param minigame
   */
  void dispose(GameType minigame);

  /**
   * Returns the font of the provided size
   * 
   * @param fontSize
   * @return bitmap font
   */
  BitmapFont getFont(int fontSize);
  
  /**
   * Returns application skin
   * 
   * @return skin
   */
  Skin getSkin();
}
