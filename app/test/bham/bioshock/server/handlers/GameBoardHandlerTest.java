package bham.bioshock.server.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.common.utils.Clock;
import bham.bioshock.server.ServerHandler;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameBoardHandlerTest {
  
  Store store;
  ServerHandler handler;
  Clock clock;

  @BeforeEach
  public void createFakeServer() {
    store = new Store();
    clock = new Clock();
    handler = new ServerHandler(store, false, clock);
  }
  
  
}
