package bham.bioshock.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontGenerator {
  /**
   * Generates a bitmap font (currently SpaceMono)
   * @param fontSize the size of the font to be generated
   * @param color the color of the font to be generated
   * @return a bitmap font
   */
  public BitmapFont genFont(int fontSize, Color color) {
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("app/assets/fonts/SpaceMono-Bold.ttf"));
    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    parameter.size = fontSize;
    parameter.color = color;
    BitmapFont font = generator.generateFont(parameter);
    generator.dispose();
    return font;
  }
}
