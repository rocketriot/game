package bham.bioshock.communication.messages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import bham.bioshock.common.models.Player;
import bham.bioshock.communication.Command;

public class AddPlayerMessage extends Message {
  
  private static final long serialVersionUID = 3475783192952177392L;
  
  public final ArrayList<JoiningPlayer> players = new ArrayList<>();  

  public AddPlayerMessage(Player player) {
    super(Command.ADD_PLAYER);
    this.players.add(new JoiningPlayer(player));
  }
  
  public AddPlayerMessage(ArrayList<Player> players) {
    super(Command.ADD_PLAYER);
    for(Player p : players) {
      this.players.add(new JoiningPlayer(p));
    }
  }
  
  public class JoiningPlayer implements Serializable {
    
    private static final long serialVersionUID = -8374083025011157818L;
    public final UUID playerId;
    public final String username;
    public final Boolean isCpu;
    public final Integer textureId;
    
    public JoiningPlayer(Player player) {
      this.playerId = player.getId();
      this.username = player.getUsername();
      this.isCpu = player.isCpu();
      this.textureId = player.getTextureID();
    }
  }
}
