package bham.bioshock.minigame.models;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.World;
import bham.bioshock.minigame.physics.SpeedVector;

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

	protected Position[] border;
	
	public Entity(float x, float y) {
		pos = new Position(x, y);
		border = new Position[3];
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

		// change the border when the entity is moved
		calculateBorder();

		if(isFlying()) {
			speed.apply(angle, World.GRAVITY*delta);			
		} else {
			speed.stop(angleFromCenter);
		}
		
		if(!isFlying()) {
			speed.friction(angleFromCenter, GROUND_FRICTION);						
		}

	}

	public void calculateBorder(){
		float width = sprite.getWidth();
		float height = sprite.getHeight();
		// bottom-right corner
		Position pos2 = new Position(sprite.getX() + width, sprite.getY());

		//upper-left corner
		Position pos3 = new Position(sprite.getX(), sprite.getY() + height);

		//upper-right corner
		Position pos4 = new Position(sprite.getX() + width,sprite.getY() + height);

		this.border[0]= pos;
		this.border[1] = pos2;
		this.border[2]=pos3;
		this.border[3]=pos4;

	}

}
