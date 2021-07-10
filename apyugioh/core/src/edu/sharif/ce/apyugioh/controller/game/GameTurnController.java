package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.player.ConfirmationAction;
import edu.sharif.ce.apyugioh.controller.player.SelectionAction;
import javafx.scene.effect.Effect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.EffectResponse;
import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.Trigger;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
import edu.sharif.ce.apyugioh.model.card.MonsterSummon;
import edu.sharif.ce.apyugioh.view.GameView;
import lombok.Getter;
import lombok.Setter;

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
    private GameCard summonedMonster;
    private GameCard setMonster;
    private GameCard changedPositionMonster;
    private List<EffectController> disposableUsedEffects;
    private List<GameCard> flipSummonedMonsters;
    private List<GameCard> attackedMonsters;
    private List<GameCard> chain;

    public GameTurnController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
        attackedMonsters = new ArrayList<>();
        chain = new ArrayList<>();
        disposableUsedEffects = new ArrayList<>();
        flipSummonedMonsters = new ArrayList<>();
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
                getGameController().setFirstPlayerTurn(!getGameController().isFirstPlayerTurn());
                getGameController().startRound();
                break;
        }
        if (!phase.equals(Phase.END) || getGameController().getGameTurnController().equals(this)) {
            logger.info("in game with id {}: it's {} phase", gameControllerID,
                    Utils.firstUpperOnly(phase.name().replaceAll("(\\d)", " $1")));
            if (!getGameController().getCurrentPlayerController().isAI()) {
                GameController.getView().showPhase(phase);
            }
        }
    }

    public void drawPhase() {
        getGameController().applyEffect(Trigger.DRAW, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                if (result == null) return null;
                EffectResponse response = result.peek();
                if (response != null && response.equals(EffectResponse.CANT_DRAW)) {
                    phase = Phase.DRAW;
                }
                return null;
            }
        });
        GameCard drawnCard = getCurrentPlayerField().drawCard();
        if (drawnCard == null)
            getGameController().endRound(!getGameController().isFirstPlayerTurn());
        logger.info("in game with id {}: {} drew {} from deck", gameControllerID, getCurrentPlayer().getUser()
                .getNickname(), drawnCard.getCard().getName());
        if (getCurrentPlayerField().getHand().size() > 6) {
            getGameController().getCurrentPlayerController().selectForceCardFromHand(new SelectionAction() {
                @Override
                public GameCard call() throws Exception {
                    if (choice == null) return null;
                    GameCard toBeRemoved = choice.peek();
                    if (toBeRemoved == null) return null;
                    toBeRemoved = getCurrentPlayerField().getHand().get(getRivalPlayerField().getAvailableMonstersInZoneCount());
                    getCurrentPlayerField().removeFromHand(toBeRemoved);
                    getCurrentPlayerField().putInGraveyard(toBeRemoved);
                    logger.info("in game with id {}: {} removed {} from hand", gameControllerID, getCurrentPlayer().getUser()
                            .getNickname(), toBeRemoved.getCard().getName());
                    return null;
                }
            });
        }
        phase = Phase.DRAW;
    }

    public void standByPhase() {
        phase = Phase.STANDBY;
        getGameController().applyEffect(Trigger.STANDBY, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                return null;
            }
        });
    }

    public void firstMainPhase() {
        phase = Phase.MAIN1;
        getGameController().applyEffect(Trigger.MAIN, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                return null;
            }
        });
    }

    public void battlePhase() {
        phase = Phase.BATTLE;
    }

    public void secondMainPhase() {
        phase = Phase.MAIN2;
        getGameController().applyEffect(Trigger.MAIN, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                return null;
            }
        });
    }

    public void endPhase() {
        phase = Phase.END;
        getGameController().resetEffect();
    }

    public void set() {
        if (!checkBeforeSet())
            return;
        if (new SetController(gameControllerID).set()
                && getSelectionController().getCard().getCard().getCardType().equals(CardType.MONSTER))
            setSetMonster(getSelectionController().getCard());
    }

    private boolean checkBeforeSet() {
        if (getSelectionController() == null) {
            GameController.getView().showError(GameView.ERROR_CARD_NOT_SELECTED);
            return false;
        }
        if (!getSelectionController().getLocation().isInHand()) {
            GameController.getView().showError(GameView.ERROR_SELECTION_NOT_IN_HAND, "set");
            return false;
        }
        if (!getPhase().equals(Phase.MAIN1) && !getPhase().equals(Phase.MAIN2)) {
            GameController.getView().showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
            return false;
        }
        return true;
    }

    public void summon() {
        checkBeforeSummon(new ConfirmationAction() {
            @Override
            public Boolean call() throws Exception {
                if (choice == null) return false;
                Boolean result = choice.peek();
                if (result == null) return false;
                if (!result) return false;
                System.out.println("Check before summon " + Thread.currentThread().getName());
                SummonController summonController = new SummonController(gameControllerID, getSelectionController().getCard());
                System.out.println("Check before summon after new " + Thread.currentThread().getName());
                summonController.normalSummon();
                if (getGameController().getPlayerByCard(getSelectionController().getCard())
                        .getField().isInMonsterZone(getSelectionController().getCard()))
                    setSummonedMonster(getSelectionController().getCard());
                return false;
            }
        });
    }

