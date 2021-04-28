package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.RoundResult;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class GameController {
    private static List<GameController> gameControllers;

    //initialize block
    static {
        gameControllers = new ArrayList<>();
    }

    private int id;
    private int numberOfRounds;
    private boolean isFirstPlayerTurn;
    private SelectionController selectionController;
    private GameTurnController gameTurnController;
    private CheatController cheatController;
    private Player firstPlayer;
    private Player secondPlayer;
    private List<RoundResult> roundResults;
    private List<EffectController> effectControllers;

    public GameController(String firstPlayerName, String secondPlayerName) {
        //initialize players ...

        roundResults = new ArrayList<>();
        effectControllers = new ArrayList<>();
    }

    public static GameController getGameControllerById(int id) {
        return gameControllers.stream().filter(e -> e.getId() == id).findAny().orElse(null);
    }

    public void play() {

    }

    public void select(CardLocation location) {
        selectionController = new SelectionController(id, location);
    }

    public void deselect() {
        selectionController = null;
    }

    public void set() {
        gameTurnController.set();
    }

    public void changePosition(boolean isChangeToAttack) {
        gameTurnController.changePosition(isChangeToAttack);
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

    public boolean isCardSelected() {
        return selectionController != null;
    }

    public Player getCurrentPlayer() {
        if (isFirstPlayerTurn) return firstPlayer;
        return secondPlayer;
    }

    public Player getRivalPlayer() {
        if (isFirstPlayerTurn) return secondPlayer;
        return firstPlayer;
    }
}
