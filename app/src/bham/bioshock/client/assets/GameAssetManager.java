package bham.bioshock.client.assets;

import java.util.ArrayList;
import java.util.HashMap;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.inject.Singleton;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.assets.Assets.GamePart;

@Singleton
public class GameAssetManager extends AssetManager implements AssetContainer {

  private HashMap<GamePart, ArrayList<String>> loaded = new HashMap<>();
  private HashMap<Integer, BitmapFont> cachedFonts = new HashMap<>();
  private FontGenerator fontGenerator;
  
  public GameAssetManager() {
    fontGenerator = new FontGenerator();
  }
  
  /**
   * Get game skin
   * 
   * @return skin
   */
  public Skin getSkin() {
    return get(Assets.skin, Skin.class);
  }
  
  /**
   * Load asset for the game type
   */
  public <T> void load(String fileName, Class<T> type, GamePart gameType) {
    super.load(fileName, type);
    ArrayList<String> list = loaded.get(gameType);
    if(list == null) {
      list = new ArrayList<String>();
      list.add(fileName);
      loaded.put(gameType, list);
    } else {
      list.add(fileName);
    }
  }
  
  /**
   * Dispose assets for the game type
   */
  public void dispose(GamePart minigame) {
    ArrayList<String> assets = loaded.get(minigame);
    
    for(String asset : assets) {
      if(this.isLoaded(asset)) {
        this.unload(asset);
      }
    }
  }

  /**
   * Return cached fonts
   */
  @Override
  public BitmapFont getFont(int fontSize) {
    BitmapFont cached = cachedFonts.get(fontSize);
    if(cached != null) {
      return cached;
    }
    
    BitmapFont font = fontGenerator.generate(fontSize);
    cachedFonts.put(fontSize, font);
    return font;
  }

  
  /**
   * Return colorful font
   */
  @Override
  public BitmapFont getFont(int fontSize, Color colour) {
    if(colour.equals(Color.WHITE)) {
      return this.getFont(fontSize);
    }
    return fontGenerator.generate(fontSize, colour);
  }
}
