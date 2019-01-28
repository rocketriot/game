package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.HostScreenController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HostScreen extends ScreenMaster {

    private TextButton host_button;
    private Table table;

    public HostScreen(HostScreenController controller) {

        this.controller = controller;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        stack = new Stack();


    }

    @Override
    public void render(float delta) {
        drawBackground(delta);
        assemble();
    }

    private void assemble() {
        table = drawTable();

        stage.clear();
        stage.addActor(stack);
        stack.setSize(stage.getWidth(), stage.getHeight());
        stack.add(table);
    }


    private Table drawTable(){
        Table table = new Table();
        stage.addActor(table);

        Label l1 = new Label("Host Name", skin);
        Label l2 = new Label("Number of PLayers", skin);
        TextField tField1 = new TextField("", skin);
        TextField tField2 = new TextField("", skin);
        tField2.setTextFieldFilter(new TextField.TextFieldFilter() {
            private char[] accepted = {'1','2','3','4'};
            @Override
            public boolean acceptChar(TextField textField, char c) {
                for (char a : accepted)
                    if (a == c) return true;
                return false;
            }
        });

        //button for start new game
        host_button = new TextButton("Start New Game", skin);
        //host_button.setPosition(stage.getWidth()/2,stage.getHeight()/2);

        host_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //bring up a popup to ask for the names
                HostPopup popup = new HostPopup();
                //popup.hostPopup();
            }
        });
        table.add(l1);
        table.add(tField1);
        table.row();
        table.add(l2);
        table.add(tField2);
        table.row();
        table.add(host_button);

        return table;
    }


    private void configureNewGame() {
        //get the name of the host
        String host_name = "hoster";
        int number_of_player = 2;
        //ask how many players
        HostScreenController contr = (HostScreenController) controller;
        contr.configureGame(host_name, number_of_player);
    }

    private class HostPopup {
     /*   private TextField text = new TextField("Enter Name", skin);
        private InputListener host_name = text.getDefaultInputListener();

        public void hostPopup() {
            Gdx.input.getTextInput(host_name, "Enter Name", "","");
        }*/
    }
}