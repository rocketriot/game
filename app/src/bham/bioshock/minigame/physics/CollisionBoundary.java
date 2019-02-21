package bham.bioshock.minigame.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;
import bham.bioshock.minigame.worlds.World;
import bham.bioshock.minigame.worlds.World.PlanetPosition;

public class CollisionBoundary extends Polygon {

  float width;
  float height;
  double rotation;

  public CollisionBoundary(float width, float height) {
    super(new float[] {0, 0, width, 0, width, height, 0, height});
    this.width = width;
    this.height = height;
    this.setOrigin(width / 2, 0);
  }

  public void draw(ShapeRenderer shapeRenderer) {
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(Color.BLACK);
    shapeRenderer.polygon(getTransformedVertices());
    shapeRenderer.end();
  }

  public void update(Position pos, double rotation) {
    this.rotation = rotation;
    this.setRotation((float) rotation);
    this.setPosition(pos.x - width/2, pos.y);
  }

  public boolean collideWith(CollisionBoundary cb) {
    return Intersector.overlapConvexPolygons(this, cb);
  }
  
  public PlanetPosition planetPosition(World world) {
    return world.convert(new Position(getX(), getY()));
  }

  public Direction getDirectionTo(World world, CollisionBoundary cb) {
    PlanetPosition pp = planetPosition(world);
    PlanetPosition pp2 = cb.planetPosition(world);
    
    if (pp.fromCenter < (pp2.fromCenter + cb.height) && (pp.fromCenter + height) > pp2.fromCenter) {
      if (pp2.angle > pp.angle) {
        return Direction.RIGHT;
      } else {
        return Direction.LEFT;
      }
    }

    if ((pp.angle - width / 2) < (pp2.angle + cb.width / 2)
        && (pp.angle + width / 2) > (pp2.angle - cb.width / 2)) {
      if (pp2.fromCenter > pp.fromCenter) {
        return Direction.UP;
      } else {
        return Direction.DOWN;
      }
    }

    return Direction.NONE;
  }

}
