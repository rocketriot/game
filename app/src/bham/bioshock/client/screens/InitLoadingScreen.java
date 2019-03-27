package bham.bioshock.client.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;

public class InitLoadingScreen implements Screen {

  private AssetContainer assets;
  private Router router;
  
  public InitLoadingScreen(Router router, AssetContainer assets) {
    this.assets = assets;
    this.router = router;
  }
  
  public void load() {
    assets.load(Assets.skin, Skin.class, GamePart.MENU);
    assets.load(Assets.menuBackground, Texture.class, GamePart.MENU);
    assets.load(Assets.gameBackground, Texture.class, GamePart.MENU);
    assets.load(Assets.loading, Texture.class, GamePart.MENU);
    assets.load(Assets.cursor, Pixmap.class, GamePart.MENU);
    assets.load(Assets.logo, Texture.class, GamePart.MENU);
    
    // Menu buttons
    assets.load(Assets.hostButton, Texture.class, GamePart.MENU);
    assets.load(Assets.hostButtonHover, Texture.class, GamePart.MENU);
    
    assets.load(Assets.joinButton, Texture.class, GamePart.MENU);
    assets.load(Assets.joinButtonHover, Texture.class, GamePart.MENU);
    
    assets.load(Assets.howToPlayButton, Texture.class, GamePart.MENU);
    assets.load(Assets.howToPlayButtonHover, Texture.class, GamePart.MENU);
    
    assets.load(Assets.preferencesButton, Texture.class, GamePart.MENU);
    assets.load(Assets.preferencesButtonHover, Texture.class, GamePart.MENU);
    
    assets.load(Assets.exitButton, Texture.class, GamePart.MENU);
    assets.load(Assets.exitButtonHover, Texture.class, GamePart.MENU);
  }
  
  public void loaded() {
    // Setup cursor
    Pixmap pm = assets.get(Assets.cursor, Pixmap.class);
    Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
    assets.unload(Assets.cursor);
    
    
    router.call(Route.MAIN_MENU);
  }
  
  
  @Override
  public void show() {
    this.load();
  }

  @Override
  public void render(float delta) {
    boolean finished = assets.update();
    System.out.println(assets.getProgress());
    if(finished) {
      this.loaded();
      return;
    }
  }

  @Override
  public void resize(int width, int height) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub
    
  }

  
  
}
