package bham.bioshock.communication.messages.minigame;

import java.util.UUID;

import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.minigame.ai.CpuAstronaut;
import bham.bioshock.minigame.models.Astronaut;
import bham.bioshock.minigame.models.Astronaut.Move;

public class MinigamePlayerMoveMessage extends Message {

  private static final long serialVersionUID = 1188177011486338949L;
  
  public final UUID playerId;
  public final Move move;
  
  public MinigamePlayerMoveMessage(Astronaut astronaut) {
    super(Command.MINIGAME_PLAYER_MOVE);
    this.playerId = astronaut.getId();
    this.move = astronaut.getMove().copy();
  }

  public MinigamePlayerMoveMessage(CpuAstronaut astronaut, Move move) {
    super(Command.MINIGAME_PLAYER_MOVE);
    this.playerId = astronaut.get().getId();
    this.move = move.copy();
  }
}
