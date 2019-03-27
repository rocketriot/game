package bham.bioshock.client.scenes.minigame;

import bham.bioshock.client.Router;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.assets.Assets;
import bham.bioshock.client.scenes.HudElement;
import bham.bioshock.common.models.Player;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.minigame.objectives.KillThemAll;
import bham.bioshock.minigame.objectives.Objective;
import bham.bioshock.minigame.objectives.Objective.MinigameType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import java.util.ArrayList;

public class MinigameScoreBoard extends HudElement {
  private Table scoreBoard;
  private Label roundLabel;
  private Image turnPointer;

  private LabelStyle scoreBoardStyle;
  private LabelStyle scoreBoardCpuStyle;
  private ArrayList<LabelStyle> scoreBoardPointsStyle;

  public MinigameScoreBoard(Stage stage, SpriteBatch batch, Store store, Router router, AssetContainer assets) {
    super(stage, batch, assets, store, router);
  }

  protected void setup() {
    VerticalGroup stats = new VerticalGroup();
    stats.setFillParent(true);
    stats.top();
    stats.left();
    stats.pad(20);
    stage.addActor(stats);

    LabelStyle style = new LabelStyle();
    style.font = assets.getFont(40);

    roundLabel = new Label("Round 1", style);
    stats.addActor(roundLabel);

    scoreBoard = new Table();
    scoreBoard.padTop(16);
    stats.addActor(scoreBoard);

    turnPointer = new Image(new Texture(Assets.turnPointer));

    scoreBoardStyle = new LabelStyle();
    scoreBoardStyle.font = assets.getFont(25);

    scoreBoardCpuStyle = new LabelStyle();
    scoreBoardCpuStyle.font = assets.getFont(16);

    scoreBoardPointsStyle = new ArrayList<>();
    scoreBoardPointsStyle.add(new LabelStyle(assets.getFont(25, new Color(0xFFD048FF)), new Color(0xFFD048FF)));
    scoreBoardPointsStyle.add(new LabelStyle(assets.getFont(25, new Color(0xC2C2C2FF)), new Color(0xC2C2C2FF)));
    scoreBoardPointsStyle.add(new LabelStyle(assets.getFont(25, new Color(0xD27114FF)), new Color(0xD27114FF)));
    scoreBoardPointsStyle.add(new LabelStyle(assets.getFont(25, new Color(0xCE2424FF)), new Color(0xCE2424FF)));
  }

  public void render() {
    batch.begin();

    roundLabel.setText(store.getMinigameStore().getObjective().name());

    scoreBoard.clearChildren();
    ArrayList<Player> players = store.getPlayers();

    String nameStr;

    Objective minigameObjective = store.getMinigameStore().getObjective();
    MinigameType minigameType = minigameObjective.getMinigameType();

    if (minigameType.equals(MinigameType.KILL_THEM_ALL)) {
      // Add players to scoreboard
      for (Player player : players) {
        boolean isWinning = minigameObjective.getWinner() == player.getId();

        // Add the turn pointer to the player whos turn it is
        scoreBoard.add(isWinning ? turnPointer : null).width(40).height(40).padTop(8)
            .padRight(8);

        // Add name of the user
        Label usernameLabel = new Label(player.getUsername(), scoreBoardStyle);
        scoreBoard.add(usernameLabel).padTop(8).fillX().align(Align.left);

        // Specify if the player is a CPU
        Label cpuLabel = new Label("CPU", scoreBoardCpuStyle);
        scoreBoard.add(player.isCpu() ? cpuLabel : null).padLeft(8).bottom();

        LabelStyle pointsStyle = generatePointsStyle(player);

        // Add the player's points
        Label pointsLabel = new Label(((KillThemAll) minigameObjective).getPlayerScore(player.getId()) + "", pointsStyle);
        scoreBoard.add(pointsLabel).padTop(8).padLeft(64).fillX().align(Align.left);

        scoreBoard.row();
      }
    } else if (minigameType.equals(MinigameType.CAPTURE_THE_FLAG)) {
      Player player = store.getPlayer(minigameObjective.getWinner());
      if (player == null) {
        nameStr = "No one is carrying the flag";
      } else {
        nameStr = player.getUsername();
      }

      // Add name of the user
      Label usernameLabel = new Label(nameStr, scoreBoardStyle);
      scoreBoard.add(usernameLabel).padTop(8).fillX().align(Align.left);

      if (player != null) {
        // Specify if the player is a CPU
        Label cpuLabel = new Label("CPU", scoreBoardCpuStyle);
        scoreBoard.add(player.isCpu() ? cpuLabel : null).padLeft(8).bottom();

      LabelStyle pointsStyle = generatePointsStyle(player);

      // Add the player's points
      Label pointsLabel = new Label("Carrying Flag" + "", pointsStyle);
      scoreBoard.add(pointsLabel).padTop(8).padLeft(64).fillX().align(Align.left);
      }


      scoreBoard.row();
    } else {
      scoreBoard.row();
    }

    batch.end();
  }

  private LabelStyle generatePointsStyle(Player player) {
    // The players position on the scoreboard in terms of points
    int position = 0;
      
    // Figure out the player's position
    for (Player sortedPlayer : store.getSortedPlayers()) {
      if (player.getId().equals(sortedPlayer.getId())) break;
      position++;
    }
  
    return scoreBoardPointsStyle.get(position);
  } 
}
