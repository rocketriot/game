package bham.bioshock.minigame.models.astronaut;

import java.io.Serializable;

public class AstronautMove implements Serializable {

  private static final long serialVersionUID = 3668803304780843571L;
  public boolean jumping = false;
  public boolean movingLeft = false;
  public boolean movingRight = false;
  
  public AstronautMove copy() {
    AstronautMove m = new AstronautMove();
    m.jumping = jumping;
    m.movingLeft = movingLeft;
    m.movingRight = movingRight;
    return m;
  }
  
  public String toString() {
    return "j: " + jumping + ", l: " + movingLeft + ", r: "+ movingRight;
  }
}