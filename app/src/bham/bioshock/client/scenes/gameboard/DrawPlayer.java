package bham.bioshock.client.scenes.gameboard;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
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
  float movingSpriteX = -1;
  float movingSpriteY = -1;
  
  ParticleEffect rocketTrail;
  float rocketTrailX;
  float rocketTrailY;

  public DrawPlayer(Batch batch) {
    super(batch);

    sprites = generateSprites(Assets.playersFolder);
    outlinedSprites = generateSprites(Assets.playersFolder);
    generateEffects();
  }

  private void generateEffects() {
     rocketTrail = new ParticleEffect();
     rocketTrail.load(
         Gdx.files.internal(Assets.particleEffect),
         Gdx.files.internal(Assets.particleEffectsFolder));
     rocketTrail.start();
  }

  public void draw(Player player, int PPS, boolean selected) {
    Sprite sprite = selected 
      ? outlinedSprites.get(player.getTextureID())
      : sprites.get(player.getTextureID());

    sprite.setOriginCenter();
    sprite.setX(player.getCoordinates().getX() * PPS);
    sprite.setY(player.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  public boolean drawMove(Player player, int PPS) {
    ArrayList<Player.Move> boardMove = player.getBoardMove();

    // Setup moving sprite
    movingSprite = sprites.get(player.getTextureID());
    movingSprite.setOriginCenter();

    // Get next available board move
    Player.Move nextMove = boardMove.get(0);

    // If the player is at the starting position set the starting coordinates
    if (nextMove.getDirection() == Direction.NONE) {
      // Set moving sprite position to position of the player
      movingSpriteX = nextMove.getCoordinates().getX();
      movingSpriteY = nextMove.getCoordinates().getY();

      // Removing starting position from move and get the next move
      boardMove.remove(0);
      nextMove = boardMove.get(0);
    }

    float distanceToMove = 3 * Gdx.graphics.getDeltaTime();

    // Get coordinates of the next grid point to move to
    int nextMoveX = nextMove.getCoordinates().getX();
    int nextMoveY = nextMove.getCoordinates().getY();

    switch (nextMove.getDirection()) {
      case UP:
        movingSprite.setRotation(0);
        movingSpriteY += distanceToMove;

        rocketTrailX = movingSpriteX + 0.5f;
        rocketTrailY = movingSpriteY;
        setRocketTrailAngle(rocketTrail, 0);

        // Has the sprite reach the next coordinate in the board move
        if (movingSprite.getY() >= nextMoveY * PPS) {
          movingSprite.setX(nextMoveX * PPS);
          movingSprite.setY(nextMoveY * PPS);
          movingSprite.draw(batch);
          
          return true;
        }

        break;

      case DOWN:
        movingSprite.setRotation(180);
        movingSpriteY -= distanceToMove;

        rocketTrailX = movingSpriteX + 0.5f;
        rocketTrailY = movingSpriteY + 1;
        setRocketTrailAngle(rocketTrail, 180);

        if (movingSprite.getY() <= nextMoveY * PPS) {
          movingSprite.setX(nextMoveX * PPS);
          movingSprite.setY(nextMoveY * PPS);
          movingSprite.draw(batch);
          
          return true;
        }

        break;

      case RIGHT:
        movingSprite.setRotation(270);
        movingSpriteX += distanceToMove;

        rocketTrailX = movingSpriteX;
        rocketTrailY = movingSpriteY + 0.5f;
        setRocketTrailAngle(rocketTrail, 270);

        if (movingSprite.getX() >= nextMoveX * PPS) {
          movingSprite.setX(nextMoveX * PPS);
          movingSprite.setY(nextMoveY * PPS);
          movingSprite.draw(batch);
          
          return true;
        }

        break;

      case LEFT:
        movingSprite.setRotation(90);
        movingSpriteX -= distanceToMove;

        rocketTrailX = movingSpriteX + 1;
        rocketTrailY = movingSpriteY + 0.5f;
        setRocketTrailAngle(rocketTrail, 90);

        if (movingSprite.getX() <= nextMoveX * PPS) {
          movingSprite.setX(nextMoveX * PPS);
          movingSprite.setY(nextMoveY * PPS);
          movingSprite.draw(batch);
          
          return true;
        }

        break;

      default:
        break;
    }
      
    rocketTrail.setPosition(rocketTrailX * PPS, rocketTrailY * PPS);
    rocketTrail.draw(batch, Gdx.graphics.getDeltaTime());
    
    movingSprite.setX(movingSpriteX * PPS);
    movingSprite.setY(movingSpriteY * PPS);
    movingSprite.draw(batch);

    return false;
  }

  private void setRocketTrailAngle(ParticleEffect particleEffect, float angle) {
    // Align particle effect angle with world
    angle -= 90;

    for (ParticleEmitter pe : particleEffect.getEmitters()) {
      ParticleEmitter.ScaledNumericValue val = pe.getAngle();
      float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
      float h1 = angle + amplitude;
      float h2 = angle - amplitude;

      val.setHigh(h1, h2);
      val.setLow(angle);
    }
  }


  public void resize(int PPS) {
    sprites.forEach(sprite -> sprite.setSize(PPS, PPS));
    outlinedSprites.forEach(sprite -> sprite.setSize(PPS, PPS));
    movingSprite.setSize(PPS, PPS);
  }
    
  public void dispose() {
    sprites.forEach(sprite -> sprite.getTexture().dispose());
    outlinedSprites.forEach(sprite -> sprite.getTexture().dispose());
    rocketTrail.dispose();
  }
}
