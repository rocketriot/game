package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Goal extends Entity {
    private static TextureRegion texture;

    public Goal(World w, float x, float y, boolean isStatic, EntityType type) {

        super(w, x, y, isStatic, EntityType.GOAL);
        setRotation(0);
    }


    @Override
    public TextureRegion getTexture() {
        return texture;
    }

    public static void loadTextures() {
        texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/minigame/flag.png")));
    }
}
