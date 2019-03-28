package bham.bioshock.client.screens;

import bham.bioshock.Config;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.assets.Assets.GamePart;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class InitLoadingScreen implements Screen {

  protected Texture background;
  private AssetContainer assets;
  private Router router;
  private Image loadingImage;
  private Table table;
  private Animation<TextureRegion> loading;
  private float animationTime = 0;
  private boolean loadingShown = false;
  private FitViewport viewport;
  private Stage stage;
  private SpriteBatch batch;
  private Label progressLabel;

  public InitLoadingScreen(Router router, AssetContainer assets) {
    this.assets = assets;
    this.router = router;
  }

  public void load() {
    // First for loading
    assets.load(Assets.loading, Texture.class, GamePart.MENU);
    assets.load(Assets.menuBackground, Texture.class, GamePart.MENU);

    assets.load(Assets.skin, Skin.class, GamePart.MENU);
    assets.load(Assets.gameBackground, Texture.class, GamePart.MENU);
    assets.load(Assets.cursor, Pixmap.class, GamePart.MENU);
    assets.load(Assets.logo, Texture.class, GamePart.MENU);

    // Menu buttons
    assets.load(Assets.hostButton, Texture.class, GamePart.MENU);
    assets.load(Assets.hostButtonHover, Texture.class, GamePart.MENU);

    assets.load(Assets.joinButton, Texture.class, GamePart.MENU);
    assets.load(Assets.joinButtonHover, Texture.class, GamePart.MENU);

    assets.load(Assets.howToPlayButton, Texture.class, GamePart.MENU);
    assets.load(Assets.howToPlayButtonHover, Texture.class, GamePart.MENU);

    assets.load(Assets.preferencesButton, Texture.class, GamePart.MENU);
    assets.load(Assets.preferencesButtonHover, Texture.class, GamePart.MENU);

    assets.load(Assets.exitButton, Texture.class, GamePart.MENU);
    assets.load(Assets.exitButtonHover, Texture.class, GamePart.MENU);

    assets.load(Assets.startButton, Texture.class, GamePart.MENU);
    assets.load(Assets.startButtonHover, Texture.class, GamePart.MENU);

    assets.load(Assets.finishButton, Texture.class, GamePart.MENU);
    assets.load(Assets.finishButtonHover, Texture.class, GamePart.MENU);

    // Music
    assets.load(Assets.mainMenuMusic, Sound.class, GamePart.MENU);
    assets.load(Assets.gameBoardMusic, Sound.class, GamePart.MENU);
    assets.load(Assets.miniGameMusic, Sound.class, GamePart.MENU);
    assets.load(Assets.menuSelectSound, Sound.class, GamePart.MENU);
    assets.load(Assets.rocketSound, Sound.class, GamePart.MENU);
    assets.load(Assets.jumpSound, Sound.class, GamePart.MENU);
    assets.load(Assets.laserSound, Sound.class, GamePart.MENU);
    assets.load(Assets.healthPickupSound, Sound.class, GamePart.MENU);
    assets.load(Assets.blackHoleSound, Sound.class, GamePart.MENU);
    assets.load(Assets.fuelSound, Sound.class, GamePart.MENU);
    assets.load(Assets.upgradeSound, Sound.class, GamePart.MENU);

    // Join Screen
    for (int i = 0; i < 4; i++) {
      assets.load(Assets.loadingBase + (i + 1) + ".png", Texture.class, GamePart.MENU);
    }
    for (int i = 0; i < 4; i++) {
      assets.load(Assets.connectedBase + (i + 1) + ".png", Texture.class, GamePart.MENU);
    }
    String[] colours = new String[] {"orange", "red", "green", "blue"};
    for (int i = 0; i < 4; i++) {
      assets.load(Assets.astroBase + colours[i] + Assets.astroShield, Texture.class, GamePart.MENU);
    }
  }

  public void loaded() {
    // Setup cursor
    Pixmap pm = assets.get(Assets.cursor, Pixmap.class);
    Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));

    router.call(Route.MAIN_MENU);
  }

  @Override
  public void show() {
    this.load();

    loadingImage = new Image();
    loadingImage.setSize(200, 200);

    table = new Table();
    table.setFillParent(true);
  }

  public void genAnimation() {
    Texture t = assets.get(Assets.loading, Texture.class);
    t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    TextureRegion[][] list = TextureRegion.split(t, t.getWidth() / 26, t.getHeight());
    loading = Assets.textureToAnimation(list, 26, 0, 0.05f);

    viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT);
    stage = new Stage(viewport);
    batch = new SpriteBatch();
  }

  public void renderTable() {
    Table container = new Table();
    container.addActor(loadingImage);
    table.add(container).width(200).height(200).pad(20);
    table.row();
    stage.addActor(table);
    progressLabel = new Label(getProgress(), new LabelStyle(assets.getFont(50), Color.WHITE));
    table.add(progressLabel).width(progressLabel.getPrefWidth()).center();
    table.row();
  }

  public String getProgress() {
    return ((int) Math.floor(assets.getProgress() * 100)) + "%";
  }

  @Override
  public void render(float delta) {
    boolean finished = assets.update();

    if (finished) {
      this.loaded();
      return;
    }

    if (assets.isLoaded(Assets.loading)
        && assets.isLoaded(Assets.menuBackground)
        && !loadingShown) {
      background = assets.get(Assets.menuBackground, Texture.class);
      genAnimation();
      renderTable();
      loadingShown = true;
    }

    if (loadingShown) {
      Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
      batch
          .getProjectionMatrix()
          .setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

      progressLabel.setText(getProgress());

      drawBackground();
      stage.act(delta);
      stage.draw();
      animationTime += delta;

      TextureRegion currentFrame = loading.getKeyFrame(animationTime, true);
      TextureRegionDrawable drawable = new TextureRegionDrawable(currentFrame);
      loadingImage.setDrawable(drawable);
    }
  }

  protected void drawBackground() {
    batch.begin();
    batch.disableBlending();
    batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    batch.enableBlending();
    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    if (stage != null) {
      stage.getViewport().update(width, height, true);
      stage.act(Gdx.graphics.getDeltaTime());
    }
  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub

  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub

  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }
}
