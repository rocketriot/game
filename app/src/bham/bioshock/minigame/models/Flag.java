package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Flag extends Entity {

    private static TextureRegion texture;

    public Flag(World w, float x, float y, boolean isStatic) {
        super(w, x, y, isStatic);
        setRotation(0);
        onGround = true;
    }

    @Override
    public TextureRegion getTexture() {
        return texture;
    }

    public static void loadTextures() {
        texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/flag.png")));
    }
}
