package bham.bioshock.client.screens;

import bham.bioshock.client.Assets;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.MinigameStore;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;


import java.util.*;


public class StatsContainer extends Container {

    private Store store;

    private Skin skin;


    private ArrayList<PlayerContainer> playerTables = new ArrayList<>();



    public StatsContainer(Store store) {

        this.store = store;

        /*nb: change the skin*/
        this.skin = new Skin(Gdx.files.internal(Assets.skin));
        setupContainers();
    }


    private void setupContainers() {

        Table table = new Table();
        Iterator<Player> iterator = store.getPlayers().iterator();

        int i = 0;
        while(iterator.hasNext()) {
            Player p = iterator.next();
            if(p.getId() != store.getMainPlayer().getId()) {
                playerTables.add(new PlayerContainer(p.getId(), p.getUsername()));
                update(playerTables.get(i));
                table.add(playerTables.get(i)).padRight(30);
                i++;
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

        UUID id = player_container.getId();
        Player player = store.getPlayer(id);

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
        private int ICON_SIZE = 1;

        PlayerContainer(UUID id, String name) {
            this.id = id;

            Image fuelIcon = new Image(new Texture(Gdx.files.internal("app/assets/ui/icons/fuel.png")));
            Image planetIcon = new Image(new Texture(Gdx.files.internal("app/assets/ui/icons/planet.png")));
            Image pointsIcon = new Image(new Texture(Gdx.files.internal("app/assets/ui/icons/star.png")));

            fuelIcon.setSize(1,1);
            planetIcon.setSize(ICON_SIZE, ICON_SIZE);
            pointsIcon.setSize(ICON_SIZE, ICON_SIZE);

            Table table = new Table();
            Label l1 = new Label("Fuel: ", skin);
            Label l2 = new Label("Planets: ", skin);
            Label l3 = new Label("Points: ", skin);

            player_name = new Label(name, skin);
            fuel_label = new Label("", skin);
            planets_label = new Label("", skin);
            points_label = new Label("", skin);

            table.add(player_name).colspan(2);
            table.row();
            table.add(fuelIcon);
            table.add(fuel_label);
            table.row();
            table.add(planetIcon);
            table.add(planets_label);
            table.row();
            table.add(pointsIcon);
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
