package bham.bioshock.minigame.models;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.World;
import bham.bioshock.minigame.physics.Gravity;
import bham.bioshock.minigame.physics.SpeedVector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;


public abstract class Entity {
		
	protected int SIZE = 50;
	protected final double GROUND_FRICTION = 0.2;
	private final double AIR_FRICTION = 0.001;
	
	protected Position pos;
	protected boolean loaded = false;
	protected Sprite sprite;
	private float rotation;
	protected float fromGround;
	
	protected SpeedVector speed;

	protected ArrayList<Position> fullBorder;
	
	public Entity(float x, float y) {
		pos = new Position(x, y);
		speed = new SpeedVector();
		fromGround = 0;
	}
	
	public Entity() {
		this(0f, 0f);
	}
	
	public int getSize() {
		return SIZE;
	}

	public Position getPos() {
		return pos;
	}
	
	public float getX() {
		return pos.x;
	}
	
	public float getY() {
		return pos.y;
	}
	
	public boolean isFlying() {
		return distanceFromGround() > 10;
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	public double distanceFromGround() {
		double dx = getX() - World.GRAVITY_POS.x;
		double dy = getY() - World.GRAVITY_POS.y;
		
		double toCenter = Math.sqrt(dx*dx + dy*dy);
		return toCenter - (World.PLANET_RADIUS + fromGround);
	}
	
	public double angleToCenterOfGravity() {
		return 180 + angleFromCenter();
	}
	
	public double getRotation() {
		return -angleFromCenter() + rotation;
	}
	
	public double angleFromCenter() {
		return Gravity.getAngleTo(getX(), getY());
	}
	

	public abstract TextureRegion getTexture();

	public void load() {
		sprite = new Sprite(getTexture());
		sprite.setSize(getSize(), getSize());
		sprite.setOrigin(0, 0);
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	public void setSpeed(float angle, float force) {
		speed.apply(angle, force);
	}
	
	public void update(float delta) {
		double angle = angleToCenterOfGravity();
		double angleFromCenter = angleFromCenter();

		pos.y += speed.dY()*delta;
		pos.x += speed.dX()*delta;

		if(isFlying()) {
			speed.apply(angle, World.GRAVITY*delta);			
		} else {
			speed.stop(angleFromCenter);
		}
		
		if(!isFlying()) {
			speed.friction(GROUND_FRICTION);						
		}
		
	}


	public void col(float delta) {
		double angle = angleToCenterOfGravity();
		double angleFromCenter = angleFromCenter();


			speed.stop(angleFromCenter);

	}
	// returns rectangle of the sprite
	public Rectangle getRectangle(){
		return sprite.getBoundingRectangle();
	}




}
