package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import bham.bioshock.client.XMLInteraction;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HowToScreen extends ScreenMaster {

  private Table textTable;
  private XMLInteraction xmlInteraction = new XMLInteraction();

  public HowToScreen(Router router) {
    super(router);

    stage = new Stage(new ScreenViewport());
    batch = new SpriteBatch();

  }

  private void assemble() {
//    // create container
//    Container<Table> tableContainer = new Container<>();
//            float container_width = screen_width*0.1f;
//            float container_height = screen_height*0.9f;
//            tableContainer.setSize(container_width, container_height);
//            tableContainer.setPosition((screen_width - container_width)/2.0f, (screen_height-container_height)/2.0f);
//
//    // create table
//    textTable = new Table(skin);
//    textTable.setFillParent(true);
//
//    // contents
//    Label l1 = new Label("How to PLay", skin);
//    Label l2 = new Label("Controls", skin);
//
//    // game desciption text is read from the XML file
//    String game_desc = reader.getTag("game_desc");
//    Label game_desc_label = new Label(game_desc, skin);
//    game_desc_label.setWrap(true);
//
//
//    String controls = reader.getTag("game_controls");
//    Label controls_desc_label = new Label(controls, skin);
//    controls_desc_label.setWrap(true);
//
//    ScrollPane scrollPane1 = new ScrollPane(game_desc_label, skin);
//    ScrollPane scrollPane2 = new ScrollPane(controls_desc_label, skin);
//
//    textTable.add(l1).padBottom(20);
//    textTable.row();
//    textTable.add(scrollPane1).width(screen_width*0.6f);
//    textTable.row();
//    textTable.add(l2).padBottom(20);
//    textTable.row();
//    textTable.add(scrollPane2).width(screen_width*0.6f);
//
//    stage.addActor(textTable);
  }

  @Override
  public void show() {
    super.show();
    assemble();
    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void render(float delta) {
    super.render(delta);
  }

  @Override
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}
}
