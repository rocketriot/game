package bham.bioshock.client.screens;

import bham.bioshock.client.Router;
import bham.bioshock.client.XMLInteraction;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.common.models.Upgrade;
import bham.bioshock.common.models.Upgrade.Type;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The How To Screen.
 */
public class HowToScreen extends ScreenMaster {

  private Table textTable;
  private Container<Table> tableContainer;

  private XMLInteraction xmlInteraction = new XMLInteraction();
  private HashMap<String, String> descriptionFromFile;

  /*
  Textures and Images
   */
  private Image title;
  private Image cursor;
  private Image planet;
  private Image fuel;
  private Image upgrade;

  private Label description;
  private Label howToMoveA;
  private Label howToMoveB;
  private Label howToMoveC;
  private Label howToCaptureA;
  private Label howToCaptureB;
  private Label fuelDescription;
  private Label upgradeDescription;

  private Container planetContainer;
  private ArrayList<Label> upgradeDescs;


  /**
   * Instantiates a new How to screen.
   *
   * @param router the router
   */
  public HowToScreen(Router router, AssetContainer assets) {
    super(router, assets);
    descriptionFromFile = xmlInteraction.xmlToDescription();
    loadImages();

  }

  private void loadImages(){
    title = new Image(new Texture(Assets.howToPlayButton));
    title.setWidth(150);
    title.setScaling(Scaling.fillX);
    cursor = new Image(new Texture(Assets.cursor));
    cursor.setWidth(50);
    cursor.setScaling(Scaling.fillX);
    planet = new Image(new Texture(Assets.planetsFolder+"/1.png"));
    planet.setWidth(100);
    planet.setScaling(Scaling.fillX);
    (planetContainer = new Container()).setActor(planet);
    planetContainer.setSize(50, 50);
    fuel = new Image(new Texture(Assets.fuel));
    fuel.setWidth(100);
    fuel.setScaling(Scaling.fillX);
    upgrade = new Image(new Texture(Assets.upgrade));
    upgrade.setWidth(100);
    upgrade.setScaling(Scaling.fillX);

    // game description text is read from the XML file
    description = new Label(descriptionFromFile.get("gameDescription"),skin);
    //description.setWrap(true);
    howToMoveA = new Label(descriptionFromFile.get("howToMove_1"),skin);
    howToMoveB = new Label(descriptionFromFile.get("howToMove_2"),skin);
    howToMoveC = new Label(descriptionFromFile.get("howToMove_3"),skin);
    howToMoveA.setAlignment(Align.left);
    howToMoveB.setAlignment(Align.left);
    howToMoveC.setAlignment(Align.left);
    howToCaptureA = new Label(descriptionFromFile.get("howToCapture_1"),skin);
    howToCaptureB = new Label(descriptionFromFile.get("howToCapture_2"),skin);
    howToCaptureA.setAlignment(Align.left);
    howToCaptureB.setAlignment(Align.left);
    fuelDescription = new Label(descriptionFromFile.get("fuel"),skin);
    upgradeDescription = new Label(descriptionFromFile.get("upgrade"),skin);
    upgradeDescs = new ArrayList<>();
    for (Type t : Type.values()) {
      upgradeDescs.add(new Label("- " + Upgrade.getTypeDisplayName(t) + ": " + Upgrade.getTypeDesc(t), skin));
    }
    fuelDescription.setWrap(true);

    tableContainer = new Container<>();
    float containerWidth = screenWidth * 0.8f;
    float containerHeight = screenHeight * 0.7f;
    tableContainer.setSize(containerWidth, containerHeight);
    tableContainer.setPosition((screenWidth - containerWidth) / 2.0f,
            (screenHeight - containerHeight) / 2.0f);

    textTable = new Table(skin);
    textTable.setFillParent(true);
    textTable.top();
    textTable.setHeight(containerHeight);

  }

  private void assemble() {

    float rowHeight = textTable.getHeight() / 7f;

    textTable.padLeft(50).padRight(50);
    textTable.padBottom(20);

    textTable.add(title).colspan(2).expandX().padBottom(50);
    textTable.row();
    textTable.add(description).colspan(2).expandX().padBottom(10);
    textTable.row().height(rowHeight);
    textTable.columnDefaults(1).padRight(50).padBottom(10);
    textTable.columnDefaults(0).expand().left();
    VerticalGroup group = new VerticalGroup();
    group.addActor(howToMoveA);
    group.addActor(howToMoveB);
    group.addActor(howToMoveC);
    group.columnAlign(Align.left);
    textTable.add(group);
    textTable.add(cursor).width(50).height(50);
    textTable.row().height(rowHeight);
    VerticalGroup group2 = new VerticalGroup();
    group2.addActor(howToCaptureA);
    group2.addActor(howToCaptureB);
    group2.columnAlign(Align.left);
    textTable.add(group2);
    textTable.add(planetContainer).width(100).height(100);
    textTable.row();
    textTable.add(fuelDescription);
    textTable.add(fuel).width(100).height(50);
    textTable.row();
    Table group3 = new Table();
    group3.row().align(Align.left);
    group3.add(upgradeDescription);
    for (Label l : upgradeDescs) {
      group3.row().align(Align.left).padLeft(30);
      group3.add(l);
    }
    textTable.add(group3).align(Align.left);
    textTable.add(upgrade).width(100).height(50);

    stage.addActor(textTable);
  }

  @Override
  public void show() {
    super.show();
    
    drawBackButton();
    assemble();
    
    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void render(float delta) {
    super.render(delta);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }


}
