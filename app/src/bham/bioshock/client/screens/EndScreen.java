package bham.bioshock.client.screens;

import bham.bioshock.Config;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.common.Position;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import java.util.ArrayList;
import java.util.Comparator;

public class EndScreen extends ScreenMaster {
  private Store store;
  private ArrayList<Player> winners;
  private OrthographicCamera camera;
  private ShapeRenderer renderer;
  private SpriteBatch fontBatch;
  private FontGenerator fontGenerator;
  private BitmapFont font;
  private Position[] locations;
  private ArrayList<Player> players;
  private VerticalGroup hg;
  private Table table;
  private Label userLabel;
  private int number =0;
  private BitmapFont scores;

  public EndScreen(Router router, Store store, AssetContainer assets) {
    super(router, assets);
    this.store = store;

    this.camera = new OrthographicCamera();
    this.batch = new SpriteBatch();
    this.fontBatch = new SpriteBatch();
    this.fontGenerator = new FontGenerator();
    this.number = store.getPlayers().size();
    this.players = store.getSortedPlayers();

    this.table = new Table();

    table.setX(screenWidth - screenWidth/3);
    table.setY(screenHeight-screenHeight/4);

    viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT, camera);
    stage = new Stage(viewport, batch);

    load();
  }


  @Override
  public void show() {
    super.show();
  }

  @Override
  public void render(float delta) {
    super.render(delta);

    font();
    stage.draw();
  }

  /**
   * Displayes "Final Results"
   */
  private void font(){
    fontBatch.begin();

    int x = (int)screenWidth/2;
    int y = (int)screenHeight - (int)screenHeight/6;

    font.draw(fontBatch, "Final Results", x, y);
    fontBatch.end();
  }

  /**
   * Displays the players, the scores and the rockets
   */
  private void load(){
    font = fontGenerator.generate(45, Color.WHITE);

    for(int i= 0 ;i<players.size(); i++) {

      // The score will have a different color depending on the place the player is on: gold, silver, bronze, nothing
      switch (i) {
        case 0:
          scores = fontGenerator.generate(50, Color.GOLD);
          break;
        case 1:
          scores = fontGenerator.generate(50, Color.GRAY);
          break;
        case 2:
          scores = fontGenerator.generate(50, Color.BROWN);
          break;

        case 3:
          scores = fontGenerator.generate(50, Color.WHITE);
          break;
      }


      hg = new VerticalGroup();
      hg.setFillParent(true);

      Label.LabelStyle style = new Label.LabelStyle();
      Label.LabelStyle scoreStyle = new Label.LabelStyle();
      scoreStyle.font = scores;
      style.font = font;

      // username label
      userLabel = new Label(players.get(i).getUsername(), style);
      hg.addActor(userLabel);

      // score label
      userLabel = new Label(Integer.toString(players.get(i).getPoints()), scoreStyle);
      hg.addActor(userLabel);

      // get the player texture, create image and add itto the evrtical group
      int textureId = players.get(i).getTextureID() + 1;
      TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/entities/players/" + textureId + ".png")));
      Image img = new Image(texture);

      // use additional table TO RESIZE ROCKETS
      Table helper = new Table();
      helper.add(img).size(300);
      hg.addActor((helper));

    // if the player score is lower than the one before display the player lower// otherwise display on the same lever
    if(i > 0 && (players.get(i).getPoints() != players.get(i-1).getPoints())){
      int j = number - 1;
      while (j < players.size()) {
        userLabel = new Label(" ", style);
        hg.addActor(userLabel);
        j++;
      }
      number--;
    }
        table.add(hg);

      // add spaces between rockets
      userLabel = new Label("  ", style);
      table.add(userLabel);
      table.add(userLabel);
      table.add(userLabel);
      table.add(userLabel);
      table.add(userLabel);
    }

    stage.addActor(table);
    stage.act(Gdx.graphics.getDeltaTime());
  }
}


