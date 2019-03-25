package bham.bioshock.testutils.server;

import java.util.UUID;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.server.interfaces.MultipleConnectionsHandler;

public class FakeServerHandler implements MultipleConnectionsHandler {

  public FakeServerHandler() {
    // TODO Auto-generated constructor stub
  }

  @Override
  public void add(ServerService service) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void unregister(ServerService serverService) {
    // TODO Auto-generated method stub
    
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
    // TODO Auto-generated method stub
    
  }

  @Override
  public void handleRequest(Message message, ServerService service) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void register(UUID id, String name, ServerService service) {
    // TODO Auto-generated method stub
    
  }

}
