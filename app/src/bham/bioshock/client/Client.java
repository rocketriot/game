package bham.bioshock.client;

import bham.bioshock.client.controllers.*;
import bham.bioshock.client.screens.*;
import bham.bioshock.common.models.Model;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.HashMap;

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
		MainMenuController mainMenuController = new MainMenuController(this, model);
		controllers.put(View.MAIN_MENU, mainMenuController);
		screens.put(View.MAIN_MENU, new MainMenuScreen(mainMenuController));

		// How To
		HowToController howToController = new HowToController(this, model);
		controllers.put(View.HOW_TO, howToController);
		screens.put(View.HOW_TO, new HowToScreen(howToController));

		// Preferences
		PreferencesController preferencesController = new PreferencesController(this, model);
		controllers.put(View.PREFERENCES, preferencesController);
		screens.put(View.PREFERENCES, new PreferencesScreen(preferencesController));

		// Host Screen
		HostScreenController hostscreenController = new HostScreenController(this, model);
		controllers.put(View.HOST_SCREEN, hostscreenController);
		screens.put(View.HOST_SCREEN, new HostScreen(hostscreenController));

		// Game Board
		GameBoardController gameBoardController = new GameBoardController(this, model);
		controllers.put(View.GAME_BOARD, gameBoardController);
		screens.put(View.GAME_BOARD, new GameBoardScreen(gameBoardController));
	}

	/** Change the client's screen */
	public void changeScreen(View view) {
		Screen screen = screens.get(view);
		this.setScreen(screen);
	}

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;

		new LwjglApplication(new Client(), config);
	}
}