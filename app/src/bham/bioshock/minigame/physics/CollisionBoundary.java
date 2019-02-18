package bham.bioshock.minigame.physics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import bham.bioshock.common.Position;

public class CollisionBoundary {

  Position pos;
  double rotation;
  float width;
  float height;
  
  public CollisionBoundary(float width, float height) {
    this.width = width;
    this.height = height;
  }
  
  public void draw(ShapeRenderer shapeRenderer) {
    if(pos == null) return;
    shapeRenderer.begin(ShapeType.Line);
    shapeRenderer.setColor(Color.BLACK);
    shapeRenderer.rect(pos.x - (width/2), pos.y, (width/2), 0, width, height, 1, 1, (float) rotation);
    shapeRenderer.end();
  }


  public void update(Position pos, double rotation) {
    this.pos = pos;
    this.rotation = rotation;
  }

}
