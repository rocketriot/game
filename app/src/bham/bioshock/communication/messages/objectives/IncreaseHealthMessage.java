package bham.bioshock.communication.messages.objectives;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * The Increase health message.
 */
public class IncreaseHealthMessage extends Message {

  private static final long serialVersionUID = 6806469893148362075L;

  /**
   * The Player id.
   */
  public final UUID playerID;
  /**
   * The Heart id.
   */
  public final UUID heartID;

  /**
   * The time created.
   */
  public final long created;

  /**
   * Instantiates a new Increase health message.
   *
   * @param playerID the player id
   * @param heartID the heart id
   */
  public IncreaseHealthMessage(UUID playerID, UUID heartID) {
    super(Command.MINIGAME_OBJECTIVE);
    this.playerID = playerID;
    this.heartID = heartID;
    this.created = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
  }

}
