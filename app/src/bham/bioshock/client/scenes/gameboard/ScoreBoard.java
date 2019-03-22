package bham.bioshock.client.scenes.gameboard;

import bham.bioshock.client.Assets;
import bham.bioshock.client.FontGenerator;
import bham.bioshock.client.Router;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class ScoreBoard extends HudElement {
  private Table scoreBoard;
  private Label roundLabel;
  private Image turnPointer;

  private LabelStyle scoreBoardStyle;
  private LabelStyle scoreBoardCpuStyle;
  private FontGenerator fontGenerator;

  public ScoreBoard(Stage stage, SpriteBatch batch, Skin skin, Store store, Router router) {
    super(stage, batch, skin, store, router);
  }

  protected void setup() {
    fontGenerator = new FontGenerator();

    VerticalGroup stats = new VerticalGroup();
    stats.setFillParent(true);
    stats.top();
    stats.left();
    stats.pad(20);
    stage.addActor(stats);

    LabelStyle style = new LabelStyle();
    style.font = fontGenerator.generate(40);

    roundLabel = new Label("Round 1", style);
    stats.addActor(roundLabel);

    scoreBoard = new Table();
    scoreBoard.padTop(16);
    stats.addActor(scoreBoard);

    turnPointer = new Image(new Texture(Assets.turnPointer));

    scoreBoardStyle = new LabelStyle();
    scoreBoardStyle.font = fontGenerator.generate(25);

    scoreBoardCpuStyle = new LabelStyle();
    scoreBoardCpuStyle.font = fontGenerator.generate(16);
  }

  protected void render(int round, ArrayList<Player> players, Player movingPlayer) {
    batch.begin();

    roundLabel.setText("Round " + round);

    scoreBoard.clearChildren();

    // Add players to scoreboard
    for (Player player : players) {
      boolean isPlayersTurn = player.getId().equals(movingPlayer.getId());

      // Add the turn pointer to the player whos turn it is
      scoreBoard.add(isPlayersTurn ? turnPointer : null).width(40).height(40).padTop(8).padRight(8);

      // Add name of the user
      Label usernameLabel = new Label(player.getUsername(), scoreBoardStyle);
      scoreBoard.add(usernameLabel).padTop(8).fillX().align(Align.left);

      // Specify if the player is a CPU
      Label cpuLabel = new Label("CPU", scoreBoardCpuStyle);
      scoreBoard.add(player.isCpu() ? cpuLabel : null).padLeft(8).bottom();

      // Add the player's points
      Label pointsLabel = new Label(player.getPoints() + "", scoreBoardStyle);
      scoreBoard.add(pointsLabel).padTop(8).padLeft(64).fillX().align(Align.left);

      scoreBoard.row();
    }

    batch.end();
  }
}
