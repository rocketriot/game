package bham.bioshock.minigame;

import java.util.ArrayList;

public class Clock {

  private long time = 0; // Time in miliseconds
  private long MAX_TIME = 10 * 60 * 1000; // Max = 10 minutes
  private final ArrayList<TimeListener> listeners = new ArrayList<>();
  
  
  public void update(int delta) {
    time += delta;
    if(time > MAX_TIME) {
      time -= MAX_TIME;
    }
    
    for(TimeListener l : listeners) {
      if((l.at != 0 && l.lastCall == 0f && time > l.at) || (l.every != 0 && time - l.lastCall > l.every)) {
        l.update(time);
        l.handle(new TimeUpdateEvent(time));      
      }
    }
  }
  
  public void update(float delta) {
    update((int) delta * 1000);
  }
  
  public void every(float second, TimeListener listener) {
    listener.setEvery(second);
    if(!listeners.contains(listener)) {
      listeners.add(listener);      
    }
  }
  
  public void at(float second, TimeListener listener) {
    listener.setAt(second * 1000);
    if(!listeners.contains(listener)) {
      listeners.add(listener);      
    }
  }
  
  public void everySecond(TimeListener listener) {
    every(1, listener);
  }
  
  abstract public static class TimeListener {
 
    int lastCall = 0; 
    int every = 0;
    int at = 0;
    
    void update(float time) {
      lastCall = (int) (time * 1000);
    }
    
    void setEvery(float second) {
      every = (int) (second * 1000);
    }
    
    void setAt(float second) {
      at = (int) (second * 1000);
    }
    
    public abstract void handle(TimeUpdateEvent event); 
  }
  
  
  static public class TimeUpdateEvent {
    public final long time;
    
    TimeUpdateEvent(long time) {
      this.time = time;
    }
  }
}
