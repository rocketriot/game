package bham.bioshock.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class AppPreferences {

    private static final String PREF_MUSIC_VOLUME = "volume";
    private static final String PREF_MUSIC_ENABLED = "music.enabled";
    private static final String PREF_SOUND_ENABLED = "sound.enabled";
    private static final String PREF_SOUND_VOL = "sound";
    private static final String PREFS_NAME = "bioshock_prefs";

    protected Preferences getPreferences() {
        return Gdx.app.getPreferences(PREFS_NAME);
    }

    //getters and setter
    public float getMusicVolume() {
        return getPreferences().getFloat(PREF_MUSIC_VOLUME);
    }

    public void setMusicVolume(float volume) {
        getPreferences().putFloat(PREF_MUSIC_VOLUME, volume);
        getPreferences().flush();

    }
}
