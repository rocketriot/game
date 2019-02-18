package bham.bioshock.client.screens;

import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


import java.util.*;


public class StatsContainer extends Container {

    private MinigameStore minigame_store;

    private Skin skin;


    private ArrayList<PlayerContainer> playerTables = new ArrayList<>();



    public StatsContainer(MinigameStore minigame_store) {

        this.minigame_store = minigame_store;

        /*nb: change the skin*/
        this.skin = new Skin(Gdx.files.internal("app/assets/skins/neon/skin/neon-ui.json"));
        setUp();
        setupContainers();
    }

    private void setUp() {
        this.fill();
        this.setSize(Gdx.graphics.getWidth()*0.2f, Gdx.graphics.getHeight()*0.2f);
        this.setPosition(10,10);
    }

    private void setupContainers() {

        Table table = new Table();
        Label l = new Label("STATS", skin);
        table.add(l);
        table.row();
        HashMap<bham.bioshock.minigame.models.Player, Player> minigames_players_map = minigame_store.getPlayerMap();
        Iterator<Player> iterator = minigames_players_map.values().iterator();
        int i = 0;
        while(iterator.hasNext()) {
            Player p = iterator.next();
            playerTables.add(new PlayerContainer(p.getId(), p.getUsername()));
            update(playerTables.get(i));
            table.add(playerTables.get(i));
            if(i%2 == 0){
                table.row();
            }
            i++;

        }

        this.setActor(table);
    }

    public void updateAll() {
        for(int i = 0; i < playerTables.size(); i++) {
            update(playerTables.get(i));
        }
    }

    private void update(PlayerContainer player_container) {
        HashMap<bham.bioshock.minigame.models.Player, Player> minigames_players_map = minigame_store.getPlayerMap();

        UUID id = player_container.getId();
        bham.bioshock.minigame.models.Player minigame_player = minigame_store.getPlayer(id);
        Player player = minigames_players_map.get(minigame_player);

        float fuel = player.getFuel();
        int planets = player.getPlanetsCaptured();
        int points = player.getPoints();
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
