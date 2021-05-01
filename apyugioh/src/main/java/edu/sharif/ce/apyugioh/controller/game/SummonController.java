package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
public class SummonController {
    private GameCard card;
    private int gameControllerID;

    public SummonController(int gameControllerID){
        this.gameControllerID = gameControllerID;
        card = GameController.getGameControllerById(gameControllerID).getSelectionController().getCard();
        GameController.getGameControllerById(gameControllerID).setSelectionController(null);
    }

    public boolean normalSummon() {
        int availableMonsters = 0;
        for(int i = 0;i<5;++i){
            if (GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField().getMonsterZone()[i] != null)
                availableMonsters++;
        }
        if (((Monster) card.getCard()).getLevel() == 5 || ((Monster) card.getCard()).getLevel() == 6 ){
            if (availableMonsters < 1){
                //there are not enough cards to tribute
                return false;
            }
            if (!tribute())
                return false;
        } else if (((Monster) card.getCard()).getLevel() >= 7){
            if (availableMonsters < 2){
                //there are not enough cards to tribute
                return false;
            }
            if (!tribute())
                return false;
            if (!tribute())
                return false;
        }
        CardLocation cardLocation = new CardLocation();
        cardLocation.setPosition(GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField().getFirstFreeMonsterZone());
        cardLocation.setFromMonsterZone(true);
        card.setRevealed(true);
        GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField().putCard(card,cardLocation);
        //show error summoned successfully
        return true;
    }

    public boolean tributeSummon() {
        return true;
    }

    public boolean specialSummon() {
        return true;
    }

    public boolean ritualSummon() {
        return true;
    }

    public boolean flipSummon() {
        return true;
    }

    public boolean tribute(){


        return true;
    }

}
