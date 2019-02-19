package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.physics.Gravity;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class StaticEntity {

    protected Position pos;
    protected Gravity gravity;
    protected World world;
    protected float rotation;
    protected Sprite sprite;
    protected int size = 50;
    protected float fromGround;

    public StaticEntity(World w, float x, float y) {
        pos = new Position(x, y);
        gravity = new Gravity(w);
        world = w;
        fromGround = 0;

    }

    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public double distanceFromGround() {
        double dx = getX() - world.gravityCenter().x;
        double dy = getY() - world.gravityCenter().y;

        double toCenter = Math.sqrt(dx * dx + dy * dy);
        return toCenter - (world.getPlanetRadius() + fromGround);
    }
    public abstract TextureRegion getTexture();

    public Sprite getSprite() {
        return sprite;
    }

    public int getSize() {
        return size;
    }


    public void load() {
        sprite = new Sprite(getTexture());
        sprite.setSize(getSize()/2, getSize());
        sprite.setOrigin(0, 0);
    }
}
