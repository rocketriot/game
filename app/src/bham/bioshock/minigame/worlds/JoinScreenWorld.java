package bham.bioshock.minigame.worlds;

import bham.bioshock.common.Position;
import bham.bioshock.minigame.models.Gun;
import bham.bioshock.minigame.models.Platform;
import bham.bioshock.minigame.models.Rocket;

import java.util.ArrayList;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;



public class JoinScreenWorld extends World {
    private double radius;
    private double gravity;
    Position gravityCenter = new Position(0, 0);

    public JoinScreenWorld() {
        radius = 5000;
        gravity = 2000;
    }
    @Override
    public double getPlanetRadius() {
        return radius;
    }

    @Override
    public double getGravity() {
        return gravity;
    }

    @Override
    public Position[] getPlayerPositions() {
        return new Position[0];
    }

    @Override
    public Position gravityCenter() {
        return gravityCenter;
    }

    @Override
    public ArrayList<Rocket> getRockets() {
        return null;
    }

    @Override
    public ArrayList<Gun> getGuns() {
        return null;
    }

    @Override
    public ArrayList<Platform> getPlatforms() {
        return null;
    }
    @Override
    public Texture getTexture() {
      return null;
    }
    @Override
    public void afterDraw(SpriteBatch batch) {
    }
}
