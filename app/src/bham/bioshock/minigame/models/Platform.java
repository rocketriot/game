package bham.bioshock.minigame.models;


import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Platform extends StaticEntity {

    private TextureRegion texture;

    public Platform(World w, float x, float y) {
        super(w ,x, y);
        width = 100;
        height = 50;
        collisionWidth = 100;
        collisionHeight = 50;
    }

    public TextureRegion getTexture() {
       return texture;
    }

    @Override
    public Player getShooter() {
        return null;
    }

    public void load() {
        texture = new TextureRegion( new Texture(Gdx.files.internal("app/assets/minigame/platform.png")));
        super.load();
    }


}
