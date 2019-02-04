package bham.bioshock.minigame.models;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import bham.bioshock.minigame.PlayerTexture;

public class Player extends Entity {
		
	private PlayerTexture dir;
	private static HashMap<PlayerTexture, Texture> textures = new HashMap<>();
	
	private final int SIZE = 150;
	
	private float v = 200f;
	
	
	public Player(float x, float y) {
		super(x, y);
		startMove();
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
	
	public void startMove() {
		dir = PlayerTexture.FRONT;
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
