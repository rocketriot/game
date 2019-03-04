package bham.bioshock.client.scenes.gameboard.hud;

import bham.bioshock.client.Assets;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class ScoreBoard extends HudElement {
  private Table scoreBoard;
  private Label roundLabel;
  private Image turnPointer;

  public ScoreBoard(Stage stage, SpriteBatch batch, Skin skin, Store store, Router router) {
    super(stage, batch, skin, store, router);
  }

  protected void setup() {
    VerticalGroup stats = new VerticalGroup();
    stats.setFillParent(true);
    stats.top();
    stats.left();
    stats.pad(16);
    stage.addActor(stats);

    roundLabel = new Label("Round 1", skin);
    stats.addActor(roundLabel);

    scoreBoard = new Table();
    scoreBoard.padTop(16);
    stats.addActor(scoreBoard);

    turnPointer = new Image(new Texture(Assets.turnPointer));
  }

  protected void render(int round, ArrayList<Player> players, Player movingPlayer) {
    batch.begin();

    roundLabel.setText("Round " + round);

    scoreBoard.clearChildren();

    // Add players to scoreboard
    for (Player player : players) {
      boolean isPlayersTurn = player.getId().equals(movingPlayer.getId());

      // Add the turn pointer to the player whos turn it is
      scoreBoard.add(isPlayersTurn ? turnPointer : null).width(30).height(30).padTop(8).padRight(4);

      // Add name of the user
      Label usernameLabel = new Label(player.getUsername(), skin);
      scoreBoard.add(usernameLabel).padTop(8).fillX().align(Align.left);

      // Specify if the player is a CPU
      Label cpuLabel = new Label("CPU", skin);
      scoreBoard.add(player.isCpu() ? cpuLabel : null).padTop(8);

      // Add the player's points
      Label pointsLabel = new Label(player.getPoints() + "", skin);
      scoreBoard.add(pointsLabel).padTop(8).padLeft(16).fillX().align(Align.left);

      scoreBoard.row();
    }

    batch.end();
  }
}
