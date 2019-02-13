package bham.bioshock.minigame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

public class MainScreen implements Screen {

  private Game game;
  private Renderer renderer;
  private World world;

  public MainScreen(Game game, World world) {
    this.game = game;
    this.world = world;
    this.renderer = new Renderer(world);
  }

  @Override
  public void show() {
    // TODO Auto-generated method stub

  }

  @Override
  public void render(float delta) {
    renderer.render(delta);
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub

  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub

  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub

  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub

  }
}
