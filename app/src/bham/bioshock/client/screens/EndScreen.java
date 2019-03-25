package bham.bioshock.client.screens;

import bham.bioshock.Config;
import bham.bioshock.client.Assets;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;
import bham.bioshock.client.gameLogic.gameboard.DrawPlayer;
import bham.bioshock.client.scenes.gameboard.ScoreBoard;
import bham.bioshock.common.Position;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EndScreen extends ScreenMaster {
  private Store store;
  private ArrayList<Player> winners;
  private OrthographicCamera camera;
  private ShapeRenderer renderer;
  private SpriteBatch fontBatch;
  private DrawPlayer drawPlayer;
  private FontGenerator fontGenerator;
  private BitmapFont font;
  private Image[] playerSprites;
  private Position[] locations;
  private ArrayList<Player> players;
  private SpriteBatch playerBatch;
  private VerticalGroup hg;
  private Table table;
  private Label userLabel;
  private int number =0;

  public EndScreen(Router router, Store store) {
    super(router);
    this.store = store;
    this.winners = store.getWinner();

    this.camera = new OrthographicCamera();
    this.batch = new SpriteBatch();
    this.fontBatch = new SpriteBatch();
    this.playerBatch = new SpriteBatch();
    this.renderer = new ShapeRenderer();
    this.locations = new Position[4];
    this.fontGenerator = new FontGenerator();
    this.number = store.getPlayers().size();
   // this.playerSprites = new Sprite[4];
    table = new Table();
    table.setX(screenWidth*2);
    table.setY(screenHeight*2);



    //hg.setPosition(screenWidth /4, screenWidth/4);


    viewport = new FitViewport(Config.GAME_WORLD_WIDTH, Config.GAME_WORLD_HEIGHT, camera);
    stage = new Stage(viewport, batch);

    setPositions();
    load();
  }


  @Override
  public void show() {
    super.show();
  }

  @Override
  public void render(float delta) {
    super.render(delta);
    //display();
   // displayWinner();
    //font();


    stage.addActor(table);
    stage.act(Gdx.graphics.getDeltaTime());
    stage.draw();

  }

  public void setPositions(){
   float x = screenWidth/5;
   float y =screenHeight/5;
    for (int i=0;i<4; i ++){
      Position pos = new Position(x,y);
      locations[i] = pos;
      x = x*2; y= y*2;

    }
  }

  private boolean tie(){
    return winners.size() == 1;
  }

  private void font(){

    fontBatch.begin();
    String text;

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

  private  void load(){
    players = store.getPlayers();
    players.sort(Comparator.comparingInt(Player::getPoints).reversed());

    for(int i= 0 ;i<players.size(); i++) {
      int textureId = players.get(i).getTextureID() + 1;

      TextureRegion texture = new TextureRegion(new Texture(Gdx.files.internal("app/assets/entities/players/" + textureId + ".png")));

      hg = new VerticalGroup();
      hg.setFillParent(true);

      Image img = new Image(texture);
      img.setWidth(50);
      img.setScaling(Scaling.fillX);
      img.setHeight(50);
      img.setScaling(Scaling.fillX);
      img.setSize(50,50);
      img.setScaling(Scaling.fillX);

      //playerSprites[i] = img;
      hg.addActor((img));
      Label.LabelStyle style = new Label.LabelStyle();
      style.font = fontGenerator.generate(40);

      userLabel = new Label(players.get(i).getUsername(), style);
      hg.addActor(userLabel);
      int  j = number;
      while(j >0 ) {
        userLabel = new Label("", style);
        hg.addActor(userLabel);
        j --;
      }
      number--;
      table.add(hg);
    }


  }
}


