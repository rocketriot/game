package bham.bioshock.client;
public class AppPreferences {

  private float music_volume = 1f;
  private boolean music_enabled = true;
  private float sounds_volume = 1f;
  private boolean sounds_enabled = true;

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

  public void setSoundsEnabled(boolean enabled) {
    sounds_enabled = enabled;
  }

  public void setMusicEnabled(boolean enabled) {
    music_enabled = enabled;
  }

  public void setMusicVolume(float volume) {
    music_volume = volume;
  }

  public void setSoundVolume(float volume) {
    sounds_volume = volume;
  }
}
