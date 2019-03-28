package bham.bioshock.client.screens;

import bham.bioshock.client.AppPreferences;
import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import bham.bioshock.client.XMLInteraction;
import bham.bioshock.client.assets.AssetContainer;
import bham.bioshock.client.controllers.SoundController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/** The Preferences Screen. */
public class PreferencesScreen extends ScreenMaster {

  private AppPreferences preferences;
  private XMLInteraction xmlInteraction = new XMLInteraction();

  /** Labels */
  private Label soundVolLabel;

  private Label musicVolLabel;
  private Label soundEnabledLabel;
  private Label musicEnabledLabel;
  private Label titleLabel;

  /** The table that the screen elements are added to */
  private Table table;

  /** Variables to keep track of current preferences */
  private boolean musicEnabled;

  private float musicVolume;
  private boolean soundsEnabled;
  private float soundsVolume;

  private Route backRoute;

  /**
   * Instantiates a new Preferences screen.
   *
   * @param router the router
   * @param preferences the passed current user preferences
   */
  public PreferencesScreen(Router router, AppPreferences preferences, AssetContainer assets) {
    super(router, assets);
    this.preferences = preferences;

    musicEnabled = preferences.getMusicEnabled();
    musicVolume = preferences.getMusicVolume();
    soundsEnabled = preferences.getSoundsEnabled();
    soundsVolume = preferences.getSoundsVolume();
  }

  public PreferencesScreen(
      Router router, AppPreferences preferences, Route backRoute, AssetContainer assets) {
    this(router, preferences, assets);
    this.backRoute = backRoute;
  }

  @Override
  public void show() {
    stage.clear();
    super.show();

    drawBackButton();

    drawButtons();
    Gdx.input.setInputProcessor(stage);
    if (musicEnabled) {
      router.call(Route.START_MUSIC, "mainMenu");
    }
  }

  @Override
  public void render(float delta) {
    super.render(delta);
  }

  /** Method to draw all the buttons and add the listeners to them */
  private void drawButtons() {
    // sound on or off
    final CheckBox musicCheckBox = new CheckBox(null, skin);
    musicCheckBox.setChecked(musicEnabled);
    musicCheckBox.addListener(
        new EventListener() {
          @Override
          public boolean handle(Event event) {
            musicEnabled = musicCheckBox.isChecked();
            router.call(Route.MUSIC_ENABLED, musicEnabled);
            preferences.setMusicEnabled(musicEnabled);
            return false;
          }
        });

    final CheckBox soundCheckBox = new CheckBox(null, skin);
    soundCheckBox.setChecked(soundsEnabled);
    soundCheckBox.addListener(
        new EventListener() {
          @Override
          public boolean handle(Event event) {
            soundsEnabled = soundCheckBox.isChecked();
            router.call(Route.SOUNDS_ENABLED, soundsEnabled);
            preferences.setSoundsEnabled(soundsEnabled);
            return false;
          }
        });

    // volume control
    final Slider musicVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
    musicVolumeSlider.setValue(musicVolume);
    musicVolumeSlider.addListener(
        new EventListener() {
          @Override
          public boolean handle(Event event) {
            musicVolume = musicVolumeSlider.getValue();
            router.call(Route.MUSIC_VOLUME, musicVolume);
            preferences.setMusicVolume(musicVolume);
            return false;
          }
        });

    final Slider soundVolumeSlider = new Slider(0f, 1f, 0.1f, false, skin);
    soundVolumeSlider.setValue(soundsVolume);
    soundVolumeSlider.addListener(
        new EventListener() {
          @Override
          public boolean handle(Event event) {
            soundsVolume = soundVolumeSlider.getValue();
            router.call(Route.SOUNDS_VOLUME, soundsVolume);
            preferences.setSoundsVolume(soundsVolume);
            return false;
          }
        });

    // Crete the labels
    titleLabel = new Label("Game Preferences", skin);
    musicVolLabel = new Label("Music Volume", skin);
    soundVolLabel = new Label("Sound Volume", skin);
    soundEnabledLabel = new Label("Sound Enabled", skin);
    musicEnabledLabel = new Label("Music Enabled", skin);

    // Create the labels
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
  public void pause() {}

  @Override
  public void resume() {}

  @Override
  public void hide() {}

  /**
   * Include writing the new preferences to the XML file into the setPrevious method - requires
   * overriding the super method
   */
  @Override
  protected void setPrevious() {
    backButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            SoundController.playSound("menuSelect");
            xmlInteraction.preferencesToXML(musicEnabled, musicVolume, soundsEnabled, soundsVolume);

            if (backRoute != null) {
              router.call(backRoute);
            } else {
              router.back();
            }
          }
        });
  }
}
