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
		double x1 = round( acceleration * Math.sin(radians) );
		double y1 = round( acceleration * Math.cos(radians) );
		
		dx += x1;
		dy += y1;
	}
	
	private double round(double value) {
		return Math.round(value * 10000) / 10000;
	}
	
	public double getValue() {
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	public double getSpeedAngle() {
		double length = getValue();
		double speedAngle = Math.asin(dx/length);
		if(dy < 0) {
			speedAngle = Math.PI - speedAngle;
		}
		return round( Math.toDegrees(speedAngle) );
	}
	
	public void stop(double angleDegrees) {
		Vector v = stopVector(angleDegrees);
		dx -= v.dx;
		dy -= v.dy;
	}
	
	public void friction(double u) {
		Vector v = stopVector(getSpeedAngle());
		dx -= v.dx * u;
		dy -= v.dy * u;
	}
	
	private Vector stopVector(double angleDegrees) {
		double angle = Math.toRadians(angleDegrees);
		double length = getValue();
		if(length == 0) return new Vector(0, 0);
		double da = Math.toRadians(getSpeedAngle() - angleDegrees);
		
		double groundV = Math.cos(da) * length;
		double dx1 = Math.sin(angle) * groundV;
		double dy1 = Math.cos(angle) * groundV;
		
		return new Vector(round(dx1), round(dy1));
	}
	
	public double dX() { return dx; }
	public double dY() { return dy; }
	
	private class Vector {
		public double dx;
		public double dy;
		
		public Vector(double dx, double dy) {
			this.dx = dx;
			this.dy = dy;
		}
	}
}