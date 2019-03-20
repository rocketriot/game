package bham.bioshock.minigame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.physics.CollisionHandler;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;

public class InputListener extends InputAdapter {

  private boolean shooting = false;
  private Astronaut mainPlayer;
  private MinigameStore localStore;
  private World world;
  private Router router;
  private CollisionHandler collisionHandler;

  public InputListener(MinigameStore minigameStore, Router router, CollisionHandler collisionHandler) {
    this.world = minigameStore.getWorld();
    this.mainPlayer = minigameStore.getMainPlayer();
    this.localStore = minigameStore;
    this.router = router;
    this.collisionHandler = collisionHandler;
  }

  @Override
  public boolean keyDown(int keyCode) {
    if (Input.Keys.SPACE == keyCode && !shooting && mainPlayer.haveGun()) {
      createBullet();
      SoundController.playSound("laser");
      shooting = true;
    }
    if (keyCode == Input.Keys.LEFT || keyCode == Input.Keys.A) {
      mainPlayer.moveLeft(true);
    }
    if (keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.D) {
      mainPlayer.moveRight(true);
    }
    if (keyCode == Input.Keys.UP || keyCode == Input.Keys.W) {
      mainPlayer.jump(true);
    }
    mainPlayer.moveChange();
    System.out.println("up: " + mainPlayer.getMove());
    router.call(Route.MINIGAME_MOVE);

    return false;
  }

  @Override
  public boolean keyUp(int keyCode) {
    if (Input.Keys.SPACE == keyCode) {
      shooting = false;
    }
    if (keyCode == Input.Keys.UP || keyCode == Input.Keys.W) {
      mainPlayer.jump(false);
    }
    if (keyCode == Input.Keys.LEFT || keyCode == Input.Keys.A) {
      mainPlayer.moveLeft(false);
    }
    if (keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.D) {
      mainPlayer.moveRight(false);
    }
    mainPlayer.moveChange();
    System.out.println("up: " + mainPlayer.getMove());
    router.call(Route.MINIGAME_MOVE);

    return false;
  }

  public void createBullet() {
    Position pos = mainPlayer.getPos();
    PlanetPosition pp = world.convert(pos);
    pp.fromCenter += mainPlayer.getHeight() / 2;
    
    SpeedVector speed = mainPlayer.getSpeedVector().copy();
    
    if (mainPlayer.getMove().movingRight) {
      pp.angle += world.angleRatio(pp.fromCenter) * 80;
      speed.apply(world.getAngleTo(pos.x, pos.y)+90 , Bullet.launchSpeed);
    } else {
      pp.angle -= world.angleRatio(pp.fromCenter) * 80;
      speed.apply(world.getAngleTo(pos.x, pos.y)-90 , Bullet.launchSpeed);
    }
        
    Position bulletPos = world.convert(pp);
    Bullet b = new Bullet(world, bulletPos.x, bulletPos.y, mainPlayer);
    
    // Apply bullet speed
    b.setSpeedVector(speed);

    router.call(Route.MINIGAME_BULLET_SEND, b);
    b.load();
    b.setCollisionHandler(collisionHandler);
    localStore.addEntity(b);
  }

}
