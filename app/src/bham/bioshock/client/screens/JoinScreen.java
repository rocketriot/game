package bham.bioshock.client.screens;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.JoinScreenStore;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.worlds.JoinScreenWorld;
import bham.bioshock.minigame.worlds.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class JoinScreen extends ScreenMaster {

  private Store store;
  private JoinScreenStore localStore;

  /* Container elements */
  private Holder holder;

  /* Animation Elements */
  private float stateTime = 0;
  private Texture[] loadTextures;
  private Texture[] connectedTextures;
  private Texture[] playerTextures;

  private float rocketSpeed = 200f;
  private float rotationSpeed = 1.8f;
  private int rocketWidth = 50;
  private int rocketHeight = 100;
  private float rocketRotation = 0;

  private Player mainPlayer;

  private RocketAnimation mainPlayerAnimation;
  private World world;

  public JoinScreen(Router router, Store store, Player mainPlayer) {
    super(router);
    this.store = store;
    this.localStore = store.getJoinScreenStore();
    this.mainPlayer = mainPlayer;

    loadTextures = new Texture[store.MAX_PLAYERS];
    loadTextures[0] = new Texture(Gdx.files.internal("app/assets/animations/loading1.png"));
    loadTextures[1] = new Texture(Gdx.files.internal("app/assets/animations/loading2.png"));
    loadTextures[2] = new Texture(Gdx.files.internal("app/assets/animations/loading3.png"));
    loadTextures[3] = new Texture(Gdx.files.internal("app/assets/animations/loading4.png"));

    connectedTextures = new Texture[store.MAX_PLAYERS];
    connectedTextures[0] =
        new Texture(Gdx.files.internal("app/assets/animations/connectedAnimSheet1.png"));
    connectedTextures[1] =
        new Texture(Gdx.files.internal("app/assets/animations/connectedAnimSheet2.png"));
    connectedTextures[2] =
        new Texture(Gdx.files.internal("app/assets/animations/connectedAnimSheet3.png"));
    connectedTextures[3] =
        new Texture(Gdx.files.internal("app/assets/animations/connectedAnimSheet4.png"));

    playerTextures = new Texture[store.MAX_PLAYERS];
    playerTextures[0] =
        new Texture(Gdx.files.internal("app/assets/minigame/astronauts/orange/shield.png"));
    playerTextures[1] =
        new Texture(Gdx.files.internal("app/assets/minigame/astronauts/red/shield.png"));
    playerTextures[2] =
        new Texture(Gdx.files.internal("app/assets/minigame/astronauts/green/shield.png"));
    playerTextures[3] =
        new Texture(Gdx.files.internal("app/assets/minigame/astronauts/blue/shield.png"));

    setUpHolder();
    setUpPlayerContainers();
    addStartGameButton();
    world = new JoinScreenWorld();
  }

  public RocketAnimation getMainPlayerAnimation() {
    return mainPlayerAnimation;
  }

  public void addPlayer(Player player) {
    /// create a new rocket animation
    int index = localStore.getRocketNum();
    int x = (int) ((stage.getWidth() / 4) * (index + 1) - 20);
    int y = (int) stage.getHeight() / 2;
    RocketAnimation anim = new RocketAnimation(world, x, y, index);
    // add it to the map
    localStore.addRocket(player.getId(), anim);

    // update the image in the player container
    PlayerContainer container = holder.getPlayerContainer(index);
    container.setId(player.getId());
    container.changeAnimation(playerTextures[index], 11, 1, 100, 100, 1f, 0);
    container.setWaitText(WaitText.CONNECTED);
    container.setName(player.getUsername());

    if (player.getId().equals(mainPlayer.getId())) {
      mainPlayerAnimation = anim;
    }
  }

  public void removePlayer(UUID playerId) {
    localStore.removeRocket(playerId);
    holder.resetPlayerContainer(playerId);
  }

  private void setUpHolder() {
    holder = new Holder();
    stage.addActor(holder);
  }

  private void setUpPlayerContainers() {
    for (int i = 0; i < store.MAX_PLAYERS; i++) {
      holder.addPlayerContainer(i,
          new PlayerContainer("Player" + (i + 1), WaitText.WAITING, loadTextures[i]));
    }
  }

  /* CREATE */
  @Override
  public void show() {
    super.show();
    drawBackButton();
  }

  /* RENDER */
  @Override
  public void render(float delta) {
    super.render(delta);
    
    if(store.isReconnecting()) {
      router.call(Route.DISCONNECT);
      return;
    }
    
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();
    stateTime += Gdx.graphics.getDeltaTime();

    // draw rockets
    drawRockets();

    updateRocketPosition();
  }

  private void drawRockets() {
    batch.begin();
    for (RocketAnimation r : localStore.getRockets()) {
      RocketAnimation animation = r;
      drawRocket(animation);
    }
    batch.end();
  }

  public enum WaitText {
    WAITING("Waiting..."), CONNECTED("Connected");
    final String text;

    WaitText(String text) {
      this.text = text;
    }
  }


  public class Holder extends Table {
    private ArrayList<PlayerContainer> playerContainers;
    private int padding = 20;

    public Holder() {
      this.setFillParent(true);
      this.pad(padding);
      playerContainers = new ArrayList<>();

      Label pl = new Label(mainPlayer.getUsername(), skin);
      add(pl).colspan(4).padBottom(20);
      row();
    }

    public void addPlayerContainer(int index, PlayerContainer pc) {
      playerContainers.add(index, pc);
      this.add(pc);
    }

    public void resetPlayerContainer(UUID id) {
      PlayerContainer container = null;
      int index = 0;
      for(PlayerContainer pc : playerContainers) {
        if(pc.getPlayerId().isPresent() && pc.getPlayerId().get().equals(id)) {
          container = pc;
          break;
        }
        index++;
      }
      if(container != null) {
        container.changeAnimation(loadTextures[index], 26, 1, 100, 100, 0.8f, -1);
        container.setWaitText(WaitText.WAITING);
        container.setName("Player" + (index + 1));
      }
    }
    
    public PlayerContainer getPlayerContainer(int index) {
      try {
        return playerContainers.get(index);
      } catch (IndexOutOfBoundsException e) {
        return null;
      }
    }
  }

  public class PlayerContainer extends Table {

    private Label name;
    private Label waitText;
    private StaticAnimation animation;
    private int sidePadding = 30;
    private int topPadding = 30;
    private Optional<UUID> playerId = Optional.empty();

    public PlayerContainer(String n, WaitText status, Texture sheet) {
      // this.setDebug(true);
      name = new Label(n, skin);
      waitText = new Label(status.toString(), skin);
      animation = new StaticAnimation(sheet, 26, 1, 100, 100, 0.8f, -1);

      this.pad(topPadding, sidePadding, topPadding, sidePadding);

      this.add(name);
      this.row();
      this.add(animation).height(animation.getHeight()).width(animation.getWidth()).padTop(10);
      this.row();
      this.add(waitText).padTop(10);
    }

    public void setId(UUID id) {
      playerId = Optional.of(id);
    }
    
    public Optional<UUID> getPlayerId() {
      return playerId;
    }

    public void setName(String n) {
      name.setText(n);
    }

    public void setWaitText(WaitText text) {
      waitText.setText(text.toString());
    }

    public void changeAnimation(Texture sheet, int cols, int rows, int width, int height,
        float frameDuration, int skipCol) {
      StaticAnimation newAnimation =
          new StaticAnimation(sheet, cols, rows, width, height, frameDuration, skipCol);
      this.animation = newAnimation;
    }


    public StaticAnimation getAnimation() {
      return animation;
    }

    @Override
    public void act(float delta) {
      clearChildren();
      this.add(name);
      this.row();
      this.add(animation).height(animation.getHeight()).width(animation.getWidth());
      this.row();
      this.add(waitText);

      super.act(delta);
    }
  }


  public class StaticAnimation extends Image {

    private Animation<TextureRegion> animation;
    private TextureRegion[] textureRegion;

    public StaticAnimation(Texture sheet, int cols, int rows, int width, int height,
        float frameDuration, int skipCol) {

      if (skipCol == -1) {
        textureRegion = new TextureRegion[cols * rows];
      } else {
        textureRegion = new TextureRegion[(cols - 1) * rows];
      }

      TextureRegion[][] tmp =
          TextureRegion.split(sheet, sheet.getWidth() / cols, sheet.getHeight() / rows);

      int index = 0;
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          if (j != skipCol) {
            textureRegion[index++] = tmp[i][j];
          }
        }
      }

      animation = new Animation<>(frameDuration, textureRegion);

      setWidth(width);
      setHeight(height);
    }


    public void act(float delta) {
      stateTime += delta;
      TextureRegion currentFrame = animation.getKeyFrame(stateTime += delta, true);
      TextureRegionDrawable drawable = new TextureRegionDrawable(currentFrame);
      this.setDrawable(drawable);
      super.act(delta);
    }

    public Animation<TextureRegion> getAnimation() {
      return animation;
    }

  }


  public class ButtonsContainer extends Table {
    ButtonsContainer() {
      bottom();
      right();
      setWidth(stage.getWidth());
      pad(20);

      TextButton startButton = new TextButton("Start Game", skin);
      TextButton miniGameButton = new TextButton("TEST Mini Game", skin);
      add(startButton);
      row();
      add(miniGameButton);

      startButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          router.call(Route.START_GAME);
        }
      });

      miniGameButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          router.call(Route.DIRECT_MINIGAME_START);
        }
      });
    }

    @Override
    public void act(float delta) {
      setWidth(stage.getWidth());
      super.act(delta);
    }


  }

  public void addStartGameButton() {
    ButtonsContainer buttons = new ButtonsContainer();
    stage.addActor(buttons);
  }

  @Override
  protected void setPrevious() {
    backButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        router.call(Route.DISCONNECT);
      }
    });
  }

  public static Vector2 getStageLocation(Actor actor) {
    return actor.localToStageCoordinates(new Vector2(0, 0));
  }

  private void drawRocket(RocketAnimation rocketAnimation) {
    Sprite sprite = rocketAnimation.getSprite();
    sprite.setRegion(rocketAnimation.getTexture());
    sprite.setPosition(rocketAnimation.getX() - (rocketAnimation.getWidth() / 2f),
        rocketAnimation.getY());
    sprite.setRotation((float) rocketAnimation.getRotation());
    sprite.draw(batch);
  }

  public void updateRocketPosition() {
    boolean moveMade = false;

    if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
      moveMade = true;
      mainPlayerAnimation.moveLeft();
    }

    if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
      moveMade = true;
      mainPlayerAnimation.moveRight();
    }

    if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
      moveMade = true;
      mainPlayerAnimation.moveUp();
    }

    if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
      moveMade = true;
      mainPlayerAnimation.moveDown();
    }

    if (moveMade) {
      // Send a move to the controller
      router.call(Route.JOIN_SCREEN_MOVE, mainPlayer.getId());
    }
  }

  public Position checkBounds(Position pos) {
    if (pos.x <= 0) {
      pos.x = 0;
    }
    if (pos.x >= stage.getWidth()) {
      pos.x = stage.getWidth();
    }
    if (pos.y <= 0) {
      pos.y = 0;
    }
    if (pos.y >= stage.getHeight()) {
      pos.y = stage.getHeight();
    }
    return pos;
  }

  @Override
  public void dispose() {
    batch.dispose();
    for (int i = 0; i < loadTextures.length; i++) {
      loadTextures[i].dispose();
      connectedTextures[i].dispose();
      playerTextures[i].dispose();
    }
  }

  public class RocketAnimation extends Entity {

    private Animation mainPlayerAnimation;
    private float frameDuration = 1.5f;

    public RocketAnimation(World w, float x, float y, int mainPlayerIndex) {
      super(w, x, y, false, null);
      setSpeed(0, rocketSpeed);
      setRotation(rocketRotation);

      mainPlayerAnimation = (new StaticAnimation(connectedTextures[mainPlayerIndex], 4, 1,
          rocketWidth, rocketHeight, frameDuration, -1)).getAnimation();
      load();

      sprite.setRegionWidth(rocketWidth);
      sprite.setRegionHeight(rocketHeight);
    }

    @Override
    public void load() {
      this.loaded = true;
      setState(State.LOADED);
      if (getTexture() != null) {
        sprite = new Sprite(getTexture());
        sprite.setSize(rocketWidth, rocketHeight);
        sprite.setOrigin(rocketWidth / 2f, rocketHeight);
      }
    }

    public int getWidth() {
      return rocketWidth;
    }

    public int getHeight() {
      return rocketHeight;
    }

    @Override
    public TextureRegion getTexture() {
      return (TextureRegion) mainPlayerAnimation.getKeyFrame(stateTime, true);
    }

    public void updatePosition(Position new_pos, float new_rot) {
      pos = new_pos;
      rotation = new_rot;
    }

    public Position getPosition() {
      return pos;
    }

    public void moveLeft() {
      pos.x -= Gdx.graphics.getDeltaTime() * rocketSpeed;
      moveTowards(90);
    }

    public void moveRight() {
      pos.x += Gdx.graphics.getDeltaTime() * rocketSpeed;
      moveTowards(270);
    }

    public void moveUp() {
      pos.y += Gdx.graphics.getDeltaTime() * rocketSpeed;
      moveTowards(0);
    }

    public void moveDown() {
      pos.y -= Gdx.graphics.getDeltaTime() * rocketSpeed;
      moveTowards(180);
    }

    public void moveTowards(float angle) {
      float signed = (angle - rotation + 180) % 360 - 180;
      rotation += Gdx.graphics.getDeltaTime() * rotationSpeed * signed;
    }

    @Override
    public double getRotation() {
      return rotation;
    }
  }

}
