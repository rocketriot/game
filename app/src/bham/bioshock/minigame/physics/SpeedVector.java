package bham.bioshock.minigame.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import bham.bioshock.common.Position;
import bham.bioshock.communication.Sendable;

public class SpeedVector extends Sendable {
  
  private static final long serialVersionUID = -149485768919020676L;
  
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
    double x1 = round(acceleration * Math.sin(radians));
    double y1 = round(acceleration * Math.cos(radians));

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
    double speedAngle = Math.asin(dx / length);
    if (dy < 0) {
      speedAngle = Math.PI - speedAngle;
    }
    return round(Math.toDegrees(speedAngle));
  }

  public void stop(double angleDegrees) {
    Vector v = stopVector(angleDegrees);
    dx -= v.dx;
    dy -= v.dy;
  }

  public void friction(double u) {
    Vector v = stopVector(getSpeedAngle());
    dx -= round(v.dx * u);
    dy -= round(v.dy * u);
  }

  private Vector stopVector(double angleDegrees) {
    double angle = Math.toRadians(angleDegrees);
    double length = getValue();
    double speedAngle = (getSpeedAngle() + 360) % 360;
    double normalizedDegrees = (angleDegrees + 360) % 360;
    if (length == 0) return new Vector(0, 0);
    if( Math.abs(normalizedDegrees - speedAngle) >= 120) {
      return new Vector(0,0);
    }
    double da = Math.toRadians(speedAngle - angleDegrees);

    double groundV = Math.cos(da) * length;
    double dx1 = Math.sin(angle) * groundV;
    double dy1 = Math.cos(angle) * groundV;

    return new Vector(round(dx1), round(dy1));
  }

  public double dX() {
    return dx;
  }

  public double dY() {
    return dy;
  }
  
  public void draw(ShapeRenderer shapeRenderer, Position pos) {
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(Color.RED);
    shapeRenderer.line(pos.x, pos.y, (float) (pos.x+dx), (float) (pos.y+dy));
    shapeRenderer.end();
  }

  public void collide(SpeedVector speed, float elastic) {
    double initAngle = this.getSpeedAngle();
    double initValue = this.getValue();
    
    double initAngle2 = speed.getSpeedAngle();
    double initValue2 = speed.getValue();
    
    this.stop(initAngle);
    speed.stop(initAngle2);
    
    this.apply(initAngle2, initValue2);
    speed.apply(initAngle, initValue);
  }
}
