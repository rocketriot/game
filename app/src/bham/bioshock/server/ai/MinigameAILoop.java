package bham.bioshock.server.ai;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import bham.bioshock.minigame.ai.MinigameAI;

/** The Minigame ai loop. */
public class MinigameAILoop extends Thread {

  private static final Logger logger = LogManager.getLogger(MinigameAILoop.class);

  /** The list of AI Handlers */
  private ArrayList<MinigameAI> aiHandlers = new ArrayList<>();

  /** The loop time */
  private final int LOOP_TIME = 1000;

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

  /** Finish the loop. */
  public void finish() {
    this.interrupt();
  }

  /**
   * Register the handler.
   *
   * @param ais all of the ai to register
   */
  public void registerHandler(MinigameAI ais) {
    synchronized(aiHandlers) {
      aiHandlers.add(ais);      
    }
  }

}
