package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;

public class SetController {
    private GameCard card;
    private int gameControllerID;

    public SetController(int gameControllerID, GameCard card) {
        this.gameControllerID = gameControllerID;
        this.card = card;
        set();
    }

    public void set() {
        if (card.getCard().getCardType().equals(CardType.MONSTER)) {
            monsterSet();
        } else {
            spellTrapSet();
        }
    }

    private void monsterSet() {
        if (GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().isMonsterZoneFull()) {
            //monster card zone is full
        } else if (GameController.getGameControllerById(gameControllerID).getGameTurnController()
                .isMonsterSetOrSummon()) {
            //you already summoned/set on this turn
        }
        GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().setToMonsterZone(card);
        //set successfully
    }

    private void spellTrapSet() {
        if (GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().isSpellZoneFull()) {
            //spell card zone is full
        }
        //set successfully
    }
}
