package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import bham.bioshock.client.XMLReader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import bham.bioshock.client.controllers.HowToController;
import org.w3c.dom.Document;

public class HowToScreen extends ScreenMaster {

    private Table textTable;
    private Stack stack;
    private XMLReader reader;

    public HowToScreen(HowToController controller) {
        this.controller = controller;

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        readXML();

        stack = new Stack();
        textTable = drawText();



    }

    private void readXML() {
        reader = new XMLReader("app/assets/XML/game_desc.xml");
        //reader.printNodes("game_desc");
    }
   private Table drawText() {
        textTable = new Table();
        Label l1 = new Label("How to PLay", skin);
        //this text can be read from an XML File
        String game_desc = reader.getTag("game_desc");
        Label l2 = new Label(game_desc, skin);
        textTable.add(l1);
        textTable.row();
        textTable.add(l2);

        return textTable;
   }

    private void assemble() {
        stage.clear();
        stage.addActor(stack);
        stack.setSize(stage.getWidth(), stage.getHeight());
        stack.add(textTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        drawBackground(delta);
        assemble();
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
