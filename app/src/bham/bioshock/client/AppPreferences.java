package bham.bioshock.client;

public class AppPreferences {

  private boolean musicEnabled;
  private float musicVolume;
  private float soundsVolume;
  private boolean soundsEnabled;

  /**
   * Creates app preferences class with saved values
   * 
   * @param musicEnabled
   * @param musicVolume
   * @param soundsEnabled
   * @param soundsVolume
   */
  public AppPreferences(boolean musicEnabled, float musicVolume, boolean soundsEnabled,
      float soundsVolume) {
    this.musicEnabled = musicEnabled;
    this.musicVolume = musicVolume;
    this.soundsEnabled = soundsEnabled;
    this.soundsVolume = soundsVolume;
  }

  /**
   * Returns true if sound is enabled
   * 
   * @return
   */
  public boolean getSoundsEnabled() {
    return soundsEnabled;
  }

  /**
   * Returns true if music is enabled
   * @return
   */
  public boolean getMusicEnabled() {
    return musicEnabled;
  }

  /**
   * Returns music volume
   * 
   * @return
   */
  public float getMusicVolume() {
    return musicVolume;
  }

  /**
   * Returns sound volume 
   * 
   * @return
   */
  public float getSoundsVolume() {
    return soundsVolume;
  }

  public void setMusicEnabled(boolean enabled) {
    musicEnabled = enabled;
  }

  public void setMusicVolume(float volume) {
    musicVolume = volume;
  }

  public void setSoundsEnabled(boolean enabled) {
    soundsEnabled = enabled;
  }

  public void setSoundsVolume(float volume) {
    soundsVolume = volume;
  }
}
