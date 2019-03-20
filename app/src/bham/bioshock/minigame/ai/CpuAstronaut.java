package bham.bioshock.minigame.ai;

import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Astronaut.Move;

public class CpuAstronaut {

  Astronaut astronaut;
  Move move;
  
  public CpuAstronaut(Astronaut a) {
    this.astronaut = a;
    move = new Move();
  }
  
  public void moveLeft() {
    move.movingLeft = true;
    move.movingRight = false;
  }
  
  public void moveRight() {
    move.movingLeft = false;
    move.movingRight = true;
  }
  
  public void jump() {
    move.jumping = true;
  }
  
  public Astronaut get() {
    return astronaut;
  }
  
  public Move endMove() {
    Move oldMove = move;
    move = new Move();
    return oldMove;
  }
  
}
