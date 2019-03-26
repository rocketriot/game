package bham.bioshock.client.assets;

import java.util.ArrayList;
import java.util.HashMap;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.inject.Singleton;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.assets.Assets.GameType;
import bham.bioshock.client.interfaces.AssetContainer;

@Singleton
public class GameAssetManager extends AssetManager implements AssetContainer {

  private HashMap<GameType, ArrayList<String>> loaded = new HashMap<>();
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
  public <T> void load(String fileName, Class<T> type, GameType gameType) {
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
  public void dispose(GameType minigame) {
    ArrayList<String> assets = loaded.get(minigame);
    
    for(String asset : assets) {
      this.unload(asset);
    }
  }

  @Override
  public BitmapFont getFont(int fontSize) {
    return fontGenerator.generate(fontSize);
  }
}
