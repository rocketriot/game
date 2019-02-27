package bham.bioshock.client.screens;

import bham.bioshock.client.AppPreferences;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.XMLInteraction;
import bham.bioshock.client.controllers.SoundController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PreferencesScreen extends ScreenMaster {

  private AppPreferences preferences;
  private XMLInteraction xmlInteraction = new XMLInteraction();

  // labels
  private Label soundVolLabel;
  private Label musicVolLabel;
  private Label soundEnabledLabel;
  private Label musicEnabledLabel;
  private Label titleLabel;

  private Table table;

  public PreferencesScreen(Router router, AppPreferences preferences) {
    super(router);
    this.preferences = preferences;
    stage = new Stage(new ScreenViewport());
    batch = stage.getBatch();
  }

  @Override
  public void show() {
    stage.clear();
    super.show();

    drawButtons();
    Gdx.input.setInputProcessor(stage);
  }

  @Override
  public void render(float delta) {
    super.render(delta);
  }

  private void drawButtons() {

    // sound on or off
    final CheckBox musicCheckBox = new CheckBox(null, skin);
    musicCheckBox.setChecked(preferences.getMusicEnabled());
    musicCheckBox.addListener(
        new EventListener() {
          @Override
          public boolean handle(Event event) {
            router.call(Route.MUSIC_ENABLED, musicCheckBox.isChecked());
            preferences.setMusicEnabled(musicCheckBox.isChecked());

            if (musicCheckBox.isChecked()) {
              router.call(Route.START_MUSIC, "mainMenu");
            }

            return false;
          }
        });

    final CheckBox soundCheckBox = new CheckBox(null, skin);
    soundCheckBox.setChecked(preferences.getSoundsEnabled());
    soundCheckBox.addListener(
        new EventListener() {
          @Override
          public boolean handle(Event event) {
            router.call(Route.SOUNDS_ENABLED, soundCheckBox.isChecked());
            preferences.setSoundsEnabled(soundCheckBox.isChecked());
            return false;
          }
        });

    // volume control
    final Slider musicVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
    musicVolumeSlider.setValue(preferences.getMusicVolume());
    musicVolumeSlider.addListener(
        new EventListener() {
          @Override
          public boolean handle(Event event) {
            router.call(Route.MUSIC_VOLUME, musicVolumeSlider.getValue());
            preferences.setMusicVolume(musicVolumeSlider.getValue());
            return false;
          }
        });

    final Slider soundVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
    soundVolumeSlider.setValue(preferences.getSoundsVolume());
    soundVolumeSlider.addListener(
        new EventListener() {
          @Override
          public boolean handle(Event event) {
            router.call(Route.SOUNDS_VOLUME, soundVolumeSlider.getValue());
            preferences.setSoundsVolume(soundVolumeSlider.getValue());
            return false;
          }
        });

    // Labels
    titleLabel = new Label("Game Preferences", skin);
    musicVolLabel = new Label("Music Volume", skin);
    soundVolLabel = new Label("Sound Volume", skin);
    soundEnabledLabel = new Label("Sound Enabled", skin);
    musicEnabledLabel = new Label("Music Enabled", skin);

    // table
    table = new Table();
    table.setFillParent(true);

    table.add(titleLabel);
    table.row();
    table.add(musicEnabledLabel);
    table.add(musicCheckBox);
    table.row();
    table.add(soundEnabledLabel);
    table.add(soundCheckBox);
    table.row();
    table.add(musicVolLabel);
    table.add(musicVolumeSlider);
    table.row();
    table.add(soundVolLabel);
    table.add(soundVolumeSlider);

    stage.addActor(table);
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void hide() {
  }

  @Override
  protected void setPrevious(){
    backButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        SoundController.playSound("menuSelect");
        //xmlInteraction.preferencesToXML(musicCheckBox.isChecked(), );
        router.back();
      }
    });
  }
}
