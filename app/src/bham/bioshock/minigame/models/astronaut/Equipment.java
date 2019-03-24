package bham.bioshock.minigame.models.astronaut;

import java.io.Serializable;

public class Equipment implements Serializable {
  
  private static final long serialVersionUID = -524979181032068874L;
  
  public boolean haveGun = false;
  public boolean haveShield = false;
  public int shieldHealth = 4;
  
  public Equipment copy() {
    Equipment e = new Equipment();
    e.haveGun = haveGun;
    e.haveShield = haveShield;
    e.shieldHealth = shieldHealth;
    return e;
  }
  
  public void removeShieldHealth() {
    if(shieldHealth <= 1) {
      haveShield = false;
    } else {
      shieldHealth--;
    }
  }
}
