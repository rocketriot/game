package bham.bioshock.client;

import java.util.HashMap;

import bham.bioshock.client.controllers.*;
import bham.bioshock.client.screens.*;
import bham.bioshock.common.models.Model;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.client.ClientService;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Client extends Game {
	/** An enum to represent all the views */
	public enum View {
		MAIN_MENU, HOW_TO, LOADING, GAME_BOARD, PREFERENCES, HOST_SCREEN
	}

	/** Stores all the controllers */
	private HashMap<View, Controller> controllers = new HashMap<View, Controller>();

	/** Stores all the screens */
	private HashMap<View, Screen> screens = new HashMap<View, Screen>();

	/** Stores all data */
	private Model model;

	private ClientService server;

	@Override
	public void create() {
		model = new Model();
		loadViews();

		// Set the first screen to the main menu
		changeScreen(View.MAIN_MENU);
	}

	/** Loads up the views */
	private void loadViews() {
		// Main Menu
		MainMenuController mainMenuController = new MainMenuController(this);
		MainMenuScreen mainMenuScreen = new MainMenuScreen(mainMenuController);
		mainMenuController.setScreen(mainMenuScreen);
		controllers.put(View.MAIN_MENU, mainMenuController);
		screens.put(View.MAIN_MENU, mainMenuScreen);

		// How To
		HowToController howToController = new HowToController(this);
		HowToScreen howToScreen = new HowToScreen(howToController);
		howToController.setScreen(howToScreen);
		controllers.put(View.HOW_TO, howToController);
		screens.put(View.HOW_TO, howToScreen);

		// Preferences
		PreferencesController preferencesController = new PreferencesController(this);
		PreferencesScreen preferencesScreen = new PreferencesScreen(preferencesController);
		preferencesController.setScreen(preferencesScreen);
		controllers.put(View.PREFERENCES, preferencesController);
		screens.put(View.PREFERENCES, preferencesScreen);

		// Host Screen
		HostScreenController hostScreenController = new HostScreenController(this);
		HostScreen hostScreen = new HostScreen(hostScreenController);
		hostScreenController.setScreen(hostScreen);
		controllers.put(View.HOST_SCREEN, hostScreenController);
		screens.put(View.HOST_SCREEN, hostScreen);

		// Game Board
		GameBoardController gameBoardController = new GameBoardController(this);
		GameBoardScreen gameBoardScreen = new GameBoardScreen(gameBoardController);
		gameBoardController.setScreen(gameBoardScreen);
		controllers.put(View.GAME_BOARD, gameBoardController);
		screens.put(View.GAME_BOARD, gameBoardScreen);
	}

	public void handleServerMessages(Action action) {
		switch (action.getCommand()) {
		case ADD_PLAYER: {
			HostScreenController controller = (HostScreenController) controllers.get(View.HOST_SCREEN);
			controller.onPlayerJoined(action);
			break;
		}
		case START_GAME: {
			HostScreenController controller = (HostScreenController) controllers.get(View.HOST_SCREEN);
			controller.onStartGame(action);
			break;
		}
		default: {
			System.out.println("Received unhandled command: " + action.getCommand().toString());
		}
		}
	}

	/** Change the client's screen */
	public void changeScreen(View view) {
		Screen screen = screens.get(view);
		this.setScreen(screen);
	}

	public Model getModel() {
		return model;
	}

	public ClientService getServer() {
		return server;
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;

		new LwjglApplication(new Client(), config);
	}
}