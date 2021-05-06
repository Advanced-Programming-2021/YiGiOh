package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;

import java.util.HashSet;

public class SummonController {

    private static HashSet<String> specialCases;
    private GameCard card;
    private int gameControllerID;

    static {
        specialCases = new HashSet<>();
        specialCases.add("Beast King Barbaros");
    }

    public SummonController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        card = getSelectionController().getCard();
        getGameController().setSelectionController(null);
    }

    public boolean normalSummon() {
        if (specialCases.contains(getSelectionController().getCard().getCard().getName()))
            return specialSummon(getSelectionController().getCard());
        int availableMonsters = getCurrentPlayerField().getAvailableMonstersInZoneCount();
        if (((Monster) card.getCard()).getLevel() == 5 || ((Monster) card.getCard()).getLevel() == 6) {
            if (availableMonsters < 1) {
                //there are not enough cards to tribute
                return false;
            }
            if (!tribute())
                return false;
        } else if (((Monster) card.getCard()).getLevel() >= 7) {
            if (availableMonsters < 2) {
                //there are not enough cards to tribute
                return false;
            }
            if (!tribute())
                return false;
            if (!tribute())
                return false;
        }
        card.setRevealed(true);
        card.setFaceDown(false);
        getCurrentPlayerField().removeFromHand(card);
        getCurrentPlayerField().putInMonsterZone(card);
        getGameController().activeEffect();
        //show error summoned successfully
        return true;
    }

    public boolean tributeSummon() {
        return true;
    }

    public boolean specialSummon(GameCard gameCard) {
        return true;
    }

    public boolean ritualSummon() {
        return true;
    }

    public boolean flipSummon() {
        card.setRevealed(true);
        card.setFaceDown(false);

        return true;
    }

    public boolean tribute() {
        GameCard tributeMonster = getGameController().getCurrentPlayerController().tributeMonster();
        if (tributeMonster == null)
            return false;
        getGameController().getCurrentPlayerController().removeCard(tributeMonster);
        return true;
    }

    private Field getCurrentPlayerField() {
        return getGameController().getCurrentPlayer().getField();
    }

    private SelectionController getSelectionController() {
        return getGameController().getSelectionController();
    }

    private GameTurnController getGameTurnController() {
        return getGameController().getGameTurnController();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }

}
