package bham.bioshock.minigame.models;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.physics.SpeedVector;

public class Player extends Entity {
		
	private PlayerTexture dir;
	private static HashMap<PlayerTexture, Texture> textures = new HashMap<>();
	
	private final double GRAVITY = 0.8;
	private final double JUMP_FORCE = 25;
	private float v = 350f;
	
	private boolean flying = false;
	private SpeedVector speed;
	
	
	public Player(float x, float y) {
		super(x, y);
		SIZE = 150;
		speed = new SpeedVector();
		update(0);
	}
	
	public Player() {
		this(0f, 0f);
	}
	
	public void moveLeft(float delta) {
		pos.x -= v * delta;
		dir = PlayerTexture.LEFT;
	}
	
	public void moveRight(float delta) {
		pos.x += v * delta;
		dir = PlayerTexture.RIGHT;
	}
	
	public void jump(float delta) {
		if(!flying) {
			speed.apply(0, JUMP_FORCE);			
		}
		flying = true;
	}
	
	public void update(float delta) {
		if(pos.y >= 0) {
			speed.apply(0, -GRAVITY);
		} else {
			speed.stop();
			pos.y = 0;
		}
		pos.y += speed.dY();
		dir = PlayerTexture.FRONT;
		if(flying) {
			pos.y -= v*delta;
			if(pos.y <= 0f) {
				flying = false;
				pos.y = 0f;
			}
		}
	}
	
	public static void loadTextures() {
		textures.put(PlayerTexture.LEFT, new Texture(Gdx.files.internal("app/assets/minigame/left.png")));
		textures.put(PlayerTexture.RIGHT, new Texture(Gdx.files.internal("app/assets/minigame/right.png")));
		textures.put(PlayerTexture.FRONT, new Texture(Gdx.files.internal("app/assets/minigame/face.png")));
	}
	
	public static Texture getTexture(PlayerTexture pt) {
		return textures.get(pt);
	}
	
	public Texture getTexture() {
		return getTexture(dir);
	}
}
