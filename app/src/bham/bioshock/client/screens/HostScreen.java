package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.HostScreenController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import javafx.util.Pair;

public class HostScreen extends ScreenMaster {

    private TextButton host_button;

    public HostScreen(HostScreenController controller) {

        this.controller = controller;
        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();


    }

    @Override
    public void render(float delta) {
        drawBackground(delta);
        drawButtons();
    }


    private void drawButtons(){
        //button for start new game
        host_button = new TextButton("Start New Game", skin);
        stage.addActor(host_button);
        host_button.setPosition(stage.getWidth()/2,stage.getHeight()/2);

        host_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //bring up a popup to ask for the names
                HostPopup popup = new HostPopup();
                popup.hostPopup();
            }
        });
    }

    /*private Pair<String, Integer> drawPopup() {

    }*/

    private void configureNewGame() {
        //get the name of the host
        String host_name = "hoster";
        int number_of_player = 2;
        //ask how many players
        HostScreenController contr = (HostScreenController) controller;
        contr.configureGame(host_name, number_of_player);
    }

    private class HostPopup {
        private TextField text = new TextField("Enter Name", skin);
        private InputListener host_name = text.getDefaultInputListener();

        public void hostPopup() {
            Gdx.input.getTextInput(host_name, "Enter Name", "","");
        }
    }
}