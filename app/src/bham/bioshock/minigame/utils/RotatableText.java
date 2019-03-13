package bham.bioshock.minigame.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData;
import com.badlogic.gdx.graphics.g2d.BitmapFont.Glyph;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.common.Position;

public class RotatableText {
  
  String text;
  BitmapFont font;
  double rotation;
  Position pos;
  SpriteBatch batch;
  
  public RotatableText(String text) {
    this.text = text;
    FontGenerator gen = new FontGenerator();
    font = gen.generate(18);
    rotation = 180f;
    pos = new Position(0,0);
    batch = new SpriteBatch();
    
  }
  
  public void update(Position pos, double rotation) {
    this.pos.x = pos.x;
    this.pos.y = pos.y;
//    this.rotation = rotation;
  }
  
  public void draw(Matrix4 camMatrix) {
//    batch.begin();
    batch.setProjectionMatrix(camMatrix);
    Matrix4 mx4Font = new Matrix4();
    Matrix4 originalMatrix = batch.getTransformMatrix();
    
 // load the background into a pixmap
//    Pixmap tile = new Pixmap();

    // load the font
//    FileHandle handle = Gdx.files.getFileHandle("someFont.fnt",
//            FileType.Internal);
//    BitmapFont font = new BitmapFont(handle);

    // get the glypth info
    font.isFlipped();
    
    mx4Font.setToRotation(new Vector3(0, 1, 0), 90);
    batch.setTransformMatrix(mx4Font);
    batch.begin();
    font.draw(batch, "TEEST", pos.x, pos.y);
    batch.end();
    
//    BitmapFontData data = font.getData();
//    Glyph g = data.getGlyph('A');
//    batch.draw(g, pos.x, pos.y);
//    Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.imagePaths[0]));
//    Glyph glyph = data.getGlyph('T');
//
//    // draw the character onto our base pixmap
//    tile.drawPixmap(fontPixmap, (TILE_WIDTH - glyph.width) / 2, (TILE_HEIGHT - glyph.height) / 2,
//            glyph.srcX, glyph.srcY, glyph.width, glyph.height);
//
//    // save this as a new texture
//    texture = new Texture(tile);
//    
    
//    TextureRegion t = font.;
//    matrix.rotate(new Vector3(0,1,0), (float) rotation);
//    batch.setTransformMatrix(matrix.rotate(new Vector3(0,1,0), (float) rotation));
//    mx4Font.setToRotation(new Vector3(200, 200, 0), (float) rotation);
//    mx4Font.setToRotation(new Vector3(200, 200, 0), (float) rotation);
//    batch.setTransformMatrix(mx4Font);
//    font.draw(batch, text, pos.x, pos.y);
//    batch.draw(t, pos.x, pos.y);
//    mx4Font.setToRotation(new Vector3(200, 200, 0), (float) -rotation);
    batch.setTransformMatrix(originalMatrix);
    
//    batch.setTransformMatrix(matrix.rotate(new Vector3(0,1,0), (float) -rotation));
//    batch.end();
//    batch.se
//    batch.setTransformMatrix(transform);
//    font.
  }
  
  
}
