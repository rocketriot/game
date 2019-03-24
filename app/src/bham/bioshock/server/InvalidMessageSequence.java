package bham.bioshock.server;

public class InvalidMessageSequence extends Exception {
  
  private static final long serialVersionUID = -2030184690800029996L;

  public InvalidMessageSequence(String message) {
    super(message);
  }
}
