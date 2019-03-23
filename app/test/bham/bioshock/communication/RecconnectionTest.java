package bham.bioshock.communication;

import org.junit.jupiter.api.Test;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.server.ServerHandler;
import bham.bioshock.testutils.FakeServer;

public class RecconnectionTest {

  @Test
  public void test() {
    Store store = new Store();
    FakeServer server = new FakeServer();
    ServerHandler handler = new ServerHandler(store, server, false);
    
  }
  
}
