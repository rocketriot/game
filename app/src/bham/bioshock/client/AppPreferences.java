package bham.bioshock.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class AppPreferences {

    private static final String PREF_MUSIC_VOLUME = "volume";
    private static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREF_SOUND_ENABLED = "sound.enabled";
    private static final String PREF_SOUND_VOLUME = "sound";
    private static final String PREFS_NAME = "rocket_riot";

    public AppPreferences() {

    }

    protected Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    //read and write to XML file using a libary
    //java XML document class


    //getters and setter
    public boolean getSoundEnabled() {
        return getPreferences().getBoolean(PREF_SOUND_ENABLED, true);
    }

    public boolean getMusicEnabled() {
        return getPreferences().getBoolean(PREF_MUSIC_ENABLED, true);
    }

    public float getMusicVolume() {
        return getPreferences().getFloat(PREF_MUSIC_VOLUME);
    }

    public float getSoundVolume() {
        return getPreferences().getFloat(PREF_SOUND_VOLUME);
    }


    public void setPrefSoundEnabled(boolean enabled) {
        getPreferences().putBoolean(PREF_SOUND_ENABLED, enabled);
        getPreferences().flush();
    }

    public void setPrefMusicEnabled(boolean enabled) {
        getPreferences().putBoolean(PREF_MUSIC_ENABLED, enabled);
        getPreferences().flush();
    }
    public void setPrefMusicVolume(float volume) {
        getPreferences().putFloat(PREF_MUSIC_VOLUME, volume);
        getPreferences().flush();
    }
    public void setPrefSoundVolume(float volume) {
        getPreferences().putFloat(PREF_SOUND_VOLUME, volume);
        getPreferences().flush();
    }
}
