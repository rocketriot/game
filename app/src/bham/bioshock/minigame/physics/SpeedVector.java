package bham.bioshock.minigame.physics;

public class SpeedVector {
	
	private double mass = 1;
	private double dx = 0;
	private double dy = 0;
		
	public SpeedVector(double _mass) {
		mass = _mass;
	}
	public SpeedVector() {};
	
	/*
	 * UP: angle 0
	 * RIGHT: angle 90
	 */
	public void apply(double angle, double force) {
		double acceleration = force / mass;
		double radians = Math.toRadians(angle);
		double x1 = acceleration * Math.sin(radians);
		double y1 = acceleration * Math.cos(radians);
		
		dx += x1;
		dy += y1;
	}
	
	public void stop() {
		dx = 0;
		dy = 0;
	}
	
	public double dX() { return dx; }
	public double dY() { return dy; }
}
