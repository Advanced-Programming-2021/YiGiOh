package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.*;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
import edu.sharif.ce.apyugioh.view.GameView;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
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
    private GameCard setOrSummonedMonster;
    private GameCard changedPositionMonster;
    private List<EffectController> disposableUsedEffects;
    private List<GameCard> attackedMonsters;
    private List<GameCard> chain;

    public GameTurnController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        attackedMonsters = new ArrayList<>();
        chain = new ArrayList<>();
        disposableUsedEffects = new ArrayList<>();
        getGameController().setPassedTurns(getGameController().getPassedTurns() + 1);
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
        if (new SetController(gameControllerID).set())
            setSetOrSummonedMonster(getSelectionController().getCard());
    }

    public void summon() {
        EffectResponse response;
        if (getCurrentPlayerField().isMonsterZoneFull() &&
                ((Monster) getSelectionController().getCard().getCard()).getLevel() <= 4) {
            logger.info("in game with id {}: can't summon | monster zone full", gameControllerID);
            System.out.println(getCurrentPlayerField().isMonsterZoneFull());
            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
        } else if (setOrSummonedMonster != null) {
            logger.info("in game with id {}: can't summon | already summoned in this round", gameControllerID);
            GameController.getView().showError(GameView.ERROR_ALREADY_SET_OR_SUMMONED_CARD);
        } else if ((response = getGameController().applyEffect(Trigger.BEFORE_SUMMON)) != null
                && response.equals(EffectResponse.SUMMON_CANT_BE_DONE)) {
            GameController.getView().showError(GameView.ERROR_CANT_BE_SUMMONED);
        } else {
            if (new SummonController(gameControllerID, getSelectionController().getCard()).normalSummon()) {
                setSetOrSummonedMonster(getSelectionController().getCard());
                getGameController().getCurrentPlayerEffectControllers().add(new EffectController(gameControllerID,
                        getSelectionController().getCard()));
                getGameController().applyEffect(Trigger.AFTER_SUMMON);
                getGameController().applyEffect(Trigger.AFTER_NORMAL_SUMMON);
                GameController.getView().showSuccess(GameView.SUCCESS_SUMMON_SUCCESSFUL);
            }
        }
    }

    public void changePosition(boolean isChangeToAttack) {
        if (isChangeToAttack == (!getSelectionController().getCard().isFaceDown())) {
            GameController.getView().showError(GameView.ERROR_ALREADY_IN_WANTED_POSITION);
            return;
        }
        if (getSelectionController().getCard().equals(changedPositionMonster)) {
            GameController.getView().showError(GameView.ERROR_ALREADY_CHANGED_POSITION_IN_TURN);
            return;
        }
        getSelectionController().getCard().setRevealed(true);
        getSelectionController().getCard().setFaceDown(!isChangeToAttack);
        setChangedPositionMonster(getSelectionController().getCard());
        GameController.getView().showSuccess(GameView.SUCCESS_CHANGE_POSITION_SUCCESSFUL);
    }

    public void flipSummon() {
        if (new SummonController(gameControllerID, getSelectionController().getCard()).flipSummon()){
            getGameController().applyEffect(Trigger.AFTER_SUMMON);
            getGameController().applyEffect(Trigger.AFTER_FLIP_SUMMON);
        }
    }

    public void attack(int position) {
        if (getGameController().getPassedTurns() == 1) {
            GameController.getView().showError(GameView.ERROR_CANT_ATTACK_IN_FIRST_TURN);
            return;
        }
        if (hasMonsterAttacked(getSelectionController().getCard())) {
            GameController.getView().showError(GameView.ERROR_CARD_ALREADY_ATTACKED);
            return;
        }
        if (position < 1 || position > 5 || getGameController().getRivalPlayer().getField().getMonsterZone()[position - 1] == null) {
            GameController.getView().showError(GameView.ERROR_NO_CARD_TO_ATTACK_TO);
            return;
        }
        if (getGameController().applyEffect(Trigger.BEFORE_ATTACK).equals(EffectResponse.ATTACK_CANT_BE_DONE)) {
            GameController.getView().showError(GameView.ERROR_CANT_ATTACK_WITH_CARD);
            return;
        }
        getGameController().setAttackController(new AttackController(gameControllerID, position));
        getGameController().getAttackController().attack();
        attackedMonsters.add(getSelectionController().getCard());
        getGameController().applyEffect(Trigger.AFTER_ATTACK);
    }

    public void directAttack() {
        if (getGameController().getPassedTurns() == 1) {
            GameController.getView().showError(GameView.ERROR_CANT_ATTACK_IN_FIRST_TURN);
            return;
        }
        if (hasMonsterAttacked(getSelectionController().getCard())) {
            GameController.getView().showError(GameView.ERROR_CARD_ALREADY_ATTACKED);
            return;
        }
        if (getRivalPlayerField().getFirstFreeMonsterZoneIndex() > 0 || getGameController().applyEffect(Trigger.BEFORE_ATTACK).equals(EffectResponse.ATTACK_CANT_BE_DONE)) {
            GameController.getView().showError(GameView.ERROR_CANT_DIRECTLY_ATTACK);
            return;
        }
        getGameController().setAttackController(new AttackController(gameControllerID));
        if (getGameController().getAttackController().directAttack()) {
            GameController.getView().showSuccess(GameView.SUCCESS_DIRECT_ATTACK_SUCCESSFUL,
                    String.valueOf(((Monster) getSelectionController().getCard().getCard()).getAttackPoints()));
        }
        attackedMonsters.add(getSelectionController().getCard());
        getGameController().applyEffect(Trigger.AFTER_ATTACK);
    }

    public boolean hasMonsterAttacked(GameCard monster) {
        return attackedMonsters.stream().anyMatch(e -> e != null && e.getId() == getSelectionController().getCard().getId());
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
