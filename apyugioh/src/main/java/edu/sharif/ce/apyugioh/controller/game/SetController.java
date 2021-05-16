package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.Effects;
import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.card.*;
import edu.sharif.ce.apyugioh.view.GameView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        if (getCurrentPlayerField().isMonsterZoneFull()) {
            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
            return false;
        } else if (getGameTurnController().hasAnyMonsterSetOrSummon()) {
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
        if (card.getCard().getCardEffects().contains(Effects.ACTIVE_AFTER_SET)) {
            getGameController().getCurrentPlayerEffectControllers().add(new EffectController(gameControllerID, card));
        }
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
            getGameController().removeMonsterCard(tributeMonster);
        }
        return true;
    }

    private boolean spellTrapSet() {
        if (getCurrentPlayerField().isSpellZoneFull()) {
            Utils.printError("Spell/Trap card zone is full");
            return false;
        }
        if (card.getCard().getCardType().equals(CardType.SPELL) && ((Spell)card.getCard()).getProperty().equals(SpellProperty.FIELD)){
            if ( getGameController().getPlayerByCard(card).getField().getFieldZone() != null) {
                GameCard lastFieldZone = getGameController().getPlayerByCard(card).getField().getFieldZone();
                getCurrentPlayerField().removeFromFieldZone(getCurrentPlayerField().getFieldZone());
                getCurrentPlayerField().putInGraveyard(lastFieldZone);
            }
            getCurrentPlayerField().removeFromHand(card);
            getCurrentPlayerField().putInFieldZone(card);
            GameController.getView().showSuccess(GameView.SUCCESS_SET_SUCCESSFUL);
            return true;
        }
        getCurrentPlayerField().removeFromHand(card);
        getCurrentPlayerField().putInSpellZone(card);
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
