package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MinigameScreen;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.Map;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.minigame.PlayerTexture;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.objectives.KillThemAll;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.FirstWorld;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import com.google.inject.Inject;

public class MinigameController extends Controller {

  private ClientService clientService;
  private MinigameStore localStore;
  
  @Inject
  public MinigameController(Store store, Router router, BoardGame game, ClientService clientService) {
    super(store, router, game);
    this.clientService = clientService;
    localStore = store.getMinigameStore();
  }


  public void sendStart() {
    clientService.send(new Action(Command.MINIGAME_START));
  }
  
  public void playerMove() {
    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add((Serializable) store.getMainPlayer().getId());
    arguments.add((Serializable) localStore.getMainPlayer().getSpeedVector());
    arguments.add((Serializable) localStore.getMainPlayer().getPosition());
    arguments.add((Serializable) localStore.getMainPlayer().getDirection());
    arguments.add((Serializable) localStore.getMainPlayer().haveGun());
    
    clientService.send(new Action(Command.MINIGAME_PLAYER_MOVE, arguments));
  }
  
  public void updatePlayer(ArrayList<Serializable> arguments) {
    UUID playerId = (UUID) arguments.get(0);
    SpeedVector speed = (SpeedVector) arguments.get(1);
    Position pos = (Position) arguments.get(2);
    PlayerTexture dir = (PlayerTexture) arguments.get(3);
    Boolean haveGun = (Boolean) arguments.get(4);
    
    localStore.updatePlayer(playerId, speed, pos, dir, haveGun);
  }
  
  public void bulletShot(Bullet bullet) {
    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add((Serializable) bullet.getSpeedVector());
    arguments.add((Serializable) bullet.getPos());
    
    clientService.send(new Action(Command.MINIGAME_BULLET, arguments));
  }
  
  public void bulletCreate(ArrayList<Serializable> arguments) {
    SpeedVector sv = (SpeedVector) arguments.get(0);
    Position p = (Position) arguments.get(1);
    
    MinigameScreen screen = (MinigameScreen) store.getScreen();
    Bullet b = new Bullet(localStore.getWorld(), p.x, p.y,localStore.getMainPlayer());
    b.setSpeedVector(sv);
    screen.addBullet(b);
  }
  
  
  public void show() {
    // Create local store for the minigame, and create a new world
    MinigameStore localStore = new MinigameStore();
    // for testing
    Objective objective = new KillThemAll();

    localStore.seed(store, new FirstWorld(), objective);

    store.setMinigameStore(localStore);

    router.call(Route.FADE_OUT, "boardGame");
    router.call(Route.START_MUSIC, "minigame");
    setScreen(new MinigameScreen(store, router));
  }

  public void sendEnd(){
    clientService.send(new Action(Command.MINIGAME_END));
  }

  public void end(ArrayList<Serializable> arguments){
    // end the minigame and send players back to the board
    UUID playerId = (UUID) arguments.get(0);
    int newScore = (int) arguments.get(1);
    store.getPlayer(playerId).setPoints(newScore);

    router.call(Route.GAME_BOARD_SHOW);
    store.resetMinigameStore();
  }
}
