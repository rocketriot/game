package bham.bioshock.minigame.ai;

import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.objectives.CaptureTheFlag;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.server.ServerHandler;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The CaptureTheFlagAI.
 */
public class CaptureTheFlagAI extends MinigameAI {

  private static final Logger logger = LogManager.getLogger(CaptureTheFlagAI.class);

  private World world;

  /**
   * Instantiates a new KillThemAllAI.
   *
   * @param id the id of the cpu player
   * @param store the store
   * @param handler the handler
   */
  public CaptureTheFlagAI(UUID id, Store store, ServerHandler handler) {
    super(id, store, handler);
  }

  @Override
  public void update(float delta) {

    CaptureTheFlag ctf = (CaptureTheFlag) localStore.getObjective();

    world = localStore.getWorld();
    Astronaut astro = astronaut.get();
    PlanetPosition astroPos = astro.getPlanetPos();
    PlanetPosition goalPos;

    if (!astro.haveGun() && !world.getGuns().isEmpty()) {
      goalPos = findNearestGun(astroPos);
    } else if (astro.getId() != ctf.getFlagOwner()) {
      goalPos = world.convert(ctf.getFlagPosition());
      if (goalPos.fromCenter > 2350) {
        Platform platform = findClosestPlatform(goalPos);
        if (platform != null) {
          ArrayList<Platform> path = world.getPlatformPath(platform);
          goalPos = path.get(0).getPlanetPos();
        }
      }
    } else {
      goalPos = new PlanetPosition(astroPos.angle + 10, astroPos.fromCenter);
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
        && Math.abs(normaliseAngle(goalPos.angle) - normaliseAngle(astroPos.angle)) <= 15) {
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
   * Find the closest platform to a position
   *
   * @param position the position
   * @return the closest platform
   */
  private Platform findClosestPlatform(PlanetPosition position) {
    ArrayList<Platform> platformsCopy = world.getPlatforms();
    ArrayList<Platform> platforms = new ArrayList<>();
    platforms.addAll(platformsCopy);
    ArrayList<Platform> consideredPlatforms = new ArrayList<>();

    float lowerFromLimit = position.fromCenter - 50;
    float upperFromLimit = position.fromCenter + 50;
    float lowerAngLimit = position.angle - 5;
    float upperAngLimit = position.angle + 5;

    // remove all platforms not within the right height
    for (Platform platform : platforms) {
      if (platform.getPlanetPos().fromCenter > lowerFromLimit
          && platform.getPlanetPos().fromCenter < upperFromLimit) {
        consideredPlatforms.add(platform);
      }
    }

    // remove all platforms not within the right angle
    for (Platform platform : platforms) {
      if (platform.getPlanetPos().angle < lowerAngLimit
          || platform.getPlanetPos().fromCenter > upperAngLimit) {
        consideredPlatforms.remove(platform);
      }
    }

    if (!consideredPlatforms.isEmpty()) {
      return consideredPlatforms.get(0);
    } else {
      return null;
    }
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
