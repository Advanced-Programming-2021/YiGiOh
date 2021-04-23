package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.RoundResult;
import edu.sharif.ce.apyugioh.model.card.CardLocation;

import java.util.*;

public class GameController {
    private static List<GameController> gameControllers;

    private int id;
    private SelectionController selectionController;
    private GameTurnController gameTurnController;
    private CheatController cheatController;
    private Player firstPlayer;
    private Player secondPlayer;
    private boolean isFirstPlayerTurn;
    private int numberOfRounds;
    private List<RoundResult> roundResults;
    private List<EffectController> effectControllers;

    //initialize block
    static {
        gameControllers = new ArrayList<>();
    }

    public GameController() {
        roundResults = new ArrayList<>();
        effectControllers = new ArrayList<>();
    }

    public static GameController getGameControllerById(int id) {
        return null;
    }

    public void play() {

    }

    public void select(CardLocation location) {

    }

    public void set() {

    }

    public void setPosition(boolean isAttack) {

    }

    public void summon() {

    }

    public void nextPhase() {

    }

    public void endRound(boolean isFirstPlayerWin) {

    }

    public void startRound() {

    }

    public void attack(int position) {

    }

    public void directAttack() {

    }

    public void activeEffect() {

    }

    public void flipSummon() {

    }

    public void surrender() {

    }

    public void cancel() {

    }

    public void exchangeSideDeckCards() {

    }

    public Player getCurrentPlayer() {
        return null;
    }

    public Player getRivalPlayer() {
        return null;
    }
}
