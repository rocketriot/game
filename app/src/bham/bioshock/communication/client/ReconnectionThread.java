package bham.bioshock.communication.client;

import bham.bioshock.client.Route;
import bham.bioshock.client.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class ReconnectionThread extends Thread {

  private static final Logger logger = LogManager.getLogger(ReconnectionThread.class);

  private Router router;
  private CommunicationClient commClient;

  public ReconnectionThread(CommunicationClient commClient, Router router) {
    super("ReconnectionThread");
    this.router = router;
    this.commClient = commClient;
  }

  public void run() {
    try {
      boolean connecting = false;
      while (!isInterrupted()) {
        Optional<ClientService> service = commClient.getConnection();

        if (!service.isPresent() || !service.get().isCreated()) {
          router.call(Route.RECONNECT, true);

          if (!connecting) {
            connecting = true;
            boolean successful = commClient.reconnect(null);
            if (successful) {
              router.call(Route.SEND_RECONNECT);
            } else {
              connecting = false;
            }
          }
        } else {
          connecting = false;
        }
        sleep(3000);
      }
    } catch (InterruptedException e) {
      logger.debug("Reconnection thread stopped");
    }
  }
}
