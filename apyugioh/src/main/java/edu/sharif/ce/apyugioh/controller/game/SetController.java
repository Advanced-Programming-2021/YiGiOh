package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.card.*;
import edu.sharif.ce.apyugioh.view.GameView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;

public class SetController {

    private static Logger logger;
    static {
        logger = LogManager.getLogger(SummonController.class);
    }

    private GameCard card;
    private int gameControllerID;

    public SetController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        card = getSelectionController().getCard();
    }

    public void specialSet(GameCard monsterToSet) {
        if (!monsterToSet.getCard().getCardType().equals(CardType.MONSTER)) {
            GameController.getView().showError(GameView.ERROR_WRONG_CARD_TYPE, "monster");
        } else {
            monsterSet();
        }
    }

    public boolean set() {
        if (card.getCard().getCardType().equals(CardType.MONSTER)) {
            return monsterSet();
        } else {
            return spellTrapSet();
        }
    }

    private boolean monsterSet() {
        if (((Monster)card.getCard()).getLevel() > 8){
            Utils.printError("you can't normally set this monster");
            return false;
        }
        if (!getGameTurnController().getPhase().equals(Phase.MAIN1) && !getGameTurnController().getPhase().equals(Phase.MAIN2)){
            GameController.getView().showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
            return false;
        }
        if (getCurrentPlayerField().isMonsterZoneFull()) {
            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
            return false;
        } else if (getGameTurnController().getSetOrSummonedMonster() != null) {
            GameController.getView().showError(GameView.ERROR_ALREADY_SET_OR_SUMMONED_CARD);
            return false;
        }
        if (!checkForTribute()){
            GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
            return false;
        }
        card.setFaceDown(true);
        getCurrentPlayerField().removeFromHand(card);
        getCurrentPlayerField().putInMonsterZone(card);
        getGameTurnController().setSetOrSummonedMonster(card);
        GameController.getView().showSuccess(GameView.SUCCESS_SET_SUCCESSFUL);
        return true;
    }

    private boolean checkForTribute(){
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
        return true;
    }

    private boolean tribute(int amount) {
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

    private boolean spellTrapSet() {
        if (getCurrentPlayerField().isSpellZoneFull()) {
            //spell card zone is full
            return false;
        }
        if (card.getCard().getCardType().equals(CardType.SPELL) && ((Spell)card.getCard()).getProperty().equals(SpellProperty.FIELD)
        && getGameController().getPlayerByCard(card).getField().getFieldZone() != null){
            Utils.printError("Field zone is full");
            return false;
        }
        GameController.getView().showSuccess(GameView.SUCCESS_SET_SUCCESSFUL);
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
