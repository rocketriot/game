package bham.bioshock.client.controllers;

import bham.bioshock.communication.messages.EndMinigameMessage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import com.google.inject.Inject;
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
import bham.bioshock.communication.messages.BulletShotMessage;
import bham.bioshock.communication.messages.MinigamePlayerMoveMessage;
import bham.bioshock.minigame.models.Astronaut.Move;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.physics.SpeedVector;
import bham.bioshock.minigame.worlds.World;

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
    clientService.send(new MinigamePlayerMoveMessage(localStore.getMainPlayer()));
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
    clientService.send(new BulletShotMessage(bullet));
  }
  
  public void bulletCreate(BulletShotMessage data) {
    Position p = data.position;
    
    Bullet b = new Bullet(localStore.getWorld(), p.x, p.y,localStore.getMainPlayer());
    b.setSpeedVector(data.speedVector);
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

  public void end(EndMinigameMessage data){
    // Only if there's a winner
    if(data.winnerId != null) {
      Player p = store.getPlayer(data.winnerId);
      p.addPoints(data.points);
      if (data.isCaptured) {
        UUID[] planetOwner = new UUID[]{data.winnerId, data.planetId};
        router.call(Route.SET_PLANET_OWNER, planetOwner);
      }
    }
    router.call(Route.FADE_OUT, "minigame");
    router.call(Route.GAME_BOARD_SHOW);
    store.resetMinigameStore();
  }
}
