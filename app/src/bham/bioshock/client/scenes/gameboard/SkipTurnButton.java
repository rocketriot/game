package bham.bioshock.client.scenes.gameboard.hud;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.common.consts.Config;
import bham.bioshock.common.models.store.Store;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SkipTurnButton extends HudElement {
  TextButton endTurnButton;

  public SkipTurnButton(Stage stage, SpriteBatch batch, Skin skin, Store store, Router router) {
    super(stage, batch, skin, store, router);
  }

  protected void setup() {
    endTurnButton = new TextButton("End Turn", skin);

    endTurnButton.setX((Config.GAME_WORLD_WIDTH - endTurnButton.getWidth()) / 2);
    endTurnButton.setY(15);

    endTurnButton.addListener(
      new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          router.call(Route.END_TURN);
        }
    });

    stage.addActor(endTurnButton);
  }

  protected void render() {
    boolean isMainPlayersTurn = store.isMainPlayersTurn();
    endTurnButton.setVisible(isMainPlayersTurn);
  }
}
