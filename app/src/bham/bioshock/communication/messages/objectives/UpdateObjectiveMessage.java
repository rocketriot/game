package bham.bioshock.communication.messages.objectives;

import java.util.UUID;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;
import bham.bioshock.communication.Command;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.minigame.objectives.CaptureTheFlag;
import bham.bioshock.minigame.objectives.Objective;

public class UpdateObjectiveMessage extends Message {

  private static final long serialVersionUID = -1679137637224356213L;

  public final ArrayList<HealthMessage> health = new ArrayList<>();
  public final UUID flagOwner;
  
  public UpdateObjectiveMessage(Objective objective) {
    super(Command.MINIGAME_UPDATE_OBJECTIVE);
    for(Entry<UUID, Integer> e : objective.getHealthCopy()) {
      health.add(new HealthMessage(e.getKey(), e.getValue()));
    }
    
    if(objective instanceof CaptureTheFlag) {
      flagOwner = ((CaptureTheFlag) objective).getFlagOwner();
    } else { 
      flagOwner = null;
    }
  }
  
  public class HealthMessage implements Serializable {
    private static final long serialVersionUID = -2784959835519945776L;
    
    public final UUID id;
    public final Integer value;
    
    public HealthMessage(UUID id, Integer value) {
      this.id = id;
      this.value = value;
    }
  }
}