//    public void summon() {
//        if (getSelectionController() == null) {
//            GameController.getView().showError(GameView.ERROR_CARD_NOT_SELECTED);
//            return;
//        }
//        if (!getSelectionController().getLocation().isInHand() ||
//                !getSelectionController().getCard().getCard().getCardType().equals(CardType.MONSTER) ||
//                ((Monster) getSelectionController().getCard().getCard()).getSummon().equals(MonsterSummon.RITUAL)) {
//            GameController.getView().showError(GameView.ERROR_SELECTION_NOT_IN_HAND, "summon");
//            return;
//        }
//        if (!(getPhase().equals(Phase.MAIN1) || getPhase().equals(Phase.MAIN2))) {
//            GameController.getView().showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
//            return;
//        }
//        if (getCurrentPlayerField().isMonsterZoneFull() &&
//                ((Monster) getSelectionController().getCard().getCard()).getLevel() <= 4) {
//            logger.info("in game with id {}: can't summon | monster zone full", gameControllerID);
//            System.out.println(getCurrentPlayerField().isMonsterZoneFull());
//            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
//            return;
//        } else if (hasAnyMonsterSetOrSummon()) {
//            logger.info("in game with id {}: can't summon | already summoned in this round", gameControllerID);
//            GameController.getView().showError(GameView.ERROR_ALREADY_SET_OR_SUMMONED_CARD);
//            return;
//        }
//        getGameController().applyEffect(Trigger.BEFORE_SUMMON, new EffectAction() {
//            @Override
//            public EffectResponse call() throws Exception {
//                if (result == null) return null;
//                EffectResponse effectResponse = result.peek();
//                if (effectResponse == null) return null;
//                if (effectResponse.equals(EffectResponse.SUMMON_CANT_BE_DONE)) {
//                    GameController.getView().showError(GameView.ERROR_CANT_BE_SUMMONED);
//                } else {
//                    SummonController summonController = new SummonController(gameControllerID, getSelectionController().getCard());
//                    System.out.println("Check before summon after new " + Thread.currentThread().getName());
//                    summonController.normalSummon();
//                    setSummonedMonster(getSelectionController().getCard());
//                }
//                System.out.println("Effect Action...");
//                return null;
//            }
//        });
//    }

    private void checkBeforeSummon(ConfirmationAction action) {
        ArrayBlockingQueue<Boolean> choice = new ArrayBlockingQueue<>(1);
        if (getSelectionController() == null) {
            GameController.getView().showError(GameView.ERROR_CARD_NOT_SELECTED);
            choice.add(Boolean.FALSE);
            return;
        }
        if (!getSelectionController().getLocation().isInHand() ||
                !getSelectionController().getCard().getCard().getCardType().equals(CardType.MONSTER) ||
                ((Monster) getSelectionController().getCard().getCard()).getSummon().equals(MonsterSummon.RITUAL)) {
            GameController.getView().showError(GameView.ERROR_SELECTION_NOT_IN_HAND, "summon");
            choice.add(Boolean.FALSE);
            return;
        }
        if (!(getPhase().equals(Phase.MAIN1) || getPhase().equals(Phase.MAIN2))) {
            GameController.getView().showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
            choice.add(Boolean.FALSE);
            return;
        }
        if (getCurrentPlayerField().isMonsterZoneFull() &&
                ((Monster) getSelectionController().getCard().getCard()).getLevel() <= 4) {
            logger.info("in game with id {}: can't summon | monster zone full", gameControllerID);
            System.out.println(getCurrentPlayerField().isMonsterZoneFull());
            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
            choice.add(Boolean.FALSE);
            return;
        } else if (hasAnyMonsterSetOrSummon()) {
            logger.info("in game with id {}: can't summon | already summoned in this round", gameControllerID);
            GameController.getView().showError(GameView.ERROR_ALREADY_SET_OR_SUMMONED_CARD);
            choice.add(Boolean.FALSE);
            return;
        }
        getGameController().applyEffect(Trigger.BEFORE_SUMMON, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                if (result == null) return null;
                EffectResponse effectResponse = result.peek();
                if (effectResponse == null) return null;
                if (effectResponse.equals(EffectResponse.SUMMON_CANT_BE_DONE)) {
                    GameController.getView().showError(GameView.ERROR_CANT_BE_SUMMONED);
                    choice.add(Boolean.FALSE);
                } else {
                    choice.add(Boolean.TRUE);
                }
                System.out.println("Effect Action...");
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (choice.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(choice);
        getGameController().getExecutor().submit(action);
    }

    public void changePosition(boolean isChangeToAttack) {
        if (checkBeforeChangePosition(isChangeToAttack)) {
            getSelectionController().getCard().setRevealed(true);
            getSelectionController().getCard().setFaceDown(!isChangeToAttack);
            setChangedPositionMonster(getSelectionController().getCard());
            GameController.getView().showSuccess(GameView.SUCCESS_CHANGE_POSITION_SUCCESSFUL);
        }
    }

    private boolean checkBeforeChangePosition(boolean isChangeToAttack) {
        if (getSelectionController() == null) {
            GameController.getView().showError(GameView.ERROR_CARD_NOT_SELECTED);
            return false;
        }
        if (!getGameController().getCurrentPlayer().getField().isInMonsterZone(getSelectionController().getCard())) {
            GameController.getView().showError(GameView.ERROR_CANT_CHANGE_CARD_POSITION);
            return false;
        }
        if (!getGameController().getGameTurnController().getPhase().equals(Phase.MAIN1) &&
                !getGameController().getGameTurnController().getPhase().equals(Phase.MAIN2)) {
            GameController.getView().showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
            return false;
        }
        if (isChangeToAttack == (!getSelectionController().getCard().isFaceDown())) {
            GameController.getView().showError(GameView.ERROR_ALREADY_IN_WANTED_POSITION);
            return false;
        }
        if (getSelectionController().getCard().equals(changedPositionMonster)) {
            GameController.getView().showError(GameView.ERROR_ALREADY_CHANGED_POSITION_IN_TURN);
            return false;
        }
        return true;
    }

    public void flipSummon() {
        if (checkBeforeFlipSummon()) {
            new SummonController(gameControllerID, getSelectionController().getCard()).flipSummon();
            flipSummonedMonsters.add(getGameController().getSelectionController().getCard());
        }
    }

    private boolean checkBeforeFlipSummon() {
        if (getSelectionController() == null) {
            GameController.getView().showError(GameView.ERROR_CARD_NOT_SELECTED);
            return false;
        }
        if (!getGameController().getCurrentPlayer().getField().isInMonsterZone(getSelectionController().getCard())) {
            GameController.getView().showError(GameView.ERROR_CANT_CHANGE_CARD_POSITION);
            return false;
        }
        if (!(getPhase().equals(Phase.MAIN1) || getPhase().equals(Phase.MAIN2))) {
            GameController.getView().showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
            return false;
        }
        if (!getSelectionController().getCard().isFaceDown() || (hasMonsterFlipped(getSelectionController().getCard()))) {
            GameController.getView().showError(GameView.ERROR_SELECTION_NOT_IN_HAND, "flip summon");
            return false;
        }
        return true;
    }

    public void attack(int position) {
        if (getSelectionController().getCard().isFaceDown()) {
            GameController.getView().showError(GameView.ERROR_CANT_ATTACK_WITH_CARD);
            return;
        }
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
        getGameController().setAttackController(new AttackController(gameControllerID, position));
        getGameController().applyEffect(Trigger.BEFORE_ATTACK, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                if (result == null) return null;
                EffectResponse effectResponse = result.peek();
                if (effectResponse == null) return null;
                if (effectResponse.equals(EffectResponse.ATTACK_CANT_BE_DONE)) {
                    GameController.getView().showError(GameView.ERROR_CANT_ATTACK_WITH_CARD);
                }
                return null;
            }
        });
        if (!getGameController().getGameTurnController().getPhase().equals(Phase.BATTLE)) {
            GameController.getView().showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
            return;
        }
        getGameController().getAttackController().attack();
        attackedMonsters.add(getSelectionController().getCard());
    }

    public void directAttack() {
        if (isDirectAttackImpossible()) return;
        EffectResponse response;
        if (getRivalPlayerField().getFirstFreeMonsterZoneIndex() > 0) {
            GameController.getView().showError(GameView.ERROR_CANT_DIRECTLY_ATTACK);
            return;
        }
        getGameController().applyEffect(Trigger.BEFORE_ATTACK, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                if (result == null) return null;
                EffectResponse effectResponse = result.peek();
                if (effectResponse == null) return null;
                if (effectResponse.equals(EffectResponse.ATTACK_CANT_BE_DONE)) {
                    GameController.getView().showError(GameView.ERROR_CANT_DIRECTLY_ATTACK);
                }
                return null;
            }
        });
        getGameController().setAttackController(new AttackController(gameControllerID));
        if (getGameController().getAttackController().directAttack()) {
            GameController.getView().showSuccess(GameView.SUCCESS_DIRECT_ATTACK_SUCCESSFUL,
                    String.valueOf(((Monster) getSelectionController().getCard().getCard()).getAttackPoints()));
        }
        attackedMonsters.add(getSelectionController().getCard());
        getGameController().applyEffect(Trigger.AFTER_ATTACK, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                return null;
            }
        });
    }

    private boolean isDirectAttackImpossible() {
        if (getSelectionController().getCard().isFaceDown()) {
            GameController.getView().showError(GameView.ERROR_CANT_ATTACK_WITH_CARD);
            return true;
        }
        if (getGameController().getPassedTurns() == 1) {
            GameController.getView().showError(GameView.ERROR_CANT_ATTACK_IN_FIRST_TURN);
            return true;
        }
        if (hasMonsterAttacked(getSelectionController().getCard())) {
            GameController.getView().showError(GameView.ERROR_CARD_ALREADY_ATTACKED);
            return true;
        }
        return false;
    }

    public boolean hasMonsterAttacked(GameCard monster) {
        return attackedMonsters.stream().anyMatch(e -> e != null && e.getId() == getSelectionController().getCard().getId());
    }

    public boolean hasAnyMonsterSetOrSummon() {
        return (setMonster != null || summonedMonster != null);
    }

    private boolean hasMonsterFlipped(GameCard monster) {
        if (monster == null)
            return false;
        for (GameCard flippedMonster : flipSummonedMonsters) {
            if (monster.getId() == flippedMonster.getId())
                return true;
        }
        return false;
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
        System.out.println("Get Selection Controller");
        return getGameController().getSelectionController();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }
}
