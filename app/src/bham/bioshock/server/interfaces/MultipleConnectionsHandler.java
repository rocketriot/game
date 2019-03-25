package bham.bioshock.server.interfaces;

import java.util.UUID;
import bham.bioshock.communication.interfaces.ServerService;
import bham.bioshock.communication.messages.Message;
import bham.bioshock.server.InvalidMessageSequence;

public interface MultipleConnectionsHandler {
  
  /**
   * Add new service
   * 
   * @param service
   */
  public void add(ServerService service);
    
  /**
   * Registers new client as connected with provided ID and name
   * 
   * @param id
   * @param service
   */
  public void register(UUID id, String name, ServerService service);
  
  /**
   * Unregister client, remove the connection and send information to all clients
   * @param serverService
   */
  public void unregister(ServerService serverService);
  
  /**
   * Sends the message to all clients
   * 
   * @param clientId
   * @param action
   */
  public void sendToAll(Message message);
  
  /**
   * Sends the message to all clients except the one specified
   * 
   * @param clientId
   * @param action
   */
  public void sendToAllExcept(Message message, UUID id);
  
  /**
   * Sends the message to the specific client
   * 
   * @param clientId
   * @param action
   */
  public void sendTo(UUID clientId, Message message);
  
  /**
   * Stop all running subservices
   */
  public void abort();
  
  /**
   * Handle received message from one of the clients
   * 
   * @param action
   * @param service
   * @throws InvalidMessageSequence 
   */
  public void handleRequest(Message message, ServerService service) throws InvalidMessageSequence;
}
