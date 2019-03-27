package bham.bioshock.common.utils;

import java.util.ArrayList;

public class Clock {

  private long time = 0; // Time in miliseconds
  private long MAX_TIME = 10 * 60 * 1000; // Max = 10 minutes
  private final ArrayList<TimeListener> listeners = new ArrayList<>();
  
  
  /**
   * Update current clock state
   * @param delta
   */
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
  
  /**
   * Call listener every n seconds
   * 
   * @param delta
   */
  public void every(float second, TimeListener listener) {
    listener.setEvery(second);
    if(!listeners.contains(listener)) {
      listeners.add(listener);      
    }
  }
  
  /**
   * Call listener once, after n seconds
   * 
   * @param second
   * @param listener
   */
  public void at(float second, TimeListener listener) {
    listener.setAt(second);
    if(!listeners.contains(listener)) {
      listeners.add(listener);      
    }
  }
  
  /**
   * Reset the clock
   */
  public void reset() {
    time = 0;
  }
  
  abstract public static class TimeListener {
 
    long lastCall = 0; 
    int every = 0;
    int at = 0;
    
    void update(long time) {
      lastCall = time;
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
