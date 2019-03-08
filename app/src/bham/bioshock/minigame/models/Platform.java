package bham.bioshock.minigame.models;


import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Platform extends StaticEntity {

    private TextureRegion texture;

    public Platform(World w, float x, float y, int width, int height) {
        super(w ,x, y, EntityType.PLATFORM);
        this.width = width;
        this.height = height;
        collisionWidth = width;
        collisionHeight = height;
    }

    public TextureRegion getTexture() {
       return texture;
    }

    public void load() {
        texture = new TextureRegion( new Texture(Gdx.files.internal("app/assets/minigame/platform.png")));
        super.load();
    }


}
