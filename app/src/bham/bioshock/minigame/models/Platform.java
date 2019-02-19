package bham.bioshock.minigame.models;


import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class Platform extends StaticEntity {

    private Integer color;
    private TextureRegion texture;


    public Platform(World w, float x, float y) {
        super(w,x, y);
        size = 100;
    }



    public TextureRegion getTexture() {
       return texture;
    }

    public void load() {
        texture =new TextureRegion( new Texture(Gdx.files.internal("app/assets/minigame/platform.png")));
        super.load();
        sprite.setOrigin(sprite.getWidth() / 2, 0);

    }


}
