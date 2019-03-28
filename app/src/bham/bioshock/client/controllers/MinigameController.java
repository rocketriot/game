package bham.bioshock.client.controllers;

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.screens.MinigameScreen;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.communication.messages.minigame.BulletShotMessage;
import bham.bioshock.communication.messages.minigame.EndMinigameMessage;
import bham.bioshock.communication.messages.minigame.MinigamePlayerMoveMessage;
import bham.bioshock.communication.messages.minigame.MinigamePlayerStepMessage;
import bham.bioshock.communication.messages.minigame.MinigameStartMessage;
import bham.bioshock.communication.messages.minigame.RequestMinigameStartMessage;
import bham.bioshock.minigame.models.Bullet;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.worlds.World;

public class MinigameController extends Controller {

  private static final Logger logger = LogManager.getLogger(MinigameController.class);

  private ClientService clientService;
  private MinigameStore localStore;
  private AssetContainer assets;

  @Inject
  public MinigameController(Store store, Router router, BoardGame game,
      ClientService clientService, AssetContainer assets) {
    super(store, router, game);
    this.clientService = clientService;
    localStore = store.getMinigameStore();
    this.assets = assets;
  }


  /**
   * Request minigame start
   * 
   * @param planetId
   */
  public void sendStart(UUID planetId) {
    clientService.send(new RequestMinigameStartMessage(planetId));
  }

  /**
   * Request direct minigame start (no planet is being captured)
   */
  public void directStart() {
    clientService.send(new RequestMinigameStartMessage(null));
  }

  /**
   * Send information of astronaut's move
   */
  public void playerMove() {
    clientService.send(new MinigamePlayerMoveMessage(localStore.getMainPlayer()));
  }

  /**
   * Send information about astronaut current position and speed vector
   */
  public void playerStep() {
    clientService.send(new MinigamePlayerStepMessage(localStore.getMainPlayer()));
  }

  /**
   * Update astronaut speed vector, position and other details from the server
   * 
   * @param data
   */
  public void updatePlayerStep(MinigamePlayerStepMessage data) {
    if (localStore == null)
      return;
    localStore.updatePlayerStep(data.created, data.playerId, data.speed, data.position,
        data.equipment);
  }

  /**
   * Update player move from the server
   * 
   * @param data
   */
  public void updatePlayerMove(MinigamePlayerMoveMessage data) {
    if (localStore == null)
      return;
    localStore.updatePlayerMove(data.playerId, data.move);
  }

  /**
   * Send information about new bullet shot by the player
   * 
   * @param bullet
   */
  public void bulletShot(Bullet bullet) {
    clientService.send(new BulletShotMessage(bullet));
  }

  /**
   * Create a bullet based on the information from the server
   * 
   * @param data
   */
  public void bulletCreate(BulletShotMessage data) {
    Position p = data.position;
    if (localStore == null || localStore.getMainPlayer() == null)
      return;
    Bullet b = new Bullet(localStore.getWorld(), p.x, p.y, data.shooterId);
    b.setSpeedVector(data.speedVector);
    b.load();
    b.setCollisionHandler(localStore.getCollisionHandler());
    localStore.addEntity(b);
  }

  /**
   * Update objective from the server Exact logic for update is handled by the objective itself
   * 
   * @param message
   */
  public void updateObjective(Message message) {
    if (localStore == null)
      return;
    Objective o = localStore.getObjective();
    if (o == null)
      return;
    o.handleMessage(message);
  }

  /**
   * Send objective update, this method is called by the objective itself
   * 
   * @param message
   */
  public void sendObjectiveUpdate(Message message) {
    if (message.command.equals(Command.MINIGAME_OBJECTIVE)) {
      clientService.send(message);
    } else {
      logger.fatal("Incorrect message in objective update");
    }
  }

  /**
   * Spawn entity
   */
  public void spawnEntity(Entity entity) {
    entity.load();
    MinigameStore localStore = store.getMinigameStore();
    entity.setCollisionHandler(localStore.getCollisionHandler());
    localStore.addEntity(entity);
  }
  
  /**
   * Start minigame with provided world and objective Seed the minigameStore and initialise
   * objective
   * 
   * @param data
   */
  public void show(MinigameStartMessage data) {
    World world = data.world;
    Objective objective = data.objective;
    MinigameStore localStore = new MinigameStore();

    // Initialise store
    localStore.seed(store, world, objective, data.planetId);
    store.setMinigameStore(localStore);

    // Initialise objective
    objective.init(world, router, store);
    objective.seed(localStore);

    router.call(Route.FADE_OUT, "boardGame");
    router.call(Route.START_MUSIC, "minigame");
    setScreen(new MinigameScreen(store, router, assets));
    localStore.started();
  }

  /**
   * Stop the minigame, show the winner, update the owner and go back to the board
   * 
   * @param data
   */
  public void end(EndMinigameMessage data) {
    // Only if there's a winner
    if (data.winnerID != null) {
      Player p = store.getPlayer(data.winnerID);
      store.setMinigameWinner(p.getUsername());
      p.addPoints(data.points);
      if (data.initiatorWon) {
        UUID[] planetOwner = new UUID[] {data.winnerID, data.planetID};
        router.call(Route.SET_PLANET_OWNER, planetOwner);
      } else {
        p = store.getPlayer(data.playerID);
        p.decreaseFuel(3 * p.getFuelGridCost());
      }
    } else {
        store.setMinigameWinner("No one");
      }
    store.resetMinigameStore();
    router.call(Route.FADE_OUT, "minigame");
    router.call(Route.GAME_BOARD_SHOW);
  }
}
