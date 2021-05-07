package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Trigger;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class SummonController {

    private static Logger logger;

    static {
        logger = LogManager.getLogger(SummonController.class);
    }


    private static HashSet<String> specialCases;
    private GameCard card;
    private int gameControllerID;

    static {
        specialCases = new HashSet<>();
        specialCases.add("Beast King Barbaros");
        specialCases.add("Gate Guardian");
    }

    public SummonController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        card = getSelectionController().getCard();
    }

    public boolean normalSummon() {
        if (specialCases.contains(getSelectionController().getCard().getCard().getName()))
            return specialSummon(getSelectionController().getCard());
        int availableMonsters = getCurrentPlayerField().getAvailableMonstersInZoneCount();
        if (((Monster) card.getCard()).getLevel() == 5 || ((Monster) card.getCard()).getLevel() == 6) {
            if (availableMonsters < 1) {
                logger.info("in game with id {}: can't summon | not enough cards to tribute", gameControllerID);
                //there are not enough cards to tribute
                return false;
            }
            if (!tribute(1))
                return false;
        } else if (((Monster) card.getCard()).getLevel() >= 7) {
            if (availableMonsters < 2) {
                logger.info("in game with id {}: can't summon | not enough cards to tribute", gameControllerID);
                //there are not enough cards to tribute
                return false;
            }
            if (!tribute(2))
                return false;
        }
        card.setRevealed(true);
        card.setFaceDown(false);
        getCurrentPlayerField().removeFromHand(card);
        getCurrentPlayerField().putInMonsterZone(card);
        logger.info("in game with id {}: summon successful", gameControllerID);
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
        getGameController().applyEffect(Trigger.AFTER_FLIP_SUMMON);
        getGameTurnController().setChangedPositionMonster(card);
        return true;
    }

    public boolean tribute(int amount) {
        GameCard tributeMonster = getGameController().getCurrentPlayerController().tributeMonster(amount);
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
