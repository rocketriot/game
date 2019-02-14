package bham.bioshock.communication.fake;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.communication.Action;
import bham.bioshock.communication.client.ClientService;
import bham.bioshock.communication.client.IClientService;

public class ClientServiceFake implements IClientService {
  private static final Logger logger = LogManager.getLogger(ClientService.class);
  
  @Override
  public void send(Action action) {
    logger.debug("Received command " + action.getCommand().toString());
  }

}
