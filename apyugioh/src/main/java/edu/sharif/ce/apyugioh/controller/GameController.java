package edu.sharif.ce.apyugioh.controller;

import java.util.*;

public class GameController {
    private static List<GameController> gameControllers;
    private int id;
    private SelectionController selectionController;
    private GameTurnController gameTurnController;
    private CheatController cheatController;

    //initialize block
    static {
        gameControllers = new ArrayList<>();
    }
}
