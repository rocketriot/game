package bham.bioshock.client.screens;

import bham.bioshock.client.Assets;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;
import bham.bioshock.client.gameLogic.gameboard.DrawPlayer;
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
  private Sprite winner;

  public EndScreen(Router router, Store store) {
    super(router);
    this.store = store;
    this.winners = store.getWinner();

    this.camera = new OrthographicCamera();
    this.batch = new SpriteBatch();
    this.fontBatch = new SpriteBatch();
    this.renderer = new ShapeRenderer();
    if(!tie()){
      winners = store.getWinner();
      int textureId = winners.get(0).getTextureID() + 1;

      TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/entities/players/" + textureId + ".png")));
      winner = new Sprite(texture);

      winner.setSize(200, 200);
      winner.setOriginCenter();

      winner.setX(200);
      winner.setY(200);
    }

    //this.viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, cam);
   // this.viewport.apply();


  }


  @Override
  public void show() {
    super.show();
  }

  @Override
  public void render(float delta) {
    super.render(delta);
    displayWinner();
    font();

  }

  public boolean tie(){
    return winners.size() == 1;
  }

  public void displayWinner() {
    if(!tie()) {
      batch.begin();
      winner.draw(batch);
      batch.end();
    }

  }

  public void font(){

    fontBatch.begin();
    String text;
    fontGenerator = new FontGenerator();
    font = fontGenerator.generate(50, Color.WHITE);

    int x = 100;
    int y = 100;

    if(tie()){
      text = " It's a tie!";
    } else{

    text = winners.get(0).getUsername();
      text = "The winner is " + text + "!";
    }

    font.draw(fontBatch, text, x, y);
    fontBatch.end();

  }
}


