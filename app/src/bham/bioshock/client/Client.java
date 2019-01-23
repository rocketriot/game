package bham.bioshock.client;

import bham.bioshock.client.ui.SceneController;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.client.ui.*;
import bham.bioshock.client.gamelogic.GameLogic;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Client {
	private GameBoard gameBoard;
	private GameLogic gameLogic;
	private SceneController ui;

	private void run() {
		gameBoard = new GameBoard();
		gameLogic = new GameLogic();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new SceneController(), config);
	}

	public static void main(String[] args) {
		(new Client()).run();
	}
}