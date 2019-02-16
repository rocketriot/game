package bham.bioshock.communication;

import java.io.Serializable;

@SuppressWarnings("serial")
abstract public class Sendable implements Serializable, Cloneable  {
  
  public Sendable clone() {
    try {
      return (Sendable) super.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null;
  }
 
}
