package bham.bioshock.client;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets {
  // General
  public static final String skin = "app/assets/skin/skin.json";
  public static final String font = "app/assets/fonts/font.otf";
  public static final String cursor = "app/assets/ui/cursor.png";

  // Backgrounds
  public static final String gameBackground = "app/assets/backgrounds/game.png";
  public static final String menuBackground = "app/assets/backgrounds/menu.png";
  
  // Main Menu
  public static final String logo = "app/assets/ui/main-menu/logo.png";
  public static final String hostButton = "app/assets/ui/main-menu/host-game.png";
  public static final String hostButtonHover = "app/assets/ui/main-menu/host-game-hover.png";
  public static final String joinButton = "app/assets/ui/main-menu/join-game.png";
  public static final String joinButtonHover = "app/assets/ui/main-menu/join-game-hover.png";
  public static final String preferencesButton = "app/assets/ui/main-menu/preferences.png";
  public static final String preferencesButtonHover = "app/assets/ui/main-menu/preferences-hover.png";
  public static final String howToPlayButton = "app/assets/ui/main-menu/how-to-play.png";
  public static final String howToPlayButtonHover = "app/assets/ui/main-menu/how-to-play-hover.png";
  public static final String exitButton = "app/assets/ui/main-menu/exit.png";
  public static final String exitButtonHover = "app/assets/ui/main-menu/exit-hover.png";

  // Game Board assets
  public static final String planetsFolder = "app/assets/entities/planets";
  public static final String flagsFolder = "app/assets/entities/flags";
  public static final String playersFolder = "app/assets/entities/players";
  public static final String asteroidsFolder = "app/assets/entities/asteroids";
  public static final String blackholesFolder = "app/assets/entities/blackHoles";
  public static final String fuel = "app/assets/entities/fuel.png";
  public static final String upgrade = "app/assets/entities/upgrade.png";
  public static final String particleEffectsFolder = "app/assets/particle-effects";
  public static final String particleEffect = "app/assets/particle-effects/rocket-trail.p";
  
  // General HUD assets
  public static final String pauseIcon = "app/assets/ui/pause.png";  
  
  // Gameboard HUD assets
  public static final String turnPointer = "app/assets/ui/turn-pointer.png";

  // Minigame HUD assets
  public static final String gun = "app/assets/minigame/gun.png";

  // Minigame Astronauts
  public static final String hearts = "app/assets/minigame/hearts.png";
  public static final String astroBase = "app/assets/minigame/astronauts/";
  public static final String astroWalk = "/astro.png";
  public static final String astroGun = "/astro_gun.png";
  public static final String astroFall = "/fall.png";
  public static final String astroFFall = "/ffall.png";
  public static final String astroShield = "/shield.png";
  
  
  public static TextureRegion[][] splittedTexture(AssetManager manager, String path, int fnum) {
    Texture t = manager.get(path, Texture.class);
    return TextureRegion.split(t, t.getWidth() / fnum, t.getHeight());
  }

  public static Animation<TextureRegion> textureToAnimation(TextureRegion[][] list, int fnum, int skip, float duration) {
    TextureRegion[] frames = new TextureRegion[fnum - skip];
    for (int i = skip; i < fnum; i++) {
      frames[i - skip] = list[0][i];
    }
    return new Animation<TextureRegion>(duration, frames);
  }
  
  // Minigame world
  public static final String planetBase = "app/assets/minigame/planets/";
  public static final String platformsBase = "app/assets/minigame/platforms/";
}
