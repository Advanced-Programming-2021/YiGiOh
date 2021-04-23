package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.card.GameCard;

import java.util.ArrayList;
import java.util.List;

public class GameTurnController {
    private Phase phase;
    private int gameControllerID;
    private boolean isFirstTurn;
    private List<GameCard> attackedMonsters;
    private List<GameCard> chain;

    public GameTurnController() {
        attackedMonsters = new ArrayList<>();
        chain = new ArrayList<>();
    }

    public void drawPhase() {

    }

    public void standByPhase() {

    }

    public void firstMainPhase() {

    }

    public void battlePhase() {

    }

    public void secondMainPhase() {

    }

    public void endPhase() {

    }

    public Phase getPhase() {
        return null;
    }

    public boolean hasMonsterAttacked(GameCard monster) {
        return true;
    }

    public void makeChain() {

    }
}
