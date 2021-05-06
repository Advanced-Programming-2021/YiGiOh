package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;

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
        if (getCurrentPlayerField().isMonsterZoneFull()) {
            //monster card zone is full
            return false;
        } else if (getGameTurnController().getSetOrSummonedMonster() != null) {
            //you already summoned/set on this turn
            return false;
        }
        card.setFaceDown(true);
        getCurrentPlayerField().removeFromHand(card);
        getCurrentPlayerField().putInMonsterZone(card);
        getGameTurnController().setSetOrSummonedMonster(card);
        //set successfully
        return true;
    }

    private boolean spellTrapSet() {
        if (getCurrentPlayerField().isSpellZoneFull()) {
            //spell card zone is full
            return false;
        }
        //set successfully
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
