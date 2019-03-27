package bham.bioshock.minigame.ai;

import bham.bioshock.common.Position;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlatformerAITest {

  private static ArrayList<PlanetPosition> playerPositions = new ArrayList<>();
  private PlanetPosition cpu1Pos;
  private PlanetPosition cpu2Pos;
  private PlanetPosition cpu3Pos;

  private PlatformerAI.FindNextPlatform findNext;
  private PlatformerAI.MoveTowardsPlatform moveTowards;

  private World testWorld;
  private Store testStore;
  private UUID testUUID;
  private MultipleConnectionsHandler testServerHandler;

  private PlatformerAI ai;

  private Platform goalPlatform;
  private ArrayList<Platform> allPlatforms;
  private ArrayList<Platform> pathToGoal;

  private HashMap<UUID, Boolean> frozen;
  private HashMap<UUID, Long> frozenFor;

  @BeforeAll
  public void setupTests() {
    frozen = new HashMap<>();
    frozenFor = new HashMap<>();
    /*testWorld = new TestingWorld();
    testStore =  new Store();
    testUUID = new UUID(0,100);
    testServerHandler = new FakeServerHandler();
    ai = new PlatformerAI(testUUID, testStore, testServerHandler);

    ((TestingWorld) testWorld).seedPlatforms();
    allPlatforms = testWorld.getPlatforms();*/
  }


  @Test
  void moveTowardsPlatformTest()
  {playerPositions.clear();
    cpu1Pos = new PlanetPosition(30, 2250);
    playerPositions.add(cpu1Pos);
    cpu2Pos = new PlanetPosition(73, 2250);
    playerPositions.add(cpu2Pos);
    cpu3Pos = new PlanetPosition(176, 2250);
    playerPositions.add(cpu3Pos);

    Platform platform1 = new Platform(testWorld, 100,100,150,10);
    Platform platform2 = new Platform(testWorld, 200,200,150,10);

    pathToGoal = new ArrayList();
    pathToGoal.add(platform1);
    pathToGoal.add(platform2);

  }

  @Test
  void angleTest() {


  }

  @Test
  void moveDirectionTest() {
      int direction;

            /*
            Determine the angle between the player's current position and the position of the platform
             */

      cpu1Pos = new PlanetPosition(30, 2250);
      PlanetPosition platform1Pos = new PlanetPosition(40, 5550);

      double angleDelta = cpu1Pos.angle - platform1Pos.angle;
            /*
            Normalise the angle
            */
      double angle = (angleDelta + 180) % 360 - 180;

            /*
            Move in the direction of the planet
             */
      if(angle < 0) {
        direction = 1;
      } else {
        direction = 2;
      }

      assertEquals(direction, 1);
  }


  @Test
  void sqDistanceTest() {
    Position cpu1pos = new Position(100,200);
    Position cpu2pos = new Position(300, 500);
    Position cpu3pos = new Position(900, 500);
    Position platformPos = new Position(200, 300);

    //20,000
    double distance1 = cpu1pos.sqDistanceFrom(platformPos);
    //50,000
    double distance2 = cpu2pos.sqDistanceFrom(platformPos);
    //510,000
    double distance3 = cpu3pos.sqDistanceFrom(platformPos);

    assertEquals(distance1,20000, 2);
    assertEquals(distance2,50000, 2);
    assertEquals(distance3,530000, 2);

  }

  @Test
  void jumpTest() {
    /*
            Get how far the player is from the platform
             */
    cpu1Pos = new PlanetPosition(30, 2250);
    PlanetPosition platform1Pos = new PlanetPosition(120, 5550);

    Position cpu1pos = new Position(100,200);
    Position cpu2pos = new Position(300, 500);
    Position cpu3pos = new Position(1000, 1200);
    Position cpu4pos = new Position(200, 300);
    Position platformPos = new Position(200, 300);

    // between mid and min 20,000
    double distance1 = cpu1pos.sqDistanceFrom(platformPos);
    // min 50,000
    double distance2 = cpu2pos.sqDistanceFrom(platformPos);
    // greater than max 1,450,000
    double distance3 = cpu3pos.sqDistanceFrom(platformPos);
    // less than min 0
    double distance4 = cpu4pos.sqDistanceFrom(platformPos);

    int dir1 = testJump(distance1);
    int dir2 = testJump(distance2);
    int dir3 = testJump(distance3);
    int dir4 = testJump(distance4);

    assertEquals(dir1, 3);
    assertEquals(dir4, 3);
    assertEquals(dir2, 3);

    assertEquals(dir3, 0);

  }

  private int testJump(double distance) {
    double JUMP_MIN = 50000;
    double JUMP_MID = 80000;
    double JUMP_MAX = 1400000;

    if ( distance > JUMP_MAX) {
      return 0;
      //The astronaut is too far from the platform to jump, do nothing
    }
    else if ( distance > JUMP_MID){
      //jump without moving, this will be a long jump
      return 1;

    }
    else if(distance > JUMP_MIN){
      //move a little in the opposite direction to slow the astronaut down
      return 2;
    }
    else {
      //move a bit more in the opposite direction
      return 3;
    }
  }

  @Test
  void frozenTest() {


    UUID cpu1 = new UUID(0,100);
    long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);


    frozen.put(cpu1, true);
    frozenFor.put(cpu1, now);

    checkIfFrozen(cpu1);

    assertTrue(frozen.containsKey(cpu1));
    assertEquals(frozenFor.get(cpu1), now);

    now -= 6;
    frozenFor.put(cpu1, now);

    checkIfFrozen(cpu1);

    assertFalse(frozen.get(cpu1));
  }

  private void checkIfFrozen(UUID id) {
    float MAX_FROZEN_TIME = 5;
    if(frozen.containsKey(id)) {
      long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
      long time = frozenFor.get(id);
                /*
                Compare the current time to the time at which the player was frozen
                 */
      if((now - time) > (MAX_FROZEN_TIME)) {
                    /*
                    Enough time has elapsed so the player should now be unfrozen
                     */
        frozen.put(id, false);
        frozenFor.remove(id);
      }
      else {
                    /*
                    player is frozen, return without updating the stack
                     */

      }
    }
  }

}