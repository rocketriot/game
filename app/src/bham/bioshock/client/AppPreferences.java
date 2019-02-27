package bham.bioshock.client;

import javax.xml.bind.annotation.XmlElement;

public class AppPreferences {

  private boolean music_enabled;
  private float music_volume;
  private float sounds_volume;
  private boolean sounds_enabled;

  public AppPreferences(boolean music_enabled, float music_volume, boolean sounds_enabled, float sounds_volume){
    this.music_enabled = music_enabled;
    this.music_volume = music_volume;
    this.sounds_enabled = sounds_enabled;
    this.music_volume = music_volume;
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

  @XmlElement(name = "music_enabled")
  public void setMusicEnabled(boolean enabled) {
    music_enabled = enabled;
  }

  @XmlElement(name = "music_volume")
  public void setMusicVolume(float volume) {
    music_volume = volume;
  }

  @XmlElement(name = "sounds_enabled")
  public void setSoundsEnabled(boolean enabled) {
    sounds_enabled = enabled;
  }

  @XmlElement(name = "sounds_volume")
  public void setSoundsVolume(float volume) {
    sounds_volume = volume;
  }
}
