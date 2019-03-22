package bham.bioshock.minigame.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import bham.bioshock.minigame.PlanetPosition;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * The type Kill them all ai tests.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KillThemAllAITests {

  private static ArrayList<PlanetPosition> playerPositions = new ArrayList<>();
  private PlanetPosition cpu1Pos;
  private PlanetPosition cpu2Pos;
  private PlanetPosition cpu3Pos;
  private PlanetPosition astronautPosition;
  private PlanetPosition closestPosition;

  /**
   * Normalise not needed test.
   */
  @Test
  void normaliseNotNeededTest(){
    float angle = normaliseAngle(45f);
    assertEquals(angle, 45f);
  }

  /**
   * Normalise not needed edge case test.
   */
  @Test
  void normaliseNotNeededEdgeCaseTest(){
    float lowEdge = normaliseAngle(0f);
    float upperEdge = normaliseAngle(180f);
    assertTrue(lowEdge == 0f && upperEdge == 180f);
  }

  /**
   * Normalise needed test.
   */
  @Test
  void normaliseNeededTest(){
    float angle = normaliseAngle(245f);
    assertEquals(angle, -115f);
  }

  /**
   * All players on the same side of the planet test - angles do not need normalisation.
   */
  @Test
  void onSameSideNoNormalisationTest(){
    playerPositions.clear();
    cpu1Pos = new PlanetPosition(30, 2250);
    playerPositions.add(cpu1Pos);
    cpu2Pos = new PlanetPosition(73, 2250);
    playerPositions.add(cpu2Pos);
    cpu3Pos = new PlanetPosition(176, 2250);
    playerPositions.add(cpu3Pos);
    astronautPosition = new PlanetPosition(50, 2250);
    closestPosition = findNearestPlayer(astronautPosition);
    assertEquals(closestPosition, cpu1Pos);
  }

  /**
   * All players on the same side of the planet test - angles need normalisation.
   */
  @Test
  void onSameSideNormalisationTest(){
    playerPositions.clear();
    cpu1Pos = new PlanetPosition(230, 2250);
    playerPositions.add(cpu1Pos);
    cpu2Pos = new PlanetPosition(273, 2250);
    playerPositions.add(cpu2Pos);
    cpu3Pos = new PlanetPosition(355, 2250);
    playerPositions.add(cpu3Pos);
    astronautPosition = new PlanetPosition(350, 2250);
    closestPosition = findNearestPlayer(astronautPosition);
    assertEquals(closestPosition, cpu3Pos);
  }

  /**
   * CPU astro is on the left side of the planet and the goal is on the right test.
   */
  @Test
  void astroLeftGoalRightTest(){
    playerPositions.clear();
    cpu1Pos = new PlanetPosition(30, 2250);
    playerPositions.add(cpu1Pos);
    cpu2Pos = new PlanetPosition(180, 2250);
    playerPositions.add(cpu2Pos);
    cpu3Pos = new PlanetPosition(250, 2250);
    playerPositions.add(cpu3Pos);
    astronautPosition = new PlanetPosition(330, 2250);
    closestPosition = findNearestPlayer(astronautPosition);
    assertEquals(closestPosition, cpu1Pos);
  }

  /**
   * CPU astro is on the right side of the planet and the goal is on the left test.
   */
  @Test
  void astroRightGoalLeftTest(){
    playerPositions.clear();
    cpu1Pos = new PlanetPosition(60, 2250);
    playerPositions.add(cpu1Pos);
    cpu2Pos = new PlanetPosition(250, 2250);
    playerPositions.add(cpu2Pos);
    cpu3Pos = new PlanetPosition(330, 2250);
    playerPositions.add(cpu3Pos);
    astronautPosition = new PlanetPosition(10, 2250);
    closestPosition = findNearestPlayer(astronautPosition);
    assertEquals(closestPosition, cpu3Pos);
  }

  private static PlanetPosition findNearestPlayer(PlanetPosition astroPos) {
    float astroAngle = normaliseAngle(astroPos.angle);

//    System.out.println("Astronaut angle: " + astroAngle);

    PlanetPosition nearestPlayer = null;
    float nearestAngle = Integer.MAX_VALUE;
    for (PlanetPosition playerPos : playerPositions) { float playerAng = normaliseAngle(playerPos.angle);

//      System.out.println("Checking if " + playerAng + " is closer than " + nearestAngle);
//      System.out.println(Math.abs(playerAng - astroAngle));

      if (Math.abs(playerAng - astroAngle) < nearestAngle) {

//        System.out.println(playerAng + " is closer than " + nearestAngle);

        nearestAngle = Math.abs(playerAng - astroAngle) ;
        nearestPlayer = playerPos;
      }
    }

//    System.out.println(nearestPlayer.angle + " is the closest angle");

    return nearestPlayer;
  }

  private static float normaliseAngle(float angle) {
    if (angle <= 180) {
      return angle;
    } else {
      return (angle - 360);
    }
  }

}
