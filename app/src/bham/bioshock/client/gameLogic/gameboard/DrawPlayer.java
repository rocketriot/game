package bham.bioshock.client.gameLogic.gameboard;

import bham.bioshock.client.Assets;
import bham.bioshock.common.Direction;
import bham.bioshock.common.models.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class DrawPlayer extends DrawEntity {
  Sprite movingSprite;
  float movingSpriteX = -1;
  float movingSpriteY = -1;
  ParticleEffect rocketTrail;
  float rocketTrailX;
  float rocketTrailY;
  private ArrayList<Sprite> sprites = new ArrayList<>();
  private ArrayList<Sprite> outlinedSprites = new ArrayList<>();

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
    Sprite sprite =
        selected ? outlinedSprites.get(player.getTextureID()) : sprites.get(player.getTextureID());

    sprite.setOriginCenter();
    sprite.setX(player.getCoordinates().getX() * PPS);
    sprite.setY(player.getCoordinates().getY() * PPS);
    sprite.draw(batch);
  }

  public void setupMove(Player player) {
    ArrayList<Player.Move> boardMove = player.getBoardMove();
    Player.Move nextMove = boardMove.get(0);

    movingSpriteX = nextMove.getCoordinates().getX();
    movingSpriteY = nextMove.getCoordinates().getY();

    rocketTrail.reset();
  }

  public boolean drawMove(Player player, int PPS) {
    ArrayList<Player.Move> boardMove = player.getBoardMove();

    // Setup moving sprite
    movingSprite = sprites.get(player.getTextureID());
    movingSprite.setOriginCenter();

    // Get next available board move
    Player.Move nextMove = boardMove.get(0);

    float distanceToMove = 3 * Gdx.graphics.getDeltaTime();

    // Get coordinates of the next grid point to move to
    int nextMoveX = nextMove.getCoordinates().getX();
    int nextMoveY = nextMove.getCoordinates().getY();

    boolean posUpdated = false;
    
    switch (nextMove.getDirection()) {
      case UP:
        movingSprite.setRotation(0);
        movingSpriteY += distanceToMove;

        rocketTrailX = movingSpriteX + 0.4f;
        rocketTrailY = movingSpriteY;
        setRocketTrailAngle(0);

        // Has the sprite reach the next coordinate in the board move
        if (movingSprite.getY() >= nextMoveY * PPS) {
          posUpdated = true;
        }

        break;

      case DOWN:
        movingSprite.setRotation(180);
        movingSpriteY -= distanceToMove;

        rocketTrailX = movingSpriteX + 0.4f;
        rocketTrailY = movingSpriteY + 1;
        setRocketTrailAngle(180);

        if (movingSprite.getY() <= nextMoveY * PPS) {
          posUpdated = true;
        }

        break;

      case RIGHT:
        movingSprite.setRotation(270);
        movingSpriteX += distanceToMove;

        rocketTrailX = movingSpriteX;
        rocketTrailY = movingSpriteY + 0.5f;
        setRocketTrailAngle(270);

        if (movingSprite.getX() >= nextMoveX * PPS) {
          posUpdated = true;
        }

        break;

      case LEFT:
        movingSprite.setRotation(90);
        movingSpriteX -= distanceToMove;

        rocketTrailX = movingSpriteX + 0.9f;
        rocketTrailY = movingSpriteY + 0.5f;
        setRocketTrailAngle(90);

        if (movingSprite.getX() <= nextMoveX * PPS) {
          posUpdated = true;
        }

        break;

      default:
        break;
    }

    rocketTrail.setPosition(rocketTrailX * PPS, rocketTrailY * PPS);
    rocketTrail.draw(batch, Gdx.graphics.getDeltaTime());
    
    if(posUpdated) {
      movingSprite.setX(nextMoveX * PPS);
      movingSprite.setY(nextMoveY * PPS);
    } else {
      movingSprite.setX(movingSpriteX * PPS);
      movingSprite.setY(movingSpriteY * PPS);
    }
    movingSprite.draw(batch);      
    

    return posUpdated;
  }


  private void setRocketTrailAngle(float angle) {
    // Align particle effect angle with world
    angle -= 90;

    for (ParticleEmitter pe : rocketTrail.getEmitters()) {
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
    if(movingSprite != null) {
      movingSprite.setSize(PPS, PPS);      
    }
  }

  public void dispose() {
    sprites.forEach(sprite -> sprite.getTexture().dispose());
    outlinedSprites.forEach(sprite -> sprite.getTexture().dispose());
    rocketTrail.dispose();
  }
}
