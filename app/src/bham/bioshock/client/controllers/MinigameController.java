package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MinigameScreen;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.minigame.worlds.FirstWorld;
import com.google.inject.Inject;

public class MinigameController extends Controller {

  private ClientService clientService;
  
  @Inject
  public MinigameController(Store store, Router router, BoardGame game, ClientService clientService) {
    super(store, router, game);
    this.clientService = clientService;
  }

  public void sendStart() {
    clientService.send(new Action(Command.MINIGAME_START));
  }
  
  public void show() {
    // Create local store for the minigame, and create a new world
    MinigameStore localStore = new MinigameStore();
    localStore.seed(store, new FirstWorld());
    
    store.setMinigameStore(localStore);

    setScreen(new MinigameScreen(localStore));
  }
}
