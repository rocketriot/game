package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MinigameScreen;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.minigame.PlayerTexture;
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
    
    clientService.send(new Action(Command.MINIGAME_PLAYER_MOVE, arguments));
  }
  
  public void updatePlayer(ArrayList<Serializable> arguments) {
    UUID playerId = (UUID) arguments.get(0);
    SpeedVector speed = (SpeedVector) arguments.get(1);
    Position pos = (Position) arguments.get(2);
    PlayerTexture dir = (PlayerTexture) arguments.get(3);
    
    localStore.updatePlayer(playerId, speed, pos, dir);
  }
  
  public void show() {
    // Create local store for the minigame, and create a new world
    MinigameStore localStore = new MinigameStore();
    localStore.seed(store, new FirstWorld());
    
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
