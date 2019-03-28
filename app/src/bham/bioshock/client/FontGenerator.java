package bham.bioshock.client;

import bham.bioshock.client.assets.Assets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class FontGenerator {
  /**
   * Generates a bitmap font (currently SpaceMono)
   *
   * @param fontSize the size of the font to be generated
   * @param color the color of the font to be generated
   * @return a bitmap font
   */
  public BitmapFont generate(int fontSize, Color color) {
    FileHandle file = Gdx.files.internal(Assets.font);
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);
    FreeTypeFontParameter parameter = new FreeTypeFontParameter();
    parameter.size = fontSize;
    parameter.color = color;

    BitmapFont font = generator.generateFont(parameter);
    font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

    generator.dispose();

    return font;
  }

  /**
   * Generates a font with a default white color
   *
   * @param fontSize the size of the font to be generated
   * @return a white bitmap font
   */
  public BitmapFont generate(int fontSize) {
    return generate(fontSize, Color.WHITE);
  }

  /**
   * Figures out the offset to align a string center
   *
   * @param font the font to generate offset of
   * @param value the string to gennerate the offset of
   * @return offset to use to center the string on the screen
   */
  public float getOffset(BitmapFont font, String value) {
    GlyphLayout glyphLayout = new GlyphLayout();
    glyphLayout.setText(font, value);

    return glyphLayout.width / 2;
  }
}
