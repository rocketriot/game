package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.Config;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

/** Displays the players fuel in the game board */
public class FuelBar extends HudElement {
  /** Used to render the fuel bar */
  private ShapeRenderer sr;

  /** Displays the amount of fuel left */
  private Label fuelValueLabel;

  /** Fuel indicator */
  private Label fuelLabel;

  private float fuelWidth = 48f;
  private float fuelPadding = 50f;
  private float fuelBorderSize = 12f;
  private float fuelMaxHeight;
  private float fuelXCoordinate;

  FuelBar(Stage stage, SpriteBatch batch, AssetContainer assets, Store store, Router router) {
    super(stage, batch, assets, store, router);

    sr = new ShapeRenderer();

    fuelMaxHeight = Config.GAME_WORLD_HEIGHT - (fuelPadding * 2) - 50f;
    fuelXCoordinate = Config.GAME_WORLD_WIDTH - (fuelWidth + fuelPadding);
  }

  /** Setup the fuel bar */
  protected void setup() {
    // Setup fuel labels
    VerticalGroup fuelInfo = new VerticalGroup();
    fuelInfo.setFillParent(true);
    fuelInfo.bottom();
    fuelInfo.right();
    fuelInfo.padRight(50);
    fuelInfo.padBottom(15);
    stage.addActor(fuelInfo);

    LabelStyle style = new LabelStyle();
    style.font = assets.getFont(20);

    LabelStyle style1 = new LabelStyle();
    style1.font = assets.getFont(25);

    fuelValueLabel = new Label(String.format("%.0f", store.getMainPlayer().getMaxFuel()), style1);
    fuelValueLabel.setFontScale(1.2f);
    fuelValueLabel.setWidth(60);
    fuelValueLabel.setAlignment(Align.center);
    fuelInfo.addActor(fuelValueLabel);

    fuelLabel = new Label("FUEL", style);
    fuelLabel.setFontScale(1.2f);
    fuelLabel.setWidth(60);
    fuelLabel.setAlignment(Align.center);
    fuelInfo.addActor(fuelLabel);
  }

  /** Render the fuel bar */
  public void render(float fuelValue) {
    float height = (fuelValue / store.getMainPlayer().getMaxFuel()) * fuelMaxHeight;

    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

    sr.begin(ShapeType.Filled);
    sr.setProjectionMatrix(batch.getProjectionMatrix());

    // Fuel bar border
    sr.setColor(new Color(1, 1, 1, 0.2f));
    sr.rect(
        fuelXCoordinate - fuelBorderSize,
        fuelPadding - fuelBorderSize + 50f,
        fuelWidth + (fuelBorderSize * 2),
        fuelMaxHeight + (fuelBorderSize * 2));
    sr.end();
    Gdx.gl.glDisable(GL30.GL_BLEND);

    sr.begin(ShapeType.Filled);
    sr.setProjectionMatrix(batch.getProjectionMatrix());

    // 75% to 100%
    sr.setColor(new Color(0xFFE04AFF));
    float height1 = Float.min(fuelMaxHeight, height);
    sr.rect(fuelXCoordinate, fuelPadding + 50f, fuelWidth, height1);

    // 50% to 75%
    sr.setColor(new Color(0xFFA947FF));
    float height2 = Float.min(fuelMaxHeight * 0.75f, height);
    sr.rect(fuelXCoordinate, fuelPadding + 50f, fuelWidth, height2);

    // 25% to 50%
    sr.setColor(new Color(0xFF8343FF));
    float height3 = Float.min(fuelMaxHeight * 0.50f, height);
    sr.rect(fuelXCoordinate, fuelPadding + 50f, fuelWidth, height3);

    // 0% to 25%
    sr.setColor(new Color(0xFF433EFF));
    float height4 = Float.min(fuelMaxHeight * 0.25f, height);
    sr.rect(fuelXCoordinate, fuelPadding + 50f, fuelWidth, height4);

    sr.end();

    batch.begin();
    fuelValueLabel.setText(String.format("%.0f", fuelValue));
    batch.end();
  }
}
