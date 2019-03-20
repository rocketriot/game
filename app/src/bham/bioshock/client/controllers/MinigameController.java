package bham.bioshock.client.controllers;

import bham.bioshock.communication.messages.EndMinigameMessage;
import bham.bioshock.communication.messages.MinigameEndMessage;
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
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.messages.BulletShotMessage;
import bham.bioshock.communication.messages.MinigamePlayerMoveMessage;
import bham.bioshock.communication.messages.MinigamePlayerStepMessage;
import bham.bioshock.communication.messages.MinigameStartMessage;
import bham.bioshock.communication.messages.RequestMinigameStartMessage;
import bham.bioshock.communication.messages.UpdateObjectiveMessage;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.objectives.Objective;
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
    clientService.send(new RequestMinigameStartMessage(planetId));
  }
  
  public void directStart() {
    clientService.send(new RequestMinigameStartMessage(null));
  }
  
  public void playerMove() {
    clientService.send(new MinigamePlayerMoveMessage(localStore.getMainPlayer()));
  }
  
  public void playerStep() {
    clientService.send(new MinigamePlayerStepMessage(localStore.getMainPlayer()));
  }
  
  public void updatePlayerStep(MinigamePlayerStepMessage data) {
    if(localStore == null) return;
    localStore.updatePlayerStep(data.created, data.playerId, data.speed, data.position, data.haveGun);
  }
  
  public void updatePlayerMove(MinigamePlayerMoveMessage data) {
    if(localStore == null) return;
    localStore.updatePlayerMove(data.playerId, data.move);
  }
  
  public void bulletShot(Bullet bullet) {
    clientService.send(new BulletShotMessage(bullet));
  }
  
  public void bulletCreate(BulletShotMessage data) {
    Position p = data.position;
    
    Bullet b = new Bullet(localStore.getWorld(), p.x, p.y,localStore.getMainPlayer());
    b.setSpeedVector(data.speedVector);
    b.load();
    b.setCollisionHandler(localStore.getCollisionHandler());
    localStore.addEntity(b);
  }
  
  public void updateObjective(UpdateObjectiveMessage data) {
    if(localStore == null) return;
    Objective o = localStore.getObjective();
    if(o == null) return;
    o.update(data.objective);
  }
  
  
  public void show(MinigameStartMessage data) {
    World world = data.world;
    Objective objective = data.objective;
    MinigameStore localStore = new MinigameStore();    
    
    // Initialise objective 
    localStore.seed(store, world, objective);
    objective.init(world, router, localStore);
    objective.seed(localStore);

    store.setMinigameStore(localStore);
    router.call(Route.FADE_OUT, "boardGame");
    router.call(Route.START_MUSIC, "minigame");
    setScreen(new MinigameScreen(store, router));
    localStore.started();
  }

  public void sendEnd(){
    clientService.send(new MinigameEndMessage());
  }

  public void end(EndMinigameMessage data){
    // Only if there's a winner
    if(data.winnerID != null) {
      Player p = store.getPlayer(data.winnerID);
      p.addPoints(data.points);
      if (data.initiatorWon) {
        UUID[] planetOwner = new UUID[]{data.winnerID, data.planetID};
        router.call(Route.SET_PLANET_OWNER, planetOwner);
      }
    }
    router.call(Route.FADE_OUT, "minigame");
    router.call(Route.GAME_BOARD_SHOW);
    store.resetMinigameStore();
  }
}
