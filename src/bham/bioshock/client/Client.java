package bham.bioshock.client;

import bham.bioshock.common.models.GameBoard;
import bham.bioshock.client.ui.UI;
import bham.bioshock.client.renderer.Renderer;

public class Client {
	private GameBoard gameBoard;
	private Renderer renderer;
	private UI ui;

	private void run() {
		gameBoard = new GameBoard();
		renderer = new Renderer();
		ui = new UI();
	}

	public static void main(String[] args) {
		(new Client()).run();
	}
}