package bham.bioshock.minigame.models;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;

public class Platform extends Entity {

    private Integer color;
    private static HashMap<Integer, Texture> textures = new HashMap<>();

    public Platform(float _x, float _y) {
        super(_x, _y);
    }

    public Texture getTexture() {
        return textures.get(color);
    }


}
