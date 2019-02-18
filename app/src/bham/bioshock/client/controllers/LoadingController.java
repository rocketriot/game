package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.store.Store;
import com.google.inject.Inject;

public class LoadingController extends Controller {

  
  @Inject
  public LoadingController(Store store, Router router, BoardGame game) {
    super(store, router, game);
   
  }
  
  public void show() {
    
  }
  
}
