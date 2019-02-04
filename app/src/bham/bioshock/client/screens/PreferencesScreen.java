package bham.bioshock.client.screens;

import bham.bioshock.client.Client;
import bham.bioshock.client.controllers.PreferencesController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;



public class PreferencesScreen extends ScreenMaster {


    //labels
    private Label soundVolLabel;
    private Label musicVolLabel;
    private Label soundEnabledLabel;
    private Label musicEnabledLabel;
    private Label titleLabel;

    private Table table;

    public PreferencesScreen(PreferencesController controller) {
        this.controller = controller;
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

    private void drawButtons(){

        //sound on or off
        final CheckBox musicCheckBox = new CheckBox(null, skin);
        musicCheckBox.setChecked(controller.getPreferences().getMusicEnabled());
        musicCheckBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                controller.getPreferences().setPrefMusicEnabled(musicCheckBox.isChecked());
                return false;
            }
        });

        final CheckBox soundCheckBox = new CheckBox(null, skin);
        soundCheckBox.setChecked(controller.getPreferences().getSoundEnabled());
        soundCheckBox.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                controller.getPreferences().setPrefSoundEnabled(soundCheckBox.isChecked());
                return false;
            }
        });

        //volume control
        final Slider musicVolumeSlider = new Slider(0f, 1f, 0.2f, false, skin);
        musicVolumeSlider.setValue(controller.getPreferences().getMusicVolume());
        musicVolumeSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                controller.getPreferences().setPrefMusicVolume( musicVolumeSlider.getValue());
                return false;
            }
        });

        final Slider soundVolumeSlider = new Slider(0f, 1f, 0.2f, false, skin);
        soundVolumeSlider.setValue(controller.getPreferences().getSoundVolume());
        soundVolumeSlider.addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                controller.getPreferences().setPrefSoundVolume( soundVolumeSlider.getValue());
                return false;
            }
        });

        //Labels
        titleLabel = new Label("Game Preferences", skin);
        musicVolLabel = new Label("Music Volume", skin);
        soundVolLabel = new Label("Sound Volume", skin);
        soundEnabledLabel = new Label("Sound Enabled", skin );
        musicEnabledLabel = new Label("Music Enabled", skin);

        //table
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


}
