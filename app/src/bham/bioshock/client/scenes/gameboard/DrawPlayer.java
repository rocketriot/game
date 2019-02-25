package bham.bioshock.client.scenes.gameboard;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import bham.bioshock.client.Assets;
import bham.bioshock.common.Direction;
import bham.bioshock.common.models.Player;

public class DrawPlayer extends DrawEntity {
    ArrayList<Sprite> sprites = new ArrayList<>(); 
    ArrayList<Sprite> outlinedSprites = new ArrayList<>();
    Sprite movingSprite = new Sprite();
    boolean isMoving = false;
    float msXCoords = -1;
    float msYCoords = -1;

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

    public void startMove(Player player, int PPS) {
      isMoving = true;
    }
    
    public void endMove(Player player, int PPS) {
      isMoving = false;
    }


    public void drawMove(Player player, int PPS) {
      ArrayList<Player.Move> boardMove = player.getBoardMove();

      // Setup moving sprite
      movingSprite = sprites.get(player.getTextureID());
      movingSprite.setOriginCenter();

      // Get next available board move
      Player.Move nextMove = boardMove.get(0);

      // If the player is at the starting position set the starting coordinates
      if (nextMove.getDirection() == Direction.NONE) {
        msXCoords = nextMove.getCoordinates().getX();
        msYCoords = nextMove.getCoordinates().getY();

        // Removing starting position from move and get the next move
        boardMove.remove(0);
        nextMove = boardMove.get(0);
      }

      float distanceToMove = 3 * Gdx.graphics.getDeltaTime();

      int nextMoveX = nextMove.getCoordinates().getX();
      int nextMoveY = nextMove.getCoordinates().getY();

      switch (nextMove.getDirection()) {
        case UP:
          movingSprite.setRotation(0);
          msYCoords += distanceToMove;
  
          // Rocket trail coordinates
          // rtXCoords = msXCoords + 0.5f;
          // rtYCoords = msYCoords;
          // // Rocket trail rotation
          // setEmmiterAngle(rocketTrail, 0);
  
          // Has the sprite reach the next coordinate in the board move
          if (movingSprite.getY() >= nextMoveY * PPS) {
            boardMove.remove(0);

            movingSprite.setX(nextMoveX * PPS);
            movingSprite.setY(nextMoveY * PPS);
            movingSprite.draw(batch);
            return;
          }

          break;

        case DOWN:
          movingSprite.setRotation(180);
          msYCoords -= distanceToMove;
  
          // rtXCoords = msXCoords + 0.5f;
          // rtYCoords = msYCoords + 1;
          // setEmmiterAngle(rocketTrail, 180);
          if (movingSprite.getY() <= nextMoveY * PPS) {
            boardMove.remove(0);

            movingSprite.setX(nextMoveX * PPS);
            movingSprite.setY(nextMoveY * PPS);
            movingSprite.draw(batch);
            return;
          }

          break;

        case RIGHT:
          movingSprite.setRotation(270);
          msXCoords += distanceToMove;
  
          // rtXCoords = msXCoords;
          // rtYCoords = msYCoords + 0.5f;
          // setEmmiterAngle(rocketTrail, 270);

          if (movingSprite.getX() >= nextMoveX * PPS) {
            boardMove.remove(0);

            movingSprite.setX(nextMoveX * PPS);
            movingSprite.setY(nextMoveY * PPS);
            movingSprite.draw(batch);
            return;
          }

          break;

        case LEFT:
          movingSprite.setRotation(90);
          msXCoords -= distanceToMove;
  
          // rtXCoords = msXCoords + 1;
          // rtYCoords = msYCoords + 0.5f;
          // setEmmiterAngle(rocketTrail, 90);

          if (movingSprite.getX() <= nextMoveX * PPS) {
            boardMove.remove(0);

            movingSprite.setX(nextMoveX * PPS);
            movingSprite.setY(nextMoveY * PPS);
            movingSprite.draw(batch);
            return;
          }

          break;

        default:
          break;
      }
        
      // rocketTrail.setPosition(rtXCoords * PPS, rtYCoords * PPS);
      movingSprite.setX(msXCoords * PPS);
      movingSprite.setY(msYCoords * PPS);
      movingSprite.draw(batch);
    }

    public void resize(int PPS) {
        sprites.forEach(sprite -> sprite.setSize(PPS, PPS));
        outlinedSprites.forEach(sprite -> sprite.setSize(PPS, PPS));
        movingSprite.setSize(PPS, PPS);
    }
    
    public void dispose() {
        sprites.forEach(sprite -> sprite.getTexture().dispose());
        outlinedSprites.forEach(sprite -> sprite.getTexture().dispose());
    }
}
