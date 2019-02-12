package bham.bioshock.client.screens;

import bham.bioshock.client.XMLReader;
import bham.bioshock.client.controllers.HowToController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HowToScreen extends ScreenMaster {

  private Table textTable;
  private XMLReader reader;

  public HowToScreen(HowToController controller) {
    this.controller = controller;

    stage = new Stage(new ScreenViewport());
    batch = new SpriteBatch();

    readXML();
  }

  private void readXML() {
    reader = new XMLReader("app/assets/XML/game_desc.xml");
  }

  private void assemble() {
    // create container
    /*Container<Table> tableContainer = new Container<>();
            float container_width = screen_width*0.1f;
            float container_height = screen_height*0.9f;
            tableContainer.setSize(container_width, container_height);
            tableContainer.setPosition((screen_width - container_width)/2.0f, (screen_height-container_height)/2.0f);
    */
    // create table
    textTable = new Table(skin);
    textTable.setFillParent(true);

    // contents
    Label l1 = new Label("How to PLay", skin);
    // game desciption text is read from the XML file
    String game_desc = reader.getTag("game_desc");
    Label desc = new Label(game_desc, skin);
    desc.setWrap(true);

    Label l2 = new Label("Controls", skin);
    String controls = reader.getTag("game_controls");
    Label contr = new Label(controls, skin);
    contr.setWrap(true);

    textTable.row().colspan(2);
    textTable.add(l1);
    textTable.row().colspan(2);
    textTable.add(game_desc);
    textTable.add(desc);
    textTable.row().colspan(2);
    textTable.add(l2);
    textTable.add(contr);

    // tableContainer.setActor(textTable);

    stage.addActor(textTable);
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
