package bham.bioshock.minigame.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.worlds.World.PlanetPosition;

public class CollisionBoundary extends Polygon {

  float width;
  float height;
  float offsetX = 0;
  float offsetY = 0;
  double rotation;
  private MinimumTranslationVector collisionVector;

  public CollisionBoundary(float width, float height) {
    super(new float[]{0, 0, width, 0, width, height, 0, height});
    this.width = width;
    this.height = height;
    this.setOrigin(width / 2, 0);
  }

  public void draw(ShapeRenderer shapeRenderer, Color c) {
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(c);
    shapeRenderer.polygon(getTransformedVertices());
    shapeRenderer.end();
  }  

  public void update(Position pos, double rotation) {
    this.rotation = rotation;
    this.setRotation((float) rotation);
    this.setPosition((pos.x - width / 2) + offsetX, pos.y + offsetY);
  }

  public boolean collideWith(Polygon p, MinimumTranslationVector v) {
    return Intersector.overlapConvexPolygons(this, p, v);
  }

  public PlanetPosition planetPosition(World world) {
    return world.convert(new Position(getX(), getY()));
  }

  public Direction getDirectionTo(World world, CollisionBoundary cb, MinimumTranslationVector v) {

    PlanetPosition pp = planetPosition(world);
    PlanetPosition pp2 = cb.planetPosition(world);
    double angleRatio = world.angleRatio(pp.fromCenter);
    double ppWidth = (width / 2) * angleRatio;
    double pp2Width = (cb.width / 2) * angleRatio;

    boolean yCollide = pp.fromCenter < (pp2.fromCenter + cb.height) && (pp.fromCenter + height) > pp2.fromCenter;
    boolean xCollide = (pp.angle - ppWidth) < (pp2.angle + pp2Width) && (pp.angle + ppWidth) > (pp2.angle - pp2Width);

    if (xCollide && !yCollide) {
      if (pp2.angle > pp.angle) {
        return Direction.RIGHT;
      } else {
        return Direction.LEFT;
      }
    }

    if (!xCollide && yCollide) {
      if (pp2.fromCenter > pp.fromCenter) {
        return Direction.UP;
      } else {
        return Direction.DOWN;
      }
    }

    return Direction.NONE;
  }

  public void offsetX(float offsetX) {
    this.offsetX = offsetX;
  }
  public void offsetY(float offsetY) {
    this.offsetY = offsetY;
  }
}
