package bham.bioshock.minigame.physics;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SpeedVectorTests {

	@Test
	public void testApply() {
		SpeedVector v = new SpeedVector();
		
		v.apply(0, 10);
		assertEquals(0, v.dX());
		assertEquals(10, v.dY());
		v.apply(180, 10);
		assertEquals(0, v.dX());
		assertEquals(0, v.dY());
		
		v.apply(90, 100);
		assertEquals(100, v.dX());
		assertEquals(0, v.dY());
		v.apply(270, 100);
		assertEquals(0, v.dX());
		assertEquals(0, v.dY());
		
		v.apply(45, 100);
		assertEquals(v.dY(), v.dX());
		v.apply(45, -100);
		assertEquals(0, v.dX());
		assertEquals(0, v.dY());
		
		v.apply(360, 80);
		assertEquals(0, v.dX());
		assertEquals(80, v.dY());
		v.apply(540, 80);
		assertEquals(0, v.dX());
		assertEquals(0, v.dY());
		
		v.apply(0, 200);
		v.apply(90, 200);
		assertEquals(200, v.dX());
		assertEquals(200, v.dY());
		
	}
	
	@Test
	public void testSpeedAngle() {
		SpeedVector v = new SpeedVector();
		
		v.apply(0, 200);
		assertEquals(0, v.getSpeedAngle());
		v.apply(90, 200);
		assertEquals(45, v.getSpeedAngle());
		v.apply(270, 200);
		assertEquals(0, v.getSpeedAngle());
		v.apply(180, 200);
		assertEquals(0, v.getSpeedAngle());
		v.apply(180, 10);
		assertEquals(180, v.getSpeedAngle());
		v.apply(270, 10);
		assertEquals(225, v.getSpeedAngle());
	}
	
	@Test
	public void testStop() {
		SpeedVector v = new SpeedVector();
		
		v.apply(0, 200);
		v.stop(0);
		assertEquals(0, v.dX());
		assertEquals(0, v.dY());
		
		v.apply(45, 100);
		v.stop(0);
		assertEquals(70, v.dX());
		assertEquals(0, v.dY());
		
		v.stop(90);
		assertEquals(0, v.dX());
		assertEquals(0, v.dY());
		
		v.apply(270, 100);
		v.stop(225);
		assertEquals(-50, v.dX());
		assertEquals(50, v.dY());
		
		SpeedVector v2 = new SpeedVector();
		v2.apply(0, 200);
		v2.stop(180);
		assertEquals(200, v2.dY());
	}
	
	@Test
	public void testBasicCollisions() {
	  SpeedVector v1 = new SpeedVector();
	  SpeedVector v2 = new SpeedVector();
	  
	  // opposite directions
	  v1.apply(90, 100);
	  assertEquals(100, v1.dX());
	  
	  v2.apply(90, -100);
	  assertEquals(-100, v2.dX());
	  
	  // elastic collision = should swap directions
//	  v1.collide(v2, 1);
//	  assertEquals(-100, v1.dX());
//	  assertEquals(100, v2.dX());
//	  
//	  v1.stop(270);
//	  assertEquals(0, v1.dX());
//	  
//	  // elastic collision - one speed is 0
//	  v1.collide(v2, 1);
//	  assertEquals(100, v1.dX());
//	  assertEquals(0, v2.dX());
	  
	  v1.stop(v1.getSpeedAngle());
	  assertEquals(0, v1.dX());
	}
	
	@Test
	public void testCollisionsDirections() {
	   SpeedVector v1 = new SpeedVector();
	   SpeedVector v2 = new SpeedVector();
	   

	}
	
	
}
