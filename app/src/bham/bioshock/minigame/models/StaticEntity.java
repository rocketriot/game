package bham.bioshock.minigame.models;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class StaticEntity extends Entity {

    public StaticEntity(World w, float x, float y, EntityType type) {
      super(w, x, y, true, type);
    }

    public abstract TextureRegion getTexture();
    
    public void setPosition(float x, float y){
        this.pos.x = x;
        this.pos.y = y;
    }

}
