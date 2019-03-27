package bham.bioshock.minigame.ai;

import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.worlds.RandomWorld;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;
import bham.bioshock.testutils.server.FakeServerHandler;
import org.junit.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

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
  private ArrayList<Platform> pathToGoal;

  public PlatformerAITest() {
      testWorld = new RandomWorld();
      testStore =  new Store();
      testUUID = new UUID(0,100);
      testServerHandler = new FakeServerHandler();
      ai = new PlatformerAI(testUUID, testStore, testServerHandler);

  }

  @Test
  void moveTowardsPlatformTest() {


    Platform platform1 = new Platform(testWorld, 100,100,150,10);
    Platform platform2 = new Platform(testWorld, 200,200,150,10);

    pathToGoal = new ArrayList();
    pathToGoal.add(platform1);
    pathToGoal.add(platform2);


    assertEquals(goalPlatform, pathToGoal.get(0));
  }


}