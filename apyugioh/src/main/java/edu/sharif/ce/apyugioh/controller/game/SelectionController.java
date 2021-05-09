package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import lombok.Getter;

public class SelectionController {
    @Getter
    private GameCard card;
    @Getter
    private CardLocation location;
    private int gameControllerID;

    public SelectionController(int gameControllerID, CardLocation location) {
        card = null;
        this.gameControllerID = gameControllerID;
        this.location = location;
        select(location);
    }

    public void select(CardLocation location) {
        if (location.isFromEnemy()) {
            if (location.isFromMonsterZone())
                card = getRivalPlayerField().getMonsterZone()[location.getPosition()];
            else if (location.isFromSpellZone())
                card = getRivalPlayerField().getSpellZone()[location.getPosition()];
            else if (location.isFromFieldZone())
                card = getRivalPlayerField().getFieldZone();
        } else {
            if (location.isInHand())
                card = getCurrentPlayerField().getHand().get(location.getPosition());
            else if (location.isFromMonsterZone())
                card = getCurrentPlayerField().getMonsterZone()[location.getPosition()];
            else if (location.isFromSpellZone())
                card = getCurrentPlayerField().getSpellZone()[location.getPosition()];
                //field zone is not an array?!!!
            else if (location.isFromFieldZone())
                card = getCurrentPlayerField().getFieldZone();
            else if (location.isFromGraveyard())
                card = getCurrentPlayerField().getGraveyard().get(location.getPosition());
        }
        //View Commands ...
    }

    private Field getCurrentPlayerField() {
        return GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField();
    }

    private Field getRivalPlayerField() {
        return GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField();
    }
}
