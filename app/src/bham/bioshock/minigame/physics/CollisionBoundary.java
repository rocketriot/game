package bham.bioshock.minigame.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import bham.bioshock.common.Direction;
import bham.bioshock.common.Position;

public class CollisionBoundary extends Polygon {

  float width;
  float height;
  double rotation;
  
  public CollisionBoundary(float width, float height) {
    super(new float[]{0,0,width,0,width,height,0,height});
    this.width = width;
    this.height = height;
    this.setOrigin(width/2, 0);
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
    this.setPosition(pos.x - (width/2), pos.y);
  }
  
  public boolean collideWith(CollisionBoundary cb) {
    return Intersector.overlapConvexPolygons(this, cb);
  }
  
  public Direction getDirectionTo(CollisionBoundary cb) {
    float x = this.getX();
    float y = this.getY();
    
    if(y < (cb.getY() + cb.height) && (y + height) > cb.getY()) {
      if(cb.getX() > x) {
        return Direction.RIGHT;
      } else {
        return Direction.LEFT;
      }
    }
    
    if((x - width/2) < (cb.getX() + cb.width/2) && (x + width/2) > (cb.getX() - cb.width/2)) {
      if(cb.getY() > y) {
        return Direction.UP;
      } else {
        return Direction.DOWN;
      }
    }

    
    return Direction.NONE;
  }

}
