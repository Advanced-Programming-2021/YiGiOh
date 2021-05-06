package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.*;
import javafx.print.PageLayout;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameTurnController {

    private static Logger logger;

    static {
        logger = LogManager.getLogger(GameTurnController.class);
    }

    private Phase phase;
    private int gameControllerID;
    private boolean isFirstTurn;
    private boolean isMonsterSetOrSummon;
    private List<GameCard> disposableUsedCards;
    private List<GameCard> attackedMonsters;
    private List<GameCard> chain;

    public GameTurnController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        attackedMonsters = new ArrayList<>();
        chain = new ArrayList<>();
        disposableUsedCards = new ArrayList<>();
    }

    public void nextPhase() {
        switch (phase) {
            case DRAW:
                standByPhase();
                break;
            case STANDBY:
                firstMainPhase();
                break;
            case MAIN1:
                battlePhase();
                break;
            case BATTLE:
                secondMainPhase();
                break;
            case MAIN2:
                endPhase();
                break;
            case END:
                drawPhase();
                break;
        }
        logger.info("in game with id {}: it's {} phase", gameControllerID,
                Utils.firstUpperOnly(phase.name().replaceAll("(\\d)", " $1")));
        GameController.getView().showPhase(phase);
    }

    public void drawPhase() {
        logger.info("in game with id {}: {} drew {} from deck", gameControllerID, getCurrentPlayer().getUser()
                .getNickname(), getCurrentPlayerField().drawCard().getCard().getName());
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

    public void summon() {
        if (getSelectionController() == null) {
            //no card is selected yet
        } else if (!getCurrentPlayerField().isInField(getSelectionController().getCard())
                || !getSelectionController().getCard().getCard().getCardType().equals(CardType.MONSTER)) {
            //you can’t summon this card
        } else if (!phase.equals(Phase.MAIN1) && !phase.equals(Phase.MAIN2)) {
            //action not allowed in this phase
        } else if (getCurrentPlayerField().isMonsterZoneFull()) {
            //monster card zone is full
        } else if (isMonsterSetOrSummon) {
            //you already summoned/set on this turn
        } else {
            new SummonController(gameControllerID).normalSummon();
        }
    }

    public void flipSummon() {

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
        return getCurrentPlayer().getField();
    }

    private Field getRivalPlayerField() {
        return getRivalPlayer().getField();
    }

    private Player getCurrentPlayer() {
        return getGameController().getCurrentPlayer();
    }

    private Player getRivalPlayer() {
        return getGameController().getRivalPlayer();
    }

    private SelectionController getSelectionController() {
        return getGameController().getSelectionController();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }
}
