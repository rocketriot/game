package bham.bioshock.server.ai;

import bham.bioshock.minigame.ai.MinigameAI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class MinigameAILoop extends Thread {

  private static final Logger logger = LogManager.getLogger(MinigameAILoop.class);

  private ArrayList<MinigameAI> aiHandlers = new ArrayList<>();

  private int LOOP_TIME = 1000;

  @Override
  public void run() {
    long currentTime = System.currentTimeMillis();
    try {
      while (!isInterrupted()) {
        long timeDelta = System.currentTimeMillis() - currentTime;
        currentTime = System.currentTimeMillis();

        synchronized(aiHandlers) {
          // If the handler is defined, call it
          for (MinigameAI handler : aiHandlers) {
            handler.run((float) (timeDelta / 1000));
          }
        }

        sleep(LOOP_TIME);
      }
    } catch (InterruptedException e) {
      // Fail quietly
      logger.debug("Minigame AI interrupted");
    }
  }

  public void finish() {
    this.interrupt();
  }

  public void registerHandler(MinigameAI ais) {
    synchronized(aiHandlers) {
      aiHandlers.add(ais);
    }
  }

}
