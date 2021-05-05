package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;

public class SetController {
    private GameCard card;
    private int gameControllerID;

    public SetController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        card = getGameController().getSelectionController().getCard();
        getGameController().setSelectionController(null);
    }

    public void specialSet(GameCard monsterToSet) {
        if (!monsterToSet.getCard().getCardType().equals(CardType.MONSTER)) {
            //this card is not a monster
        } else {
            monsterSet();
        }
    }

    public void set() {
        if (card.getCard().getCardType().equals(CardType.MONSTER)) {
            monsterSet();
        } else {
            spellTrapSet();
        }
    }

    private void monsterSet() {
        if (getCurrentPlayerField().isMonsterZoneFull()) {
            //monster card zone is full
        } else if (GameController.getGameControllerById(gameControllerID).getGameTurnController()
                .isMonsterSetOrSummon()) {
            //you already summoned/set on this turn
        }
        card.setFaceDown(true);
        getCurrentPlayerField().removeFromHand(card);
        getCurrentPlayerField().putInMonsterZone(card);
        //set successfully
    }

    private void spellTrapSet() {
        if (getCurrentPlayerField().isSpellZoneFull()) {
            //spell card zone is full
        }
        //set successfully
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }

    private Field getCurrentPlayerField() {
        return getGameController().getCurrentPlayer().getField();
    }
}
