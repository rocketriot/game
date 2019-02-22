package bham.bioshock.client.scenes.gameboard;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import bham.bioshock.client.Assets;
import bham.bioshock.common.models.Player;

public class DrawPlayer extends DrawEntity {
    ArrayList<Sprite> sprites = new ArrayList<>(); 
    ArrayList<Sprite> outlinedSprites = new ArrayList<>(); 

    public DrawPlayer(Batch batch) {
        super(batch);

        sprites = generateSprites(Assets.playersFolder);
        outlinedSprites = generateSprites(Assets.playersFolder);
    }

    public void draw(Player player, int PPS, boolean selected) {
      Sprite sprite = selected 
        ? outlinedSprites.get(player.getTextureID())
        : sprites.get(player.getTextureID());

      sprite.setX(player.getCoordinates().getX() * PPS);
      sprite.setY(player.getCoordinates().getY() * PPS);
      sprite.draw(batch);
    }

    public void resize(int PPS) {
        sprites.forEach(sprite -> sprite.setSize(PPS, PPS));
        outlinedSprites.forEach(sprite -> sprite.setSize(PPS, PPS));
    }
    
    public void dispose() {
        sprites.forEach(sprite -> sprite.getTexture().dispose());
        outlinedSprites.forEach(sprite -> sprite.getTexture().dispose());
    }
}
