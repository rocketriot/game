package bham.bioshock.minigame.models;

import bham.bioshock.client.Assets;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Goal extends Entity {
    //private static TextureRegion texture;
    private static Animation<TextureRegion> blackHoleAnimation;
    private float animationTime;


    public Goal(World w, float x, float y, boolean isStatic, EntityType type) {

        super(w, x, y, isStatic, EntityType.GOAL);
        setRotation(0);
        animationTime= 0;
        width= 180;
        height= 180;

    }


    @Override
    public TextureRegion getTexture() {
        return blackHoleAnimation.getKeyFrame(animationTime,true);
    }

    public static void createTextures(AssetManager manager) {
        TextureRegion[][] textureR = Assets.splittedTexture(manager, Assets.blackHoleAnimationSheet, 8);
        blackHoleAnimation = Assets.textureToAnimation(textureR, 8, 0, 0.1f);
    }

    public static void loadTextures(AssetManager manager) {
        manager.load(Assets.blackHoleAnimationSheet, Texture.class);


   /* Texture sheet = new Texture(Gdx.files.internal(Assets.blackHoleAnimationSheet));

        TextureRegion[] textureRegion = new TextureRegion[8];
        TextureRegion[][] tmp =
                TextureRegion.split(sheet, sheet.getWidth() / 8, sheet.getHeight());

        int index = 0;
        for (int j = 0; j < 8; j++) {
            textureRegion[index++] = tmp[0][j];
        }


        //blackHoleAnimation = new Animation<TextureRegion>(0.1f, textureRegion);
*/
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        animationTime += delta;
    }
}
