package bham.bioshock.minigame.ai;

import bham.bioshock.minigame.models.Astronaut;

public class CpuAstronaut {

  Astronaut astronaut;
  
  public CpuAstronaut(Astronaut a) {
    this.astronaut = a;
  }
  
  public void moveLeft() {
    astronaut.moveLeft(true);
    astronaut.moveRight(false);
  }
  
  public void moveRight() {
    astronaut.moveRight(true);
    astronaut.moveLeft(false);
  }
  
  public void jump() {
    astronaut.jump(true);
  }

  public void moveChange() {
    astronaut.moveChange();
    astronaut.jump(false);
  }
  
  public Astronaut get() {
    return astronaut;
  }


  
}
