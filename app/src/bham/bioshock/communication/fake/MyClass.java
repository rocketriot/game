package bham.bioshock.communication.fake;

import bham.bioshock.communication.client.ClientService;

public class MyClass {

  
  private ClientService service;
  
  public MyClass(ClientService service) {
    this.service = service;
  }
  
  
  public void run() {
    service.close();
  }
}
