package bham.bioshock.client.screens;

import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;


public class StatsContainer extends Container {

    private MinigameStore store;
    private Skin skin;


    private ArrayList<PlayerContainer> playerTables = new ArrayList<>();



    public StatsContainer(MinigameStore store) {

        this.store = store;

        /*nb: change the skin*/
        this.skin = new Skin(Gdx.files.internal("app/assets/skins/neon/skin/neon-ui.json"));
        setUp();
        setupContainers();
    }

    private void setUp() {
        this.setSize(Gdx.graphics.getWidth()*0.2f, Gdx.graphics.getHeight()*0.2f);
        this.setPosition(10,10);
    }

    private void setupContainers() {

        Table table = new Table();
        Label l = new Label("STATS", skin);
        table.add(l);
        table.row();
        ArrayList<Player> players = store.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            playerTables.add(new PlayerContainer(players.get(i).getId(), players.get(i).getUsername()));
            update(playerTables.get(i));
            table.add(playerTables.get(i));
            if(i%2 == 0){
                table.row();
            }
        }

        this.setActor(table);
    }

    public void updateAll() {
        for(int i = 0; i < playerTables.size(); i++) {
            update(playerTables.get(i));
        }
    }

    private void update(PlayerContainer player_container) {
        Collection<bham.bioshock.minigame.models.Player> map = store.getPlayers();
        UUID id = player_container.getId();
        float fuel = map.get(id).getFuel();
        int planets = map.get(id).getPlanetsCaptured();
        int points = map.get(id).getPoints();
        player_container.setFuel(fuel);
        player_container.setPlanets(planets);
        player_container.setPoints(points);
    }

    private class PlayerContainer extends Container {

        private Label fuel_label;
        private Label planets_label;
        private Label points_label;
        private Label player_name;
        private UUID id;

        PlayerContainer(UUID id, String name) {
            this.id = id;
            Table table = new Table();
            Label l1 = new Label("Fuel: ", skin);
            Label l2 = new Label("Planets: ", skin);
            Label l3 = new Label("Points: ", skin);

            player_name = new Label(name, skin);
            fuel_label = new Label("", skin);
            planets_label = new Label("", skin);
            points_label = new Label("", skin);

            table.add(player_name);
            table.row();
            table.add(l1);
            table.add(fuel_label);
            table.row();
            table.add(l2);
            table.add(planets_label);
            table.row();
            table.add(l3);
            table.add(points_label);

            this.setActor(table);
        }



        public void setPoints(int points) {
            points_label.setText(points);
        }
        public void setFuel(float fuel) {
            fuel_label.setText(Float.toString(fuel));
        }
        public void setPlanets(int planets) {
            planets_label.setText(planets);
        }

        public UUID getId() {
            return id;
        }
    }
}
