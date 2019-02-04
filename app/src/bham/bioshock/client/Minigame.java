package bham.bioshock.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import bham.bioshock.minigame.MainScreen;
import bham.bioshock.minigame.World;

public class Minigame extends Game {
	
	private World world;
	
	@Override
	public void create() {
		world = new World();
		setScreen(new MainScreen(this, world));
	}
	
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.foregroundFPS = 60;

		new LwjglApplication(new Minigame(), config);
	}
}
