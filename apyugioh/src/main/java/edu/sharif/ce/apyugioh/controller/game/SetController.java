package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.view.GameView;

public class SetController {
    private GameCard card;
    private int gameControllerID;

    public SetController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        card = getSelectionController().getCard();
        getGameController().setSelectionController(null);
    }

    public void specialSet(GameCard monsterToSet) {
        if (!monsterToSet.getCard().getCardType().equals(CardType.MONSTER)) {
            //this card is not a monster
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
        card.setFaceDown(true);
        getCurrentPlayerField().removeFromHand(card);
        getCurrentPlayerField().putInMonsterZone(card);
        getGameTurnController().setSetOrSummonedMonster(card);
        GameController.getView().showSuccess(GameView.SUCCESS_SET_SUCCESSFUL);
        return true;
    }

    private boolean spellTrapSet() {
        if (getCurrentPlayerField().isSpellZoneFull()) {
            //spell card zone is full
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
