package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.view.View;
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
        if (location.isInHand())
            card = GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField()
                    .getHand().get(location.getPosition());
        else if (location.isFromMonsterZone())
            card = GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField()
                    .getMonsterZone()[location.getPosition()];
        else if (location.isFromSpellZone())
            card = GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField()
                    .getSpellZone()[location.getPosition()];
            //field zone is not an array?!!!
        else if (location.isFromFieldZone())
            card = GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField()
                    .getFieldZone();
        else if (location.isFromGraveyard())
            card = GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField()
                    .getGraveyard().get(location.getPosition());
        else if (location.isFromEnemy()) {
            if (location.isFromMonsterZone())
                card = GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField()
                        .getMonsterZone()[location.getPosition()];
            else if (location.isFromSpellZone())
                card = GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField()
                        .getSpellZone()[location.getPosition()];
            else if (location.isFromFieldZone())
                card = GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField()
                        .getFieldZone();
        }
        //View Commands ...
    }
}
