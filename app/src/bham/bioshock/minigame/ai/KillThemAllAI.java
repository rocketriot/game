package bham.bioshock.minigame.ai;

import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.worlds.World;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.server.ServerHandler;

/**
 * The KillThemAllAI.
 */
public class KillThemAllAI extends MinigameAI {

  private static final Logger logger = LogManager.getLogger(KillThemAllAI.class);

  private World world;

  /**
   * Instantiates a new KillThemAllAI.
   *
   * @param id the id of the cpu player
   * @param store the store
   * @param handler the handler
   */
  public KillThemAllAI(UUID id, Store store, ServerHandler handler) {
    super(id, store, handler);
  }

  @Override
  public void update(float delta) {
    world = localStore.getWorld();
    Astronaut astro = astronaut.get();
    PlanetPosition astroPos = astro.getPlanetPos();
    PlanetPosition goalPos;

    if (!astro.haveGun() && !world.getGuns().isEmpty()) {
      goalPos = findNearestGun(astroPos);
    } else {
      goalPos = findNearestPlayer(astronaut.astronaut);
    }

    if (goalPos == null) {
      return;
    }

    if (normaliseAngle(goalPos.angle) < normaliseAngle(astroPos.angle)) {
      astronaut.moveLeft();
    } else {
      astronaut.moveRight();
    }

    if (astronaut.astronaut.haveGun()
        && goalPos.fromCenter > astroPos.fromCenter) {
      astronaut.jump();
    }

    if (astronaut.astronaut.haveGun()
        && Math.abs(normaliseAngle(goalPos.angle) - normaliseAngle(astroPos.angle)) <= 25) {
      astronaut.shoot();
    }
  }

  /**
   * Find nearest gun position.
   *
   * @param astronautPosition the player position
   * @return the planet position of the nearest gun
   */
  private PlanetPosition findNearestGun(PlanetPosition astronautPosition) {
    float astroAngle = normaliseAngle(astronautPosition.angle);
    Collection<Gun> guns = localStore.getGuns();
    PlanetPosition nearestGun = null;
    float nearestAngle = Integer.MAX_VALUE;
    if (guns.isEmpty()) {
      return null;
    } else {
      for (Gun gun : guns) {
        PlanetPosition gunPos = world.convert(gun.getPos());
        float gunAng = normaliseAngle(gunPos.angle);

        if (Math.abs(gunAng - astroAngle) < nearestAngle) {
          nearestAngle = Math.abs(gunAng - astroAngle);
          nearestGun = gun.getPlanetPos();
        }
      }
    }
    return nearestGun;
  }

  /**
   * Find nearest player position.
   *
   * @param astronaut the astronaut
   * @return the nearest player position
   */
  private PlanetPosition findNearestPlayer(Astronaut astronaut) {
    float astroAngle = normaliseAngle(astronaut.getPlanetPos().angle);

    Collection<Astronaut> storePlayers = localStore.getPlayers();
    ArrayList<Astronaut> players = new ArrayList<>();

    for (Astronaut astro : storePlayers) {
      players.add(astro);
    }
    players.remove(astronaut);

    PlanetPosition nearestPlayer = null;
    float nearestAngle = Integer.MAX_VALUE;
    for (Astronaut player : players) {
      PlanetPosition playerPos = player.getPlanetPos();
      float playerAng = normaliseAngle(playerPos.angle);
      if (Math.abs(playerAng - astroAngle) < nearestAngle) {
        nearestAngle = Math.abs(playerAng - astroAngle);
        nearestPlayer = player.getPlanetPos();
      }
    }
    return nearestPlayer;
  }

  /**
   * Normalise angles so that the the circular world becomes "flat", between -179 and 180
   *
   * @param angle the angle
   * @return the normalised angle
   */
  private float normaliseAngle(float angle) {
    if (angle <= 180) {
      return angle;
    } else {
      return (angle - 360);
    }
  }
}
