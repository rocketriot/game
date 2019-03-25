package bham.bioshock.client.screens;

import bham.bioshock.client.Assets;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;
import bham.bioshock.client.gameLogic.gameboard.DrawPlayer;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.Coordinates;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;

public class EndScreen extends ScreenMaster {
  private Store store;
  private ArrayList<Player> winners;
  private OrthographicCamera camera;
  private ShapeRenderer renderer;
  private SpriteBatch fontBatch;
  private DrawPlayer drawPlayer;
  private FontGenerator fontGenerator;
  private BitmapFont font;

  public EndScreen(Router router, Store store) {
    super(router);
    this.store = store;
    this.winners = store.getWinner();

    this.camera = new OrthographicCamera();
    this.batch = new SpriteBatch();
    this.fontBatch = new SpriteBatch();
    this.renderer = new ShapeRenderer();

    this.viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT, camera);
    this.viewport.apply();

    displayWinner();

  }


  @Override
  public void show() {
    super.show();
  }

  @Override
  public void render(float delta) {
    super.render(delta);
    displayWinner();
    String name = winners.get(0).getUsername();
    font(name);

  }

  public void displayWinner() {
    batch.begin();
    winners = store.getWinner();



      int textureId = winners.get(0).getTextureID() +1 ;
    System.out.println(textureId);



      TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/entities/players/" + textureId + ".png")));

      Sprite sprite = new Sprite(texture);

      sprite.setSize(200,200);
      sprite.setOriginCenter();

      sprite.setX(200);
      sprite.setY(200);
      sprite.draw(batch);
      batch.end();


  }

  public void tie(){

  }

  public void font(String text){

    fontGenerator = new FontGenerator();
    font = fontGenerator.generate(100, Color.WHITE);

    fontBatch.begin();
    int x = 100;
    int y = 100;

    font.draw(fontBatch, text, x, y);
    fontBatch.end();

  }
}


