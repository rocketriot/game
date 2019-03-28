package bham.bioshock.client.screens;

import bham.bioshock.Config;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.controllers.SoundController;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import java.util.ArrayList;

public class EndScreen extends ScreenMaster {
  private Store store;
  private FontGenerator fontGenerator;
  private BitmapFont titleFont;
  private ArrayList<BitmapFont> pointsFonts;
  private BitmapFont font;
  private ArrayList<Player> sortedPlayers;
  private ParticleEffect rocketTrail;
  HorizontalGroup playersContainer;
  private ArrayList<Image> rockets;

  private final String TITLE = "Final Results";
  private final int PLAYER_WIDTH = 400;
  private final int MAX_PLAYER_HEIGHT = 600;
  private final int HORIZONTAL_PLAYERS_PADDING = (Config.GAME_WORLD_WIDTH - (PLAYER_WIDTH * 4)) / 2;

  public EndScreen(Router router, Store store, AssetContainer assets) {
    super(router, assets);

    this.store = store;
    this.fontGenerator = new FontGenerator();

    pointsFonts = new ArrayList<>();
    pointsFonts.add(assets.getFont(40, new Color(0xFFD048FF)));
    pointsFonts.add(assets.getFont(40, new Color(0xC2C2C2FF)));
    pointsFonts.add(assets.getFont(40, new Color(0xD27114FF)));
    pointsFonts.add(assets.getFont(40, new Color(0xCE2424FF)));

    rockets = new ArrayList<>();
    for (int i = 1; i <= 4; i++) {
      Texture texture = new Texture(Gdx.files.internal(Assets.playersSmallFolder + "/" + i + ".png"));
      texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
      rockets.add(new Image(texture));
    }
  }

  @Override
  public void show() {
    super.show();

    titleFont = assets.getFont(60);
    font = assets.getFont(40);
    sortedPlayers = store.getSortedPlayers();

    rocketTrail = new ParticleEffect();
    rocketTrail.load(
        Gdx.files.internal(Assets.particleEffect),
        Gdx.files.internal(Assets.particleEffectsFolder));
    rocketTrail.start();

    playersContainer = new HorizontalGroup();
    playersContainer.setFillParent(true);
    playersContainer.center();
    stage.addActor(playersContainer);
    
  }

  @Override
  public void render(float delta) {
    super.render(delta);

    batch.begin();
    renderTitle();
    renderFinishButton();
    renderResults();
    batch.end();
  }

  private void renderTitle() {
    int xOffset = (int) fontGenerator.getOffset(titleFont, TITLE);
    titleFont.draw(batch, TITLE, Config.GAME_WORLD_WIDTH / 2 - xOffset, Config.GAME_WORLD_HEIGHT - 100);
  }
  
  private void renderResults() {    
    int i = 0;
    
    for (Player player : store.getPlayers()) {
      int position = 0;
      for (Player sortedPlayer: sortedPlayers) {
        if (sortedPlayer.getId().equals(player.getId())) break;
        position++;
      }

      int height = 0;

      if (sortedPlayers.get(0).getPoints() != 0 && player.getPoints() != 0)
        height = (int) (MAX_PLAYER_HEIGHT * ((float) player.getPoints() / (float) sortedPlayers.get(0).getPoints()));

      // Player username label
      String username = player.getUsername();
      int xOffset = (int) fontGenerator.getOffset(font, username);
      font.draw(batch, username, HORIZONTAL_PLAYERS_PADDING + (i*PLAYER_WIDTH) - xOffset + (PLAYER_WIDTH/2), height + 220);

      // Player points label
      String points = String.valueOf(player.getPoints());
      xOffset = (int) fontGenerator.getOffset(pointsFonts.get(i), points);
      pointsFonts.get(position).draw(batch, points, HORIZONTAL_PLAYERS_PADDING + (i*PLAYER_WIDTH) - xOffset + (PLAYER_WIDTH/2), height + 170);
      
      // Player rocket
      Image rocket = rockets.get(i);
      rocket.setPosition(HORIZONTAL_PLAYERS_PADDING + (i*PLAYER_WIDTH) + (PLAYER_WIDTH/2) - (rocket.getWidth()/2), height);
      stage.addActor(rocket);

      setRocketTrailAngle(0);
      rocketTrail.setPosition(HORIZONTAL_PLAYERS_PADDING + (i*PLAYER_WIDTH) + (PLAYER_WIDTH/2) - 3, height);
      rocketTrail.draw(batch, Gdx.graphics.getDeltaTime());
      i++;
    }
  }

  private void setRocketTrailAngle(float angle) {
    // Align particle effect angle with world
    angle -= 90;

    for (ParticleEmitter pe : rocketTrail.getEmitters()) {
      ParticleEmitter.ScaledNumericValue val = pe.getAngle();
      float amplitude = (val.getHighMax() - val.getHighMin()) / 2f;
      float h1 = angle + amplitude;
      float h2 = angle - amplitude;

      val.setHigh(h1, h2);
      val.setLow(angle);
    }
  }

  private void renderFinishButton() {
    Image finishButton = drawAsset(Assets.finishButton, Config.GAME_WORLD_WIDTH - 280, 0);
      addListener(finishButton, new BaseClickListener(finishButton, Assets.finishButton, Assets.finishButtonHover) {
        @Override
        public void clicked(InputEvent event, float x, float y) {
          SoundController.playSound("menuSelect");
          router.call(Route.FADE_OUT, "boardGame");
          router.call(Route.MAIN_MENU);
        }
      });
      
      stage.addActor(finishButton);
  }

  @Override
  public void dispose() {
    super.dispose();

    for (int i = 0; i <= 3; i++) {
      ((BaseClickListener) rockets.get(i).getListeners().get(0)).dispose();
    }
  }
}