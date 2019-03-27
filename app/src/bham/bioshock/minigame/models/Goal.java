package bham.bioshock.minigame.models;

import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Goal object represents the final position for the platformer objective
 */
public class Goal extends Entity {
  private static Animation<TextureRegion> blackHoleAnimation;
  private float animationTime;

  private static final long serialVersionUID = -8342563560891277870L;

  /**
   * Creates a new goal at specified position
   * 
   * @param w
   * @param x
   * @param y
   * @param isStatic
   * @param type
   */
  public Goal(World w, float x, float y) {
    super(w, x, y, true, EntityType.GOAL);
    setRotation(0);
    animationTime = 0;
    width = 180;
    height = 180;
  }

  @Override
  public TextureRegion getTexture() {
    return blackHoleAnimation.getKeyFrame(animationTime, true);
  }

  public static void createTextures(AssetContainer manager) {
    TextureRegion[][] textureR = Assets.splittedTexture(manager, Assets.blackHoleAnimationSheet, 8);
    blackHoleAnimation = Assets.textureToAnimation(textureR, 8, 0, 0.1f);
  }

  public static void loadTextures(AssetContainer manager) {
    // loaded by the game board
    // manager.load(Assets.blackHoleAnimationSheet, Texture.class, GamePart.MINIGAME);
  }

  @Override
  public void update(float delta) {
    super.update(delta);
    animationTime += delta;
  }
}
