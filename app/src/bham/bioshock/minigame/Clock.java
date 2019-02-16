package bham.bioshock.minigame;

import java.util.ArrayList;

public class Clock {

  private float time = 0f;
  private final ArrayList<TimeListener> listeners = new ArrayList<>();
  
  
  public void update(float delta) {
    time += delta;
    
    for(TimeListener l : listeners) {
      if(time - l.lastCall > l.every) {
        l.update(time);
        l.handle(new TimeUpdateEvent(time));      
      }
    }
  }
  
  public void every(float second, TimeListener listener) {
    listener.setEvery(second);
    if(!listeners.contains(listener)) {
      listeners.add(listener);      
    }
  }
  
  public void everySecond(TimeListener listener) {
    every(1, listener);
  }
  
  abstract public class TimeListener {
 
    float lastCall = 0f; 
    float every = 0;
    
    void update(float time) {
      lastCall = time;
    }
    
    void setEvery(float second) {
      every = second;
    }
    
    public abstract boolean handle(TimeUpdateEvent event); 
  }
  
  
  static public class TimeUpdateEvent {
    public final float time;
    
    TimeUpdateEvent(float time) {
      this.time = time;
    }
  }
}
