package bham.bioshock.client;

public class AppPreferences {

  private boolean music_enabled;
  private float music_volume;
  private float sounds_volume;
  private boolean sounds_enabled;

  public AppPreferences(boolean music_enabled, float music_volume, boolean sounds_enabled,
      float sounds_volume) {
    this.music_enabled = music_enabled;
    this.music_volume = music_volume;
    this.sounds_enabled = sounds_enabled;
    this.sounds_volume = sounds_volume;
  }

  public boolean getSoundsEnabled() {
    return sounds_enabled;
  }

  public boolean getMusicEnabled() {
    return music_enabled;
  }

  public float getMusicVolume() {
    return music_volume;
  }

  public float getSoundsVolume() {
    return sounds_volume;
  }

  public void setMusicEnabled(boolean enabled) {
    music_enabled = enabled;
  }

  public void setMusicVolume(float volume) {
    music_volume = volume;
  }

  public void setSoundsEnabled(boolean enabled) {
    sounds_enabled = enabled;
  }

  public void setSoundsVolume(float volume) {
    sounds_volume = volume;
  }
}
