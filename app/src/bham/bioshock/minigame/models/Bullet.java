package bham.bioshock.minigame.models;

import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Bullet extends Entity {
    private TextureRegion texture;
    public boolean isFired = false;

    public Bullet(World w, float x, float y) {
        super(w,x, y);
        size = 100;
    }

    public void loadTexture() {
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public void load() {
        loadTexture();
        super.load();
        sprite.setOrigin(sprite.getWidth() / 2, 0);

    }


}
