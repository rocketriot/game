package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.Config;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.UUID;

/** Displays a popup for starting a minigame */
public class MinigamePrompt implements InputProcessor {
  /** Width of the popup */
  private final int WIDTH = 600;

  /** Height of the popup */
  private final int HEIGHT = 500;

  private SpriteBatch batch;
  private Stage stage;
  private FitViewport viewport;
  private Store store;
  private Router router;
  private ShapeRenderer sr;

  /** Font to use for the minigame title */
  private BitmapFont font;

  /** Font to use for the rest of the popup */
  private BitmapFont fontSmall;

  /** Specifies the planet that the popup is displaying */
  private UUID planetID = null;

  private Skin skin;

  /** Stores all the textures for the planets */
  private ArrayList<Texture> planetTextures;

  /** Specifies if the popup is visible or not */
  private boolean isVisible = false;

  /** Lets the player attempt to capture the planet */
  private TextButton yesButton;

  /** Lets the player reject capturing the planet */
  private TextButton noButton;

  /** Displays the planet that is potentially going to be captured */
  private Image image;

  public MinigamePrompt(SpriteBatch batch, Store store, Router router, Skin skin) {
    this.batch = batch;
    this.store = store;
    this.router = router;
    this.skin = skin;
    this.sr = new ShapeRenderer();

    OrthographicCamera camera = new OrthographicCamera();
    viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT, camera);
    stage = new Stage(viewport, batch);

    setup();
  }

  /** Setup the minigame prompt */
  public void setup() {
    // Setup fonts
    font = new FontGenerator().generate(60, Color.BLACK);
    fontSmall = new FontGenerator().generate(30, Color.BLACK);

    // Add all the planet textures
    planetTextures = new ArrayList<>();

    Texture texture1 = new Texture(Assets.planetsFolder + "/1.png");
    texture1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    planetTextures.add(texture1);

    Texture texture2 = new Texture(Assets.planetsFolder + "/2.png");
    texture1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    planetTextures.add(texture2);

    Texture texture3 = new Texture(Assets.planetsFolder + "/3.png");
    texture1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    planetTextures.add(texture3);

    Texture texture4 = new Texture(Assets.planetsFolder + "/4.png");
    texture1.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    planetTextures.add(texture4);

    renderIcon();
    renderButtons();
  }

  /** Renders the minigame prompts */
  public void render() {
    // Don't show the prompt if not needed
    if (!isVisible) return;

    batch.begin();
    renderName();
    renderGravity();
    renderPlanetSize();
    renderDescription();
    batch.end();
  }

  /** Renders the planet being captured in the background of the popup */
  private void renderIcon() {
    int textureId = 1;
    image = new Image(planetTextures.get(textureId));
    image.setX((Config.GAME_WORLD_WIDTH / 2) - (image.getWidth() / 2));
    image.setY((Config.GAME_WORLD_HEIGHT / 2) - (image.getHeight() / 2) + 100);
    stage.addActor(image);
  }

  /** Renders the background of the popup */
  private void renderBackground() {
    Gdx.gl.glEnable(GL30.GL_BLEND);
    Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);

    sr.begin(ShapeType.Filled);
    sr.setProjectionMatrix(batch.getProjectionMatrix());
    sr.setColor(new Color(1, 1, 1, 0.8f));
    sr.rect(
        (Config.GAME_WORLD_WIDTH / 2) - (WIDTH / 2) - 10,
        (Config.GAME_WORLD_HEIGHT / 2) - (HEIGHT / 2) - 10,
        WIDTH + 20,
        HEIGHT + 20);
    sr.end();

    Gdx.gl.glDisable(GL30.GL_BLEND);
  }

  /** Displays the title of the popup */
  private void renderName() {
    String name = "Minigame";
    float xOffset = new FontGenerator().getOffset(font, name);
    float x = (Config.GAME_WORLD_WIDTH / 2) - (WIDTH / 2) + xOffset + 30;
    float y = (Config.GAME_WORLD_HEIGHT / 2) + 130;
    font.draw(batch, name, x, y);
  }

  /** Displays the description of the popup */
  private void renderDescription() {
    String description = "Do you want to spend 30 fuel to\nattempt to capture this planet?";
    float x = (Config.GAME_WORLD_WIDTH / 2) - (WIDTH / 2) + 90;
    float y = (Config.GAME_WORLD_HEIGHT / 2) - 80;
    fontSmall.draw(batch, description, x, y);
  }

  /** Displays the gravity of the planet being captured */
  private void renderGravity() {
    float gravity = ((float) store.getGameBoard().getPlanet(planetID).getMinigameGravity()) / 2500f;
    String gravityString = String.format("Gravity: %.2fg", gravity);
    float x = (Config.GAME_WORLD_WIDTH / 2) - (WIDTH / 2) + 90;
    float y = (Config.GAME_WORLD_HEIGHT / 2) + 40;
    fontSmall.draw(batch, gravityString, x, y);
  }

  /** Displays the size of the planet being captured */
  private void renderPlanetSize() {
    int size = store.getGameBoard().getPlanet(planetID).getMinigameRadius() / 10;
    String sizeString = "Planet Size: " + size;
    float x = (Config.GAME_WORLD_WIDTH / 2) - (WIDTH / 2) + 310;
    float y = (Config.GAME_WORLD_HEIGHT / 2) + 40;
    fontSmall.draw(batch, sizeString, x, y);
  }

  /** Displays buttons to let the player to choose attack or not */
  private void renderButtons() {
    // Button to accept the minigame
    yesButton = new TextButton("  Yes  ", skin);
    yesButton.setX((Config.GAME_WORLD_WIDTH / 2) - (WIDTH / 2) + 170);
    yesButton.setY((Config.GAME_WORLD_HEIGHT / 2) - (HEIGHT / 2));
    yesButton.addListener(
        new BaseClickListener(planetID) {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            SoundController.playSound("menuSelect");
            router.call(Route.SEND_MINIGAME_START, planetId);
            isVisible = false;
          }
        });
    stage.addActor(yesButton);

    // Button to reject the minigame
    noButton = new TextButton("  No  ", skin);
    noButton.setX((Config.GAME_WORLD_WIDTH / 2) - (WIDTH / 2) + 170 + 150);
    noButton.setY((Config.GAME_WORLD_HEIGHT / 2) - (HEIGHT / 2));
    noButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            SoundController.playSound("menuSelect");
            router.call(Route.END_TURN);
            isVisible = false;
          }
        });
    stage.addActor(noButton);
  }

  /** Handles when the minigame prompt needs to be displayed */
  public void show(UUID planetID) {
    this.planetID = planetID;
    renderButtons();
    isVisible = true;
  }

  /** Returns the stage */
  public Stage getStage() {
    return stage;
  }

  /** Draws the popup if needed */
  public void draw() {
    if (!isVisible) return;

    renderBackground();
    stage.draw();
  }

  @Override
  public boolean keyDown(int keycode) {
    return false;
  }

  @Override
  public boolean keyUp(int keycode) {
    return false;
  }

  @Override
  public boolean keyTyped(char character) {
    return false;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return false;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }

  /** Click listener that takes in a planet ID */
  protected class BaseClickListener extends ClickListener {
    /** ID of the planet being potentially captured */
    protected UUID planetId;

    public BaseClickListener(UUID planetId) {
      this.planetId = planetId;
    }
  }
}
