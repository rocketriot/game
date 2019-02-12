package bham.bioshock.communication.fake;

public class ServerFake extends Thread {
  
  private FakeStore fakeStore;
  
  public ServerFake(FakeStore fakeStore) {
    this.fakeStore = fakeStore;
  }
}
