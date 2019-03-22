package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MinigameScreen;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.minigame.models.Astronaut.Move;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;
import com.google.inject.Inject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class MinigameController extends Controller {

  private ClientService clientService;
  private MinigameStore localStore;
  
  @Inject
  public MinigameController(Store store, Router router, BoardGame game, ClientService clientService) {
    super(store, router, game);
    this.clientService = clientService;
    localStore = store.getMinigameStore();
  }


  public void sendStart(UUID planetId) {
    clientService.send(new Action(Command.MINIGAME_START, planetId));
  }
  
  public void directStart() {
    clientService.send(new Action(Command.MINIGAME_DIRECT_START));
  }
  
  public void playerMove() {
    ArrayList<Serializable> arguments = new ArrayList<>();
    arguments.add((Serializable) store.getMainPlayer().getId());
    arguments.add((Serializable) localStore.getMainPlayer().getSpeedVector());
    arguments.add((Serializable) localStore.getMainPlayer().getPos());
    arguments.add((Serializable) localStore.getMainPlayer().getMove());
    arguments.add((Serializable) localStore.getMainPlayer().haveGun());
    
    clientService.send(new Action(Command.MINIGAME_PLAYER_MOVE, arguments));
  }
  
  public void updatePlayer(Action action) {
    UUID playerId = (UUID) action.getArgument(0);
    SpeedVector speed = (SpeedVector) action.getArgument(1);
    Position pos = (Position) action.getArgument(2);
    Move move = (Move) action.getArgument(3);
    Boolean haveGun = (Boolean) action.getArgument(4);

    localStore.updatePlayer(action.whenCreated(), playerId, speed, pos, move, haveGun);
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
    
    Bullet b = new Bullet(localStore.getWorld(), p.x, p.y,localStore.getMainPlayer());
    b.setSpeedVector(sv);
    b.load();
    localStore.addEntity(b);
  }
  
  
  public void show(ArrayList<Serializable> arr) {
    World w = (World) arr.get(0);
    Objective o = (Objective) arr.get(1);
    MinigameStore localStore = new MinigameStore();
    
    // Initialise objective 
    localStore.seed(store, w, o);
    o.init(w, router, localStore);
    o.seed(localStore);

    store.setMinigameStore(localStore);
    router.call(Route.FADE_OUT, "boardGame");
    router.call(Route.START_MUSIC, "minigame");
    setScreen(new MinigameScreen(store, router));
    localStore.started();
  }

  public void sendEnd(){
    clientService.send(new Action(Command.MINIGAME_END));
  }

  public void end(ArrayList<Serializable> arguments){
    // end the minigame and send players back to the board
    UUID playerId = (UUID) arguments.get(0);
    UUID planetId = (UUID) arguments.get(1);
    int points = (int) arguments.get(2);
    
    // Only if there's a winner
    if(playerId != null) {
      Player p = store.getPlayer(playerId);
      p.addPoints(points);
      UUID[] planetOwner = new UUID[] { playerId, planetId };
      router.call(Route.SET_PLANET_OWNER, planetOwner);      
    }
    router.call(Route.FADE_OUT, "minigame");
    router.call(Route.GAME_BOARD_SHOW);
    store.resetMinigameStore();
  }
}
