package bham.bioshock.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import bham.bioshock.client.AppPreferences;
import bham.bioshock.common.models.GameBoard;
import bham.bioshock.common.models.Store;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.CommunicationClient;
import bham.bioshock.communication.client.IClientService;
import bham.bioshock.communication.fake.ClientServiceFake;

public class BoardGameModule extends AbstractModule {
  @Override 
  protected void configure() {
    bind(IClientService.class)
      .annotatedWith(Names.named("Fake"))
      .to(ClientServiceFake.class);
    
    bind(IClientService.class).to(ClientService.class);
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
    return client.getConnection();
  }
  
  
}