package bham.bioshock.minigame.models;

import bham.bioshock.client.assets.Assets.GamePart;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The flag is a target for players while capture the flag objective.
 * It can be carried by the player and is dropped when the player is killed
 */
public class Flag extends StaticEntity {

  private static final long serialVersionUID = -374379982329919955L;

  /**
   * The texture.
   */
  public static TextureRegion texture;

  /**
   * The owner of the flag.
   */
  private Entity owner = null;

  /**
   * Instantiates a new Flag.
   *
   * @param w the w
   * @param x the x
   * @param y the y
   */
  public Flag(World w, float x, float y) {
    super(w, x, y, EntityType.FLAG);
  }

  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  /**
   * Update the position of the flag.
   * If it has an owner, adjust to owner position
   */
  @Override
  public void update(float delta) {
    if (owner != null) {
      PlanetPosition pp = owner.getPlanetPos();
      pp.fromCenter += height + 150;
      Position p = world.convert(pp);

      this.pos.x = p.x;
      this.pos.y = p.y;
    }
  }

  /**
   * Sets owner.
   *
   * @param astronaut the astronaut owner
   */
  public void setOwner(Astronaut astronaut) {
    this.owner = astronaut;
  }

  /**
   * Remove owner.
   */
  public void removeOwner() {
    this.owner = null;
  }

  /**
   * Returns whether the flag has an owner or not.
   *
   * @return whether the flag has an owner or not.
   */
  public boolean haveOwner() {
    return owner != null;
  }

  /**
   * Queue textures for loading
   *
   * @param manager the asset manager
   */
  public static void loadTextures(AssetContainer manager) {
    manager.load(Assets.flag, Texture.class, GamePart.MINIGAME);
  }

  /**
   * Create textures for rendering
   *
   * @param manager the asset manager
   */
  public static void createTextures(AssetContainer manager) {
    texture = new TextureRegion(manager.get(Assets.flag, Texture.class));
  }
}
