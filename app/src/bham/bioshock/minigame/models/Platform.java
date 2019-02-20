package bham.bioshock.minigame.models;


import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class Platform extends Entity {

    private Integer color;
    private static HashMap<Integer, Texture> textures = new HashMap<>();

    public Platform(World w, float x, float y) {
        super(w, x, y);
    }

    public TextureRegion getTexture() {
       return null;
    }


}
