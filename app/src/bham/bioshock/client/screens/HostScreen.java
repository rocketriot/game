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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class HostScreen extends ScreenMaster {

    private TextButton host_button;
    private Table table;

    private String host_name;

    public HostScreen(HostScreenController controller) {

        this.controller = controller;
        stage = new Stage(new ScreenViewport());

        batch = new SpriteBatch();

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

    private void assemble() {
        table = drawTable();

        stage.addActor(table);
    }


    private Table drawTable(){
        Table table = new Table();
        table.setFillParent(true);

        Label l1 = new Label("Host Name", skin);
        Label l2 = new Label("Number of PLayers", skin);
        TextField hostNameField = new TextField("", skin);
        //hostNameField.setMessageText("Enter Host Name");
        SelectBox selectPlayers = new SelectBox(skin);

        //get max players from reader
        HostScreenController contr = (HostScreenController) controller;
        int max_players = contr.getMaxPlayers();
        Array<Integer> selection = new Array<>();
        for(int i = 1; i <= max_players; i++) {
            selection.add(i);
        }
        selectPlayers.setItems(selection);
        //get Preferred number of players
        int preferred_players = ((HostScreenController) controller).getPreferredPlayers();
        selectPlayers.setSelected(preferred_players);

        hostNameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                host_name = hostNameField.getText();
            }
        });






        //button for start new game
        host_button = new TextButton("Start New Game", skin);
        //host_button.setPosition(stage.getWidth()/2,stage.getHeight()/2);

        host_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                host_name = hostNameField.getText();
                System.out.println("host name = " + host_name);
                int players = selectPlayers.getSelectedIndex() + 1;
                System.out.println("number of players " + players);
                configureNewGame(host_name, players);

            }
        });

        table.add(l1);
        table.add(hostNameField);
        table.row();
        table.add(l2);
        table.add(selectPlayers);
        table.row();
        table.add(host_button);

        return table;
    }


    private void configureNewGame(String host, int players) {
        HostScreenController contr = (HostScreenController) controller;
        contr.configureGame(host, players);
    }

}