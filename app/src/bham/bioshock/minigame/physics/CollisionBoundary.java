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
  float x;
  float y;
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
    this.x = pos.x;
    this.y = pos.y;
    this.rotation = rotation;
    this.setRotation((float) rotation);
    this.setPosition(x - (width/2), y);
  }
  
  public boolean collideWith(CollisionBoundary cb) {
    return Intersector.overlapConvexPolygons(this, cb);
  }
  
  public Direction getDirectionTo(CollisionBoundary cb) {
    if((x - width/2) < (cb.x + cb.width/2) && (x + width/2) > (cb.x - cb.width/2)) {
      if(cb.y > y) {
        return Direction.UP;
      } else {
        return Direction.DOWN;
      }
    }
    
    if((y - height/2) < (cb.y + cb.height/2) && (y + height/2) > (cb.y - cb.height/2)) {
      if(cb.x > x) {
        return Direction.RIGHT;
      } else {
        return Direction.LEFT;
      }
    }
    
    return Direction.NONE;
  }

}
