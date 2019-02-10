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
}
