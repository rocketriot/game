package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.Store;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Clock extends HudElement {
  private FontGenerator fontGenerator;
  private BitmapFont font;
  private BitmapFont fontRed;
  private SimpleDateFormat timeFormat;

  /** The length of the minigame */
  private float seconds = 60;

  Clock(Stage stage, SpriteBatch batch, Skin skin, Store store, Router router) {
    super(stage, batch, skin, store, router);
  }

  protected void setup() {
    fontGenerator = new FontGenerator();
    font = fontGenerator.generate(72);
    fontRed = fontGenerator.generate(72, new Color(0xFF3C48FF));

    timeFormat = new SimpleDateFormat("mm:ss");
  }

  public void render() {
    seconds -= Gdx.graphics.getDeltaTime();

    GregorianCalendar calendar = new GregorianCalendar(0, 0, 0, 0, 0, (int) seconds);
    String time = timeFormat.format(calendar.getTime());

    int x = Config.GAME_WORLD_WIDTH / 2 - 90;
    int y = Config.GAME_WORLD_HEIGHT - 50;

    batch.begin();

    if (seconds <= 10 && (int) seconds % 2 == 0) {
      fontRed.draw(batch, time, x, y);
    } else {
      font.draw(batch, time, x, y);
    }

    batch.end();
  }
}
