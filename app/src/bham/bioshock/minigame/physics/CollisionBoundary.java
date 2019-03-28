package bham.bioshock.minigame.physics;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.PlanetPosition;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Polygon;

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

  public void draw(ShapeRenderer shapeRenderer, Color c) {
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(c);
    shapeRenderer.polygon(getTransformedVertices());
    shapeRenderer.end();
  }

  public void update(Position pos, double rotation) {
    this.rotation = rotation;
    this.setRotation((float) rotation);
    this.setPosition((pos.x - width / 2), pos.y);
  }

  public boolean collideWith(Polygon p, MinimumTranslationVector v) {
    return Intersector.overlapConvexPolygons(this, p, v);
  }

  public PlanetPosition planetPosition(World world) {
    return world.convert(new Position(getX(), getY()));
  }

  public CollisionBoundary clone() {
    return new CollisionBoundary(width, height);
  }
}
