package bham.bioshock.client.screens;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;

import bham.bioshock.client.Client;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import bham.bioshock.client.controllers.JoinScreenController;

public class JoinScreen extends ScreenMaster {
  JoinScreenController controller;

  private final int CELL_PADDING = 100;
  private final int IMAGE_WIDTH = 20;

  public enum WaitText {
    WAITING, CONNECTED
  }

  private HashMap<WaitText, String> wait_text;

  //players
  private PlayerContainer c1;
  private PlayerContainer c2;
  private PlayerContainer c3;
  private PlayerContainer c4;

  private Table table;

  //Arrays to hold the labels
  private ArrayList<Label> player_names;
  private ArrayList<Label> waiting_labels;

  public JoinScreen(JoinScreenController controller) {
    this.controller = controller;

    stage = new Stage(new ScreenViewport());
    batch = new SpriteBatch();

    wait_text = new HashMap<>();
    wait_text.put(WaitText.WAITING, "Waiting...");
    wait_text.put(WaitText.CONNECTED, "Connected");

    player_names = new ArrayList<>();
    waiting_labels = new ArrayList<>();

  }

  @Override
  public void show() {
    super.show();

    addBackButton();

    buildJoinScreen();
  }

  private void buildJoinScreen() {
    table = new Table();
    table.setFillParent(true);
    //table.setHeight(screen_height * 0.7f);
    //table.setWidth(screen_width * 0.7f);
    table.pad(CELL_PADDING);
    stage.addActor(table);


    addPlayers();

  }
  private void addPlayers() {
    c1 = new PlayerContainer(new Texture(Gdx.files.internal("app/assets/entities/rockets/1.png")), "Player1", WaitText.WAITING);
    c2 = new PlayerContainer(new Texture(Gdx.files.internal("app/assets/entities/rockets/2.png")), "Player2", WaitText.WAITING);
    c3 = new PlayerContainer(new Texture(Gdx.files.internal("app/assets/entities/rockets/3.png")), "Player3", WaitText.WAITING);
    c4 = new PlayerContainer(new Texture(Gdx.files.internal("app/assets/entities/rockets/4.png")), "Player4", WaitText.WAITING);

    table.add(c1);
    table.add(c2);
    table.row();
    table.add(c3);
    table.add(c4);
  }

  private class PlayerContainer extends Container<Table> {

    String waitText;
    Label waiting_label;
    Label name_label;

    PlayerContainer(Texture img, String name, WaitText text) {
      this.waitText = wait_text.get(text);
      Image image = new Image(img);
      image.setScaling(Scaling.fit);

      Table t = new Table();
      t.setFillParent(true);
      t.pad(15);

      name_label = new Label(name, skin);
      player_names.add(name_label);

      waiting_label = new Label(waitText, skin);
      waiting_labels.add(waiting_label);

      t.add(name_label).pad(0,0,10,0);
      t.row();
      t.add(image).expand();
      t.row();
      t.add(waiting_label).pad(10,0,0,0);
      this.setActor(t);
    }


  }

  public void changeWaitLabel(Label label, WaitText text) {
    label.setText(wait_text.get(text));

  }

  public void changePlayerName(Label label, String player) {
    label.setText(player);
  }

  public ArrayList<Label> getPlayer_names() {
    return player_names;
  }

  public ArrayList<Label> getWaiting_labels() {
    return waiting_labels;
  }


  @Override
  public void render(float delta) {
    super.render(delta);

  }





}
