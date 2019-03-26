package bham.bioshock.minigame.ai;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlatformerAITest {

  private static ArrayList<PlanetPosition> playerPositions = new ArrayList<>();
  private PlanetPosition cpu1Pos;
  private PlanetPosition cpu2Pos;
  private PlanetPosition cpu3Pos;

  private FindNextPlatform findNext;
  private moveTowardsPlatform moveTowards;

  private Platform goalPlatform;
  private ArrayList<Platform> pathToGoal;


  @Test
  void moveTowardsPlatformTest() {
    Platform platform1 = new Platform(store.getWorld(), 100,100,150,10);
    Platform platform2 = new Platform(store.getWorld(), 200,200,150,10);

    pathToGoal = new ArrayList();
    pathToGoal.add(platform1);
    pathToGoal.add(platform2);

    findNext = new FindNextPlatform("find next");
    findNext.update();

    assertEquals(goalPlatform, pathToGoal(0));
  }


}