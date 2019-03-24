package bham.bioshock.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import bham.bioshock.client.AppPreferences;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.store.Store;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;
import bham.bioshock.communication.interfaces.MessageService;

public class BoardGameModule extends AbstractModule {
  @Override 
  protected void configure() {
    bind(MessageService.class).to(ClientService.class);
  }
  
  @Provides
  AppPreferences provideAppPreferences(Store store) {
    return store.getPreferences();
  }
  
  @Provides
  GameBoard provideGameBoard(Store store) {
    return store.getGameBoard();
  }
  
  @Provides
  ClientService provideClientService(CommunicationClient client) {
    return client.getConnection().orElse(null);
  }
  
  
}