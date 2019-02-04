package bham.bioshock.minigame.models;

import com.badlogic.gdx.graphics.Texture;

import bham.bioshock.common.Position;

public abstract class Entity {
		
	protected int SIZE = 150;
	
	protected Position pos;
	protected boolean loaded = false;
	
	public Entity(float x, float y) {
		pos = new Position(x, y);
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
	
	
	public abstract Texture getTexture();
}
