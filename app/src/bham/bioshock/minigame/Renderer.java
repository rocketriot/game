package bham.bioshock.minigame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import bham.bioshock.common.consts.Config;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;

public class Renderer {

	private World w;
	private Player mainPlayer;
	private ArrayList<Entity> entities;
	
	private OrthographicCamera cam;
	private Sprite background;
	private Stage stage;
	private SpriteBatch batch;
	private SpriteBatch backgroundBatch;
	private Viewport viewport;

	private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
	private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
	

	public Renderer(World _w) {
		w = _w;
		mainPlayer = w.getMainPlayer();
		
		entities = new ArrayList<Entity>();
		entities.addAll(w.getPlayers());
		entities.addAll(w.getRockets());

		cam = new OrthographicCamera();
		
		batch = new SpriteBatch();
		backgroundBatch = new SpriteBatch();
		
		cam.update();

		loadSprites();
	}

	public void loadSprites() {
		viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, cam);
		Player.loadTextures();
		Rocket.loadTextures();
		stage = new Stage(viewport);
		background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));
	}

	public void render(float delta) {
		batch.setProjectionMatrix(cam.combined);
		
		updatePosition();
		cam.position.set(mainPlayer.getX(), GAME_WORLD_HEIGHT / 2, 0);
		cam.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		backgroundBatch.begin();
		backgroundBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		backgroundBatch.end();
		
		batch.begin();
		drawPlayers();
		drawMainPlayer();
		
		batch.end();
	}
	
	public void drawPlayers() {
		for(Entity e : entities) {
			batch.draw(e.getTexture(), e.getX(), e.getY(), e.getSize(), e.getSize());
		}
	}
	
	public void updatePosition() {
		float dt = Gdx.graphics.getDeltaTime();
		mainPlayer.update(dt);
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
			mainPlayer.moveLeft(dt);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
			mainPlayer.moveRight(dt);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			mainPlayer.jump(dt);
		}
	}
	
	public void drawMainPlayer() {
		batch.draw(mainPlayer.getTexture(), mainPlayer.getX(), mainPlayer.getY(), mainPlayer.getSize(), mainPlayer.getSize());
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
}
