package bham.bioshock.minigame.models;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.physics.CollisionBoundary;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public abstract class StaticEntity {

    protected Position pos;
    protected World world;
    protected float rotation;
    protected Sprite sprite;
    protected int size = 100;
    protected float fromGround;
    protected CollisionBoundary collisionBoundary;
    protected float collisionWidth = 100;
    protected float collisionHeight = 100;
    private boolean loaded = false;
    protected SpeedVector speed;

    protected final double GROUND_FRICTION = 0.2;

    public StaticEntity(World w, float x, float y) {
        pos = new Position(x, y);
        world = w;
        fromGround = 0;
        speed = new SpeedVector();

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
/*
    public double distanceFromGround() {
        double dx = getX() - world.gravityCenter().x;
        double dy = getY() - world.gravityCenter().y;

        double toCenter = Math.sqrt(dx * dx + dy * dy);
        return toCenter - (world.getPlanetRadius() + fromGround);
    }
    */
    public abstract TextureRegion getTexture();
    public double angleToCenterOfGravity() {
        return 180 + angleFromCenter();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public int getSize() {
        return size;
    }
    public double angleFromCenter() {
        return world.getAngleTo(getX(), getY());
    }

    public void load() {
        this.loaded = true;
        sprite = new Sprite(getTexture());
        sprite.setSize(getSize()/2, getSize());
        sprite.setOrigin(sprite.getWidth() / 2, 0);

        collisionWidth = sprite.getWidth();
        collisionHeight = sprite.getHeight();
        collisionBoundary = new CollisionBoundary(collisionHeight,collisionWidth);
        collisionBoundary.update(pos, getRotation());
       
    }

    public boolean checkCollision(Entity e) {
        if(collisionBoundary.collideWith(e.collisionBoundary) ) {
            System.out.println("col");
            return true;
        }
        return false;
    }

    public void setPosition(float x, float y){
        this.pos.x = x;
        this.pos.y = y;

    }


    public double getRotation() {
        return rotation - angleFromCenter();
    }
    public CollisionBoundary collisionBoundary() {
        return collisionBoundary;
    }


    public void handleCollision(Entity e) {
        double speedAngle = e.speed.getSpeedAngle();
        e.speed.stop(speedAngle);
        e.speed.apply(-speedAngle, 20);
    }
}
