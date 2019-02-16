package bham.bioshock.common.models.store;

import bham.bioshock.minigame.World;

public class MinigameStore {

  private World world;
  
  public MinigameStore() {
    
  }

  public World getWorld() {
    return world;
  }

  public void setWorld(World world) {
    this.world = world;
  }
}
