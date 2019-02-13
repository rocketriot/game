package bham.bioshock.client.controllers;

import bham.bioshock.client.Router;
import bham.bioshock.common.models.Store;

/** Root controller used by all other controllers */
public abstract class Controller {

  protected Store store;
  protected Router router;

  public Controller(Store store, Router router) {
    this.store = store;
    this.router = router;
  }

}
