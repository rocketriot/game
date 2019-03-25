package bham.bioshock.minigame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.client.scenes.minigame.MinigameHud;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.*;
import bham.bioshock.minigame.physics.CollisionHandler;
import bham.bioshock.minigame.worlds.World;

public class InputListener extends InputAdapter {

  private boolean shooting = false;
  private Astronaut mainPlayer;
  private MinigameStore localStore;
  private World world;
  private Router router;
  private CollisionHandler collisionHandler;
  private MinigameHud hud;

  public InputListener(MinigameStore minigameStore, Router router, CollisionHandler collisionHandler, MinigameHud hud) {
    this.world = minigameStore.getWorld();
    this.mainPlayer = minigameStore.getMainPlayer();
    this.localStore = minigameStore;
    this.router = router;
    this.collisionHandler = collisionHandler;
    this.hud = hud;
  }

  @Override
  public boolean keyDown(int keyCode) {
    if (Input.Keys.SPACE == keyCode && !shooting && mainPlayer.getEquipment().haveGun) {
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
    router.call(Route.MINIGAME_MOVE);

    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    hud.touchDown(screenX, screenY, pointer, button);

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
    router.call(Route.MINIGAME_MOVE);

    return false;
  }

  public void createBullet() {
    Bullet b = Bullet.createForPlayer(world, mainPlayer);
    b.load();
    b.setCollisionHandler(collisionHandler);
    b.update(0);
    router.call(Route.MINIGAME_BULLET_SEND, b);
    localStore.addEntity(b);
  }

}
