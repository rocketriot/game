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

		// change the border when the entity is moved
		calculateFullBorder();

		if(isFlying()) {
			speed.apply(angle, World.GRAVITY*delta);			
		} else {
			speed.stop(angleFromCenter);
		}
		
		if(!isFlying()) {
			speed.friction(angleFromCenter, GROUND_FRICTION);						
		}

	}

	private Position[] calculateBorderCorners(){
		float width = sprite.getWidth();
		float height = sprite.getHeight();
		// bottom-right corner
		Position pos2 = new Position(sprite.getX() + width, sprite.getY());

		//upper-left corner
		Position pos3 = new Position(sprite.getX(), sprite.getY() + height);

		//upper-right corner
		Position pos4 = new Position(sprite.getX() + width,sprite.getY() + height);

		Position[] border = new Position[4];
		border[0]= pos;
		border[1] = pos2;
		border[2]=pos3;
		border[3]=pos4;

		return border;
	}

	private ArrayList<Position> calculateFullBorder(){
		Position[] border = calculateBorderCorners();

		// get bottom line - from bottom-left to bottom-right corner
		for(float i = pos.x; i<= border[1].x;i++ ){
			Position current = new Position(i, pos.y);
			fullBorder.add(current);
		}

		// get left line - from bottom-left to upper-left corner
		for(float i = pos.y; i<= border[2].y;i++ ){
			Position current = new Position(pos.x, i);
			fullBorder.add(current);
		}

		// get upper line - from upper-left to upper-right corner
		for(float i = border[2].x; i<= border[3].x;i++ ){
			Position current = new Position(i, border[2].y);
			fullBorder.add(current);
		}

		// get left line - from upper-right to bottom-right corner
		for(float i = border[3].y; i<= border[1].y;i++ ){
			Position current = new Position(border[1].x, i);
			fullBorder.add(current);
		}
	}

	public ArrayList<Position> getBorder(){
		return this.fullBorder;
	}


}
