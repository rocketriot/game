package bham.bioshock.client;

import bham.bioshock.client.ui.SceneController;
import bham.bioshock.common.models.MainModel;
import bham.bioshock.client.controller.MainController;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Client {
	private MainModel mainModel;
	private MainController mainController;
	private SceneController sceneController;

	private void run() {
		mainModel = new MainModel();
		mainController = new MainController(mainModel);
		sceneController = new SceneController(mainController);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(sceneController, config);
	}

	public static void main(String[] args) {
		(new Client()).run();
	}
}