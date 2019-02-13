package bham.bioshock.client.controllers;

import bham.bioshock.client.BoardGame;
import bham.bioshock.client.Router;
import bham.bioshock.client.screens.MinigameScreen;
import bham.bioshock.common.models.Store;

import bham.bioshock.minigame.World;
import com.google.inject.Inject;

public class MinigameController extends Controller {

    @Inject
    public MinigameController(Store store, Router router, BoardGame game) {
        super(store, router, game);
    }

    public void show(){

        World world = new World();
        setScreen(new MinigameScreen(game, world));

        System.out.println("Showing minigame");
    }
}
