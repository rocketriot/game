package bham.bioshock.minigame.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.common.Position;

public class RotatableText {
  
  String text;
  BitmapFont font;
  float rotation;
  Position pos;
  GlyphLayout layout;
  
  public RotatableText(String text) {
    this.text = text;
    FontGenerator gen = new FontGenerator();
    font = gen.generate(18);
    rotation = 180f;
    pos = new Position(0,0); 
    layout = new GlyphLayout();
    layout.setText(font, text);
  }
  
  public void update(Position pos, double rotation) {
    this.pos.x = pos.x;
    this.pos.y = pos.y;
    this.rotation = (float) rotation;
  }
  
  public void draw(SpriteBatch batch) {   

    Matrix4 mx4Font = new Matrix4();
    
    // Save original batch matrix 
    Matrix4 originalMatrix = batch.getTransformMatrix();
    
    // Rotate batch to target position and rotation
    Quaternion q = new Quaternion();
    q.set(new Vector3(0, 0, 1), rotation);
    mx4Font.set(new Vector3(pos.x, pos.y, 0), q);
    
    // Draw the text
    batch.setTransformMatrix(mx4Font);
    batch.begin();
    font.draw(batch, text, -(layout.width/2), 0);
    batch.end();
    batch.setTransformMatrix(originalMatrix);
  }


  public String getText() {
    return text;
  }
}
