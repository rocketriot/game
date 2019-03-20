package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Flag extends Entity {

  private static final long serialVersionUID = -374379982329919955L;
  
  public static TextureRegion texture;

  private Entity owner = null;
  
  public Flag(World w, float x, float y) {
    super(w, x, y, true, EntityType.FLAG);
    setRotation(0);
  }

  @Override
  public TextureRegion getTexture() {
    return texture;
  }

  public static void loadTextures() {
    texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/flag.png")));
  }
  
  @Override
  public void draw(SpriteBatch batch) {
    if(owner == null) return;
    
    PlanetPosition pp = owner.getPlanetPos();
    pp.fromCenter += height + 60;
    Position p = world.convert(pp);

    this.pos.x = p.x;
    this.pos.y = p.y;
    super.draw(batch);
  }
  
  public void setOwner(Astronaut astronaut) {
    this.owner = astronaut;
  }
  
  public void removeOwner() {
    this.owner = null;
  }
}
