package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Trigger;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
import edu.sharif.ce.apyugioh.view.GameView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class SummonController {

    private static Logger logger;
    private static HashSet<String> specialCases;

    static {
        logger = LogManager.getLogger(SummonController.class);
        specialCases = new HashSet<>();
        specialCases.add("Beast King Barbaros");
        specialCases.add("Gate Guardian");
        specialCases.add("The Tricky");
    }

    private GameCard card;
    private int gameControllerID;

    public SummonController(int gameControllerID,GameCard card) {
        this.gameControllerID = gameControllerID;
        this.card = card;
    }

    public boolean normalSummon() {
        if (specialCases.contains(card.getCard().getName()))
            return specialSummon();
        int availableMonsters = getCurrentPlayerField().getAvailableMonstersInZoneCount();
        if (((Monster) card.getCard()).getLevel() == 5 || ((Monster) card.getCard()).getLevel() == 6) {
            if (availableMonsters < 1) {
                logger.info("in game with id {}: can't summon | not enough cards to tribute", gameControllerID);
                GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                return false;
            }
            if (!tribute(1))
                return false;
        } else if (((Monster) card.getCard()).getLevel() >= 7) {
            if (availableMonsters < 2) {
                logger.info("in game with id {}: can't summon | not enough cards to tribute", gameControllerID);
                GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                return false;
            }
            if (!tribute(2))
                return false;
        }
        card.setRevealed(true);
        card.setFaceDown(false);
        getCurrentPlayerField().removeFromHand(card);
        getCurrentPlayerField().putInMonsterZone(card);
        getGameController().getCurrentPlayerEffectControllers().add(new EffectController(gameControllerID,card));
        logger.info("in game with id {}: summon successful", gameControllerID);
        return true;
    }

    public boolean specialSummon(){
        return true;
    }

    public boolean ritualSummon() {
        return true;
    }

    public boolean flipSummon() {
        card.setRevealed(true);
        card.setFaceDown(false);
        getGameTurnController().setChangedPositionMonster(card);
        getGameController().getCurrentPlayerEffectControllers().add(new EffectController(gameControllerID,card));
        return true;
    }

    public boolean tribute(int amount) {
        GameCard[] tributeMonsters = getGameController().getCurrentPlayerController().tributeMonster(amount);
        if (tributeMonsters == null) return false;
        for (GameCard tributeMonster : tributeMonsters) {
            if (tributeMonster == null)
                return false;
        }
        for (GameCard tributeMonster : tributeMonsters) {
            getGameController().removeCard(tributeMonster);
        }
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
