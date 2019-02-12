package bham.bioshock.client.controllers;

import bham.bioshock.client.Router;
import bham.bioshock.common.models.Store;
import com.google.inject.Inject;

public class LoadingController extends Controller {

  
  @Inject
  public LoadingController(Store store, Router router) {
    super(store, router);
  }
  
  public void show() {
    
  }
  
}
