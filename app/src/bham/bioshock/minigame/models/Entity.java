package bham.bioshock.minigame.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.World;
import bham.bioshock.minigame.physics.SpeedVector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Arrays;


public abstract class Entity {
		
	protected int SIZE = 50;
	private final double GROUND_FRICTION = 0.2;
	private final double AIR_FRICTION = 0.01;
	
	protected Position pos;
	protected boolean loaded = false;
	protected Sprite sprite;
	private float rotation;
	
	protected SpeedVector speed;

	protected ArrayList<Position> fullBorder;
	
	public Entity(float x, float y) {
		pos = new Position(x, y);
		speed = new SpeedVector();
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
		return toCenter - World.PLANET_RADIUS;
	}
	
	public double angleToCenterOfGravity() {
		return 180 + angleFromCenter();
	}
	
	public void updateRotation() {
		sprite.setRotation((float) -angleFromCenter() + rotation);
	}
	
	public double angleFromCenter() {
		double dx = getX() - World.GRAVITY_POS.x;
		double dy = getY() - World.GRAVITY_POS.y;
		double distance = distanceFromGround() + World.PLANET_RADIUS;

		if(dy > 0) {
			return Math.toDegrees( Math.asin(dx/distance) );			
		} else {
			return 180 - Math.toDegrees( Math.asin(dx/distance) );	
		}
	}
	
	public abstract Texture getTexture();
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
			speed.friction(angleFromCenter, GROUND_FRICTION);						
		}

	}

	// returns rectangle of the sprite
	public Rectangle getRectangle(){
		return sprite.getBoundingRectangle();
	}




}
