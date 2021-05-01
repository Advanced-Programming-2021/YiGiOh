package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameTurnController {
    private Phase phase;
    private int gameControllerID;
    private boolean isFirstTurn;
    private boolean isMonsterSetOrSummon;
    private List<GameCard> attackedMonsters;
    private List<GameCard> chain;

    public GameTurnController() {
        attackedMonsters = new ArrayList<>();
        chain = new ArrayList<>();
    }

    public void drawPhase() {

    }

    public void standByPhase() {

    }

    public void firstMainPhase() {

    }

    public void battlePhase() {

    }

    public void secondMainPhase() {

    }

    public void endPhase() {

    }

    public void set() {
        if (GameController.getGameControllerById(gameControllerID).isCardSelected()) {
            //no card is selected yet
        } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField().
                isInHand(GameController.getGameControllerById(gameControllerID).getSelectionController().getCard())) {
            //you can't set this card
        } else if (!(phase.equals(Phase.MAIN1) || phase.equals(Phase.MAIN2))) {
            //you can't do this action in this phase
        } else {
            new SetController(gameControllerID, GameController.getGameControllerById(gameControllerID)
                    .getSelectionController().getCard());
        }
    }

    public void summon(){

    }

    public void flipSummon(){

    }

    public void changePosition(boolean isChangeToAttack) {
        if (GameController.getGameControllerById(gameControllerID).isCardSelected()) {
            //no card is selected yet
        } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField()
                .isFromMonsterZone(GameController.getGameControllerById(gameControllerID).getSelectionController().getCard())) {
            //you can't change this card position
        } else if (!(phase.equals(Phase.MAIN1) || phase.equals(Phase.MAIN2))) {
            //you can't do this action in this phase
        } else {
            if ((isChangeToAttack && GameController.getGameControllerById(gameControllerID).getSelectionController().getCard().isFaceDown())) {

            }
        }
    }

    public boolean hasMonsterAttacked(GameCard monster) {
        return true;
    }

    public void makeChain() {

    }
}
