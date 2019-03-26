package bham.bioshock.server.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.server.ServerHandler;
import bham.bioshock.testutils.server.FakeServer;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameBoardHandlerTest {
  
  Store store;
  FakeServer server;
  ServerHandler handler;
  Clock clock;

  @BeforeEach
  public void createFakeServer() {
    store = new Store();
    server = new FakeServer();
    clock = new Clock();
    handler = new ServerHandler(store, server, false, clock);
  }
  
  
}
