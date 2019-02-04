package bham.bioshock.minigame.models;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;


public class Rocket extends Entity {
	
	private Integer color;
	private static HashMap<Integer, Texture> textures = new HashMap<>();
	
	public Rocket(float _x, float _y, int _color) {
		super(_x, _y);
		color = _color;
		SIZE = 650;
	}
	
	public static void loadTextures() {
		textures.put(1, new Texture(Gdx.files.internal("app/assets/entities/rockets/1.png")));
		textures.put(2, new Texture(Gdx.files.internal("app/assets/entities/rockets/2.png")));
		textures.put(3, new Texture(Gdx.files.internal("app/assets/entities/rockets/3.png")));
		textures.put(4, new Texture(Gdx.files.internal("app/assets/entities/rockets/4.png")));
	}
	
	public Texture getTexture() {
		return textures.get(color);
	}

}
