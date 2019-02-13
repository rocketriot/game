package bham.bioshock.minigame;


import java.awt.*;
import java.util.ArrayList;


import bham.bioshock.common.consts.Config;
import bham.bioshock.minigame.models.Entity;
import bham.bioshock.minigame.models.Player;
import bham.bioshock.minigame.models.Rocket;
import bham.bioshock.minigame.physics.Gravity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class Renderer {

	private World w;
	private Player mainPlayer;
	private ArrayList<Entity> entities;
	
	ShapeRenderer renderer;
	private OrthographicCamera cam;
	Vector3 lerpTarget = new Vector3();
	private Sprite background;
	private Stage stage;
	private SpriteBatch batch;
	private SpriteBatch backgroundBatch;
	private Viewport viewport;
	private double camRotation;
	
	private final int GAME_WORLD_WIDTH = Config.GAME_WORLD_WIDTH;
	private final int GAME_WORLD_HEIGHT = Config.GAME_WORLD_HEIGHT;
	private Circle mainPlanet;
	//private Rectangle


	public Renderer(World _w) {
		w = _w;
		mainPlayer = w.getMainPlayer();
		renderer = new ShapeRenderer();
		
		entities = new ArrayList<Entity>();
		entities.addAll(w.getPlayers());
		entities.addAll(w.getRockets());

		cam = new OrthographicCamera();
		
		batch = new SpriteBatch();
		backgroundBatch = new SpriteBatch();
		camRotation = 0;
		
		cam.update();

		loadSprites();
	}

	public void loadSprites() {
		viewport = new FitViewport(GAME_WORLD_WIDTH, GAME_WORLD_HEIGHT, cam);
		Player.loadTextures();
		Rocket.loadTextures();
		stage = new Stage(viewport);
		background = new Sprite(new Texture(Gdx.files.internal("app/assets/backgrounds/game.png")));
		
		mainPlayer.load();
		for(Entity e : entities) {
			e.load();
		}
	}

	public void render(float delta) {
		batch.setProjectionMatrix(cam.combined);
		renderer.setProjectionMatrix(cam.combined);
		
		updatePosition();
		cam.position.lerp(lerpTarget.set(mainPlayer.getX(), mainPlayer.getY(), 0), 3f*delta);
		
		double rotation = -Gravity.getAngleTo(cam.position.x, cam.position.y);
		cam.rotate((float) (camRotation - rotation));
		camRotation = rotation;
		cam.update();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		backgroundBatch.begin();
		backgroundBatch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		backgroundBatch.end();
		
		drawPlanet();
		batch.begin();
		drawEntities();
		drawMainPlayer();
		Rectangle border= mainPlayer.getRectangle();
		//drawBorder(border);
		batch.end();
	}
	
	public void drawPlanet() {
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.SALMON);

		renderer.circle(0, 0, (float) World.PLANET_RADIUS);
		// bounding circle
		mainPlanet = new Circle(0,0,(float)World.PLANET_RADIUS-50);
		renderer.setColor(Color.RED);
		renderer.circle(0,0,(float)World.PLANET_RADIUS-50);

		renderer.end();
	}

	public void drawBorder(Rectangle border){
		renderer.begin(ShapeType.Filled);
		renderer.setColor(Color.BLACK);
		//float xcenter = (Gdx.graphics.getWidth() - mainPlayer.getSprite().getWidth()) / 2.0f;
		//float ycenter = (Gdx.graphics.getHeight() - mainPlayer.getSprite().getHeight()) / 2.0f;
		//center : ( (x1 +x2)/2 ,(y1 + y2)/2 ).
		//oat xbr = mainPlayer.getX() + border.getWidth();
		//float ybr = mainPlayer.getY() + border.getHeight();
		//float xcenter = (mainPlayer.getX() + xbr)/2;
		//float ycenter = (mainPlayer.getY() + ybr)/2;
		//renderer.circle(xcenter,ycenter,100);

		renderer.rect(border.getX(), border.getY(), border.getWidth(),border.getHeight());
		renderer.end();
	}
	
	public void drawEntities() {
		for(Entity e : entities) {
			Sprite sprite = e.getSprite();
			sprite.setPosition(e.getX(), e.getY());
			sprite.setRotation((float) e.getRotation());
			sprite.draw(batch);
			e.update(Gdx.graphics.getDeltaTime());
		}
	}
	
	public void updatePosition() {
		float dt = Gdx.graphics.getDeltaTime();
		mainPlayer.update(dt);
		float xcenter = (Gdx.graphics.getWidth() - mainPlayer.getSprite().getWidth()) / 2.0f;
		float ycenter = (Gdx.graphics.getHeight() - mainPlayer.getSprite().getHeight()) / 2.0f;

		System.out.println(xcenter + "," +ycenter);

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
			mainPlayer.moveLeft(dt);

			//if()
			//if(Intersector.overlaps(mainPlanet,mainPlayer.getRectangle()))
				//System.out.println(mainPlayer.getRectangle());
				//mainPlayer.col(dt);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
			//System.out.println(mainPlayer.getRectangle());
			mainPlayer.moveRight(dt);

			//if(Intersector.overlaps(mainPlanet,mainPlayer.getRectangle())){
				//System.out.println(mainPlayer.getRectangle());
				//mainPlayer.col(dt);

			//}
		}
		if(Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			mainPlayer.jump(dt);

			//if(Intersector.overlaps(mainPlanet,mainPlayer.getRectangle())){
				//System.out.println(mainPlayer.getRectangle());
				//mainPlayer.col(dt);
			//}

		}

		}

	
	public void drawMainPlayer() {
		Sprite sprite = mainPlayer.getSprite();
		sprite.setRegion(mainPlayer.getTexture());
		sprite.setPosition(mainPlayer.getX(), mainPlayer.getY());
		sprite.setRotation((float) -mainPlayer.angleFromCenter());

		sprite.draw(batch);
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
}
