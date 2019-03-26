package bham.bioshock.testutils.communication;

import java.util.LinkedList;
import java.util.Optional;
import java.util.UUID;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.communication.messages.Message;

public class FakeServerService implements ServerService {

  boolean running = false;
  Optional<UUID> id = Optional.empty();
  LinkedList<Message> messages = new LinkedList<Message>();
  
  
  @Override
  public void saveId(UUID id, String name) {
    this.id = Optional.of(id);
  }

  @Override
  public Optional<UUID> Id() {
    return id;
  }

  @Override
  public void send(Message message) {
    messages.add(message);
  }

  @Override
  public int getSenderQueueSize() {
    return 0;
  }

  @Override
  public long getSenderCounter() {
    return 0;
  }

  @Override
  public void resetSenderCounter() {
  }

  @Override
  public void abort() {
    running = false;
  }
  
  public boolean isRunning() {
    return running;
  }
  
  public LinkedList<Message> getSentMessages() {
    return messages;
  }

  public void clearMessages() {
    messages.clear();
  }

  @Override
  public void start() {
    running = true;
  }
  
}
