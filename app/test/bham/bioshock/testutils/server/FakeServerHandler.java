package bham.bioshock.testutils.server;

import java.util.LinkedList;
import java.util.UUID;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;

public class FakeServerHandler implements MultipleConnectionsHandler {

  public LinkedList<ServerService> connecting = new LinkedList<>();
  public LinkedList<ServerService> unregistered = new LinkedList<>();
  public LinkedList<Message> messages = new LinkedList<>();
  
  
  
  public FakeServerHandler() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public void add(ServerService service) {
    connecting.add(service);
  }

  @Override
  public void unregister(ServerService serverService) {
    unregistered.add(serverService);
  }

  @Override
  public void sendToAll(Message message) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void sendToAllExcept(Message message, UUID id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void sendTo(UUID clientId, Message message) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void abort() {
    for (ServerService s : connecting) {
      s.abort();
    }
  }

  @Override
  public void handleRequest(Message message, ServerService service) {
    messages.add(message);
  }

  @Override
  public void register(UUID id, String name, ServerService service) {
    // TODO Auto-generated method stub
    
  }

}
