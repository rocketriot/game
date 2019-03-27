package bham.bioshock.client.assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import bham.bioshock.client.assets.Assets.GamePart;

public interface AssetContainer {

  /**
   * Queue an asset for loading for certain game
   * 
   * @param string
   * @param class1
   */
  <T> void load(String string, Class<T> c, GamePart game);

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
  void dispose(GamePart minigame);
  
  /**
   * Unload specific texture
   * 
   * @param fileName
   */
  void unload (String fileName);

  /**
   * Returns the font of the provided size
   * 
   * @param fontSize
   * @return bitmap font
   */
  BitmapFont getFont(int fontSize);
  
  /**
   * Generate font with colour
   * 
   * @param i
   * @param white
   * @return
   */
  BitmapFont getFont(int i, Color white);
  
  /**
   * Returns application skin
   * 
   * @return skin
   */
  Skin getSkin();
  
  /**
   * Checks if the file has been already loaded, or is queued for loading
   * 
   * @param fileName
   * @return
   */
  boolean contains (String fileName);
  
  /**
   * Wait for the file
   * 
   * @param fileName
   */
  void finishLoadingAsset(String fileName);
  
  /**
   * If the file is loaded
   * 
   * @param fileName
   * @return
   */
  boolean isLoaded (String fileName);

  
  /**
   * Removes all textures
   */
  void clear();
}
