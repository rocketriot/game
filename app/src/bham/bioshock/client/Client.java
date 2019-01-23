package bham.bioshock.client;

import bham.bioshock.common.models.GameBoard;
import bham.bioshock.client.ui.UI;
import bham.bioshock.client.renderer.Renderer;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Client {
	private GameBoard gameBoard;
	private Renderer renderer;
	private UI ui;

	private void run() {
		gameBoard = new GameBoard();
		renderer = new Renderer();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new UI(), config);
	}

	public static void main(String[] args) {
		(new Client()).run();
	}
}