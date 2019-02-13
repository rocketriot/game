package bham.bioshock.client.controllers;

import com.google.inject.Inject;
import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.common.models.Store;

public class HowToController extends Controller {
  
  @Inject
  public HowToController(Store store, Router router, BoardGame game) {
    super(store, router, game);
  }
}
