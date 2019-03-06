package bham.bioshock.minigame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.worlds.World.PlanetPosition;

public class InputListener extends InputAdapter {

  private boolean shooting = false;
  private Player mainPlayer;
  private MinigameStore localStore;
  private World world;
  private Router router;
  
  public InputListener(MinigameStore minigameStore, Router router) {
    this.world = minigameStore.getWorld();
    this.mainPlayer = minigameStore.getMainPlayer();
    this.localStore = minigameStore;
    this.router = router;
  }
  
  @Override
  public boolean keyDown(int keyCode) {
    if (Input.Keys.SPACE == keyCode && !shooting && mainPlayer.haveGun()) {
      createBullet();
      SoundController.playSound("laser");
      shooting = true;
    }
    if(keyCode == Input.Keys.LEFT || keyCode == Input.Keys.A) {
      System.out.println("MOVE LEFT");
      mainPlayer.moveLeft();
    }
    if(keyCode == Input.Keys.RIGHT || keyCode == Input.Keys.D) {
      System.out.println("MOVE RIGHT");
      mainPlayer.moveRight();
    }
    if(keyCode == Input.Keys.UP || keyCode == Input.Keys.W) {
      mainPlayer.jump();
    }
    
    router.call(Route.MINIGAME_MOVE);
    
    return false;
  }

  @Override
  public boolean keyUp(int keyCode) {
    if (Input.Keys.SPACE == keyCode) {
      shooting = false;
    }
    
    mainPlayer.moveStop();
    
    return false;
  }
  
  public void createBullet() {
    PlanetPosition pp = world.convert(mainPlayer.getPos());
    pp.fromCenter += mainPlayer.getHeight() / 2;

    if (mainPlayer.getDirection().equals(PlayerTexture.LEFT)) {
      pp.angle -= 2;
    } else if (mainPlayer.getDirection().equals(PlayerTexture.RIGHT)) {
      pp.angle += 2;
    }

    Position bulletPos = world.convert(pp);

    Bullet b = new Bullet(world, bulletPos.x, bulletPos.y, mainPlayer);
    // First synchronise the bullet with the player
    b.setSpeedVector((SpeedVector) mainPlayer.getSpeedVector().clone());
    // Apply bullet speed
    b.setSpeed((float) mainPlayer.getSpeedVector().getSpeedAngle(), Bullet.launchSpeed);
//    router.call(Route.MINIGAME_BULLET_SEND, b);
    addBullet(b);
  }

  public void addBullet(Bullet b) {
    b.load();
    localStore.addEntity(b);
//    entities.add(b);
  }
}
