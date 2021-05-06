package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.card.*;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.tool.ast.GrammarASTErrorNode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameTurnController {
    private Phase phase;
    private int gameControllerID;
    private boolean isFirstTurn;
    private GameCard SetOrSummonedMonster;
    private List<GameCard> disposableUsedCards;
    private List<GameCard> attackedMonsters;
    private List<GameCard> chain;

    public GameTurnController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        attackedMonsters = new ArrayList<>();
        chain = new ArrayList<>();
        disposableUsedCards = new ArrayList<>();
    }

    public void drawPhase() {
        phase = Phase.DRAW;
    }

    public void standByPhase() {
        phase = Phase.STANDBY;
    }

    public void firstMainPhase() {
        phase = Phase.MAIN1;
    }

    public void battlePhase() {
        phase = Phase.BATTLE;
    }

    public void secondMainPhase() {
        phase = Phase.MAIN2;
    }

    public void endPhase() {
        phase = Phase.END;
    }

    public void set() {
        if (getGameController().isCardSelected()) {
            //no card is selected yet
        } else if (!getCurrentPlayerField().
                isInHand(getSelectionController().getCard())) {
            //you can't set this card
        } else if (!(phase.equals(Phase.MAIN1) || phase.equals(Phase.MAIN2))) {
            //you can't do this action in this phase
        } else {
            new SetController(gameControllerID).set();
        }
    }

    public void summon(){
        if (getCurrentPlayerField().isMonsterZoneFull() &&
                ((Monster)getSelectionController().getCard().getCard()).getLevel()<=4){
            //monster card zone is full
        } else if (SetOrSummonedMonster != null){
            //you already summoned/set on this turn
        } else{
            new SummonController(gameControllerID).normalSummon();
        }
    }

    public void flipSummon(){

    }

    public void changePosition(boolean isChangeToAttack) {
        if (getGameController().isCardSelected()) {
            //no card is selected yet
        } else if (!getCurrentPlayerField()
                .isInMonsterZone(getSelectionController().getCard())) {
            //you can't change this card position
        } else if (!(phase.equals(Phase.MAIN1) || phase.equals(Phase.MAIN2))) {
            //you can't do this action in this phase
        } else {
            if ((isChangeToAttack && getSelectionController().getCard().isFaceDown())) {

            }
        }
    }

    public boolean hasMonsterAttacked(GameCard monster) {
        return true;
    }

    public void makeChain() {

    }

    private Field getCurrentPlayerField() {
        return getGameController().getCurrentPlayer().getField();
    }

    private SelectionController getSelectionController() {
        return getGameController().getSelectionController();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }
}
