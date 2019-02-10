package bham.bioshock.minigame.models;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import bham.bioshock.minigame.PlayerTexture;

public class Player extends Entity {
		
	private PlayerTexture dir;
	private static HashMap<PlayerTexture, Texture> textures = new HashMap<>();
	
	private final double JUMP_FORCE = 300;

	private float v = 40f;
	
	public Player(float x, float y) {
		super(x, y);
		SIZE = 50;
		update(0);
	}
	
	public Player() {
		this(0f, 0f);
	}
	
	public void moveLeft(float delta) {
		if(!isFlying()) {
			speed.apply(angleFromCenter() + 270, v);	
		}
		dir = PlayerTexture.LEFT;
	}
	
	public void moveRight(float delta) {
		if(!isFlying()) {
			speed.apply(angleFromCenter() + 90, v);	
		}
		dir = PlayerTexture.RIGHT;
	}
	
	public void jump(float delta) {
		if(!isFlying()) {
			speed.apply(angleFromCenter(), JUMP_FORCE);			
		}
	}
	
	public void update(float delta) {
		super.update(delta);
		dir = PlayerTexture.FRONT;
	}
	
	public Texture getTexture() {
		return textures.get(dir);
	}

	public static void loadTextures() {
		textures.put(PlayerTexture.LEFT, new Texture(Gdx.files.internal("app/assets/minigame/left.png")));
		textures.put(PlayerTexture.RIGHT, new Texture(Gdx.files.internal("app/assets/minigame/right.png")));
		textures.put(PlayerTexture.FRONT, new Texture(Gdx.files.internal("app/assets/minigame/face.png")));
	}

	
}
