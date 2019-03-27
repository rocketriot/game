package bham.bioshock.client.scenes.minigame;

import bham.bioshock.Config;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.store.Store;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class HudClock extends HudElement {
  
  private BitmapFont font;
  private BitmapFont fontRed;
  private SimpleDateFormat timeFormat;

  /** The length of the minigame */
  private float seconds = 60;

  HudClock(Stage stage, SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    super(stage, batch, assets, store, router);
  }

  protected void setup() {
    font = assets.getFont(72);
    fontRed = assets.getFont(72, new Color(0xFF3C48FF));

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
