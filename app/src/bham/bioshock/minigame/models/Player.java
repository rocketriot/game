package bham.bioshock.minigame.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import bham.bioshock.minigame.PlayerTexture;

public class Player extends Entity {
	
	private static Animation<TextureRegion> walkAnimation;
	private static TextureRegion frontTexture;
	private static final int FRAMES = 11;
		
	private PlayerTexture dir;
	float animationTime;
	
	private final double JUMP_FORCE = 300;
	private float v = 20f;
	
	public Player(float x, float y) {
		super(x, y);
		SIZE = 100;
		animationTime = 0;
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
		animationTime += delta;
		dir = PlayerTexture.FRONT;
	}
	
	public TextureRegion getTexture() {
		if(dir == PlayerTexture.FRONT) {
			return frontTexture;
		}
		TextureRegion region = walkAnimation.getKeyFrame(animationTime, true);
		if(region.isFlipX()) {
			region.flip(true, false);
		}
		if (dir == PlayerTexture.LEFT) {
			region.flip(true, false);				
			return region;
		}
		return region;
	}

	public static void loadTextures() {
		Texture walkSheet = new Texture(Gdx.files.internal("app/assets/minigame/astronaut.png"));
		
		TextureRegion[][] tmp = TextureRegion.split(walkSheet, walkSheet.getWidth() / FRAMES, walkSheet.getHeight());
				
		TextureRegion[] walkFrames = new TextureRegion[FRAMES - 1];
		int index = 0;
		frontTexture = tmp[0][0];
		for (int i = 1; i < FRAMES; i++) {
			walkFrames[index++] = tmp[0][i];
		}
		
		walkAnimation = new Animation<TextureRegion>(0.1f, walkFrames);
	}

	
}
