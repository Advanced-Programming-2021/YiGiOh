package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.player.AIPlayerController;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.*;
import edu.sharif.ce.apyugioh.model.card.*;
import edu.sharif.ce.apyugioh.view.GameView;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameController {
    private static List<GameController> gameControllers;
    @Getter
    private static GameView view;
    private static Logger logger;

    //initialize block
    static {
        gameControllers = new ArrayList<>();
        view = new GameView();
        logger = LogManager.getLogger(GameController.class);
    }

    private int id;
    private int numberOfRounds;
    private int passedTurns;
    private boolean isFirstPlayerTurn;
    private boolean isTurnTempChanged;
    private AttackController attackController;
    private SelectionController selectionController;
    private GameTurnController gameTurnController;
    private CheatController cheatController;
    private PlayerController firstPlayer;
    private PlayerController secondPlayer;
    private List<RoundResult> roundResults;
    private List<EffectController> firstPlayerEffectControllers;
    private List<EffectController> secondPlayerEffectControllers;

    public GameController(PlayerController firstPlayer, PlayerController secondPlayer, int numberOfRounds) {
        id = LocalDateTime.now().getNano();
        this.firstPlayer = firstPlayer;
        this.firstPlayer.setGameControllerID(id);
        this.secondPlayer = secondPlayer;
        this.secondPlayer.setGameControllerID(id);
        this.numberOfRounds = numberOfRounds;
        isFirstPlayerTurn = true;

        cheatController = new CheatController(id);

        roundResults = new ArrayList<>();
        firstPlayerEffectControllers = new ArrayList<>();
        secondPlayerEffectControllers = new ArrayList<>();

        gameControllers.add(this);
    }

    public static GameController getGameControllerById(int id) {
        return gameControllers.stream().filter(e -> e.getId() == id).findAny().orElse(null);
    }

    public void play() {
        for (int i = 0; i < 3; i++) {
            logger.info("in game with id {}: {} drew {} from deck", id, firstPlayer.getPlayer().getUser()
                    .getNickname(), firstPlayer.getPlayer().getField().drawCard().getCard().getName());
            logger.info("in game with id {}: {} drew {} from deck", id, secondPlayer.getPlayer().getUser()
                    .getNickname(), secondPlayer.getPlayer().getField().drawCard().getCard().getName());
        }
        startRound();
    }

    public void select(CardLocation location) {
        selectionController = new SelectionController(id, location);
        logger.info("in game with id {}: {} selected from {}", id, selectionController.getCard().getCard().getName(),
                location);
    }

    public void deselect() {
        if (selectionController != null) {
            logger.info("in game with id {}: {} deselected from {}", id, selectionController.getCard().getCard().getName(),
                    selectionController.getLocation());
            selectionController = null;
        }
    }

    public void set() {
        gameTurnController.set();
        showCurrentPlayerBoard();
        isRoundEnded();
    }

    public void summon() {
        gameTurnController.summon();
        showCurrentPlayerBoard();
        isRoundEnded();
    }

    public void changePosition(boolean isChangeToAttack) {
        gameTurnController.changePosition(isChangeToAttack);
    }

    public void flipSummon() {
        gameTurnController.flipSummon();
    }

    public void attack(int position) {
        gameTurnController.attack(position);
        showCurrentPlayerBoard();
        isRoundEnded();
    }

    public void directAttack() {
        gameTurnController.directAttack();
        showCurrentPlayerBoard();
        isRoundEnded();
    }

    public void endPhase() {
        while (!gameTurnController.getPhase().equals(Phase.END)) {
            nextPhase();
        }
        nextPhase();
    }

    public void nextPhase() {
        if (isTurnTempChanged) {
            isTurnTempChanged = false;
            isFirstPlayerTurn = !isFirstPlayerTurn;
            return;
        }
        gameTurnController.nextPhase();
        if (getCurrentPlayerController() instanceof AIPlayerController) {
            ((AIPlayerController) getCurrentPlayerController()).nextPhaseAction();
        }
    }

    public void changeTurnTemp() {
        isTurnTempChanged = true;
        isFirstPlayerTurn = !isFirstPlayerTurn;
    }

    public void nextPhaseAI() {
        if (isTurnTempChanged) {
            isTurnTempChanged = false;
            isFirstPlayerTurn = !isFirstPlayerTurn;
            return;
        }
        gameTurnController.nextPhase();
    }

    public void startRound() {
        gameTurnController = new GameTurnController(id);
        logger.info("in game with id {}: it's {}'s turn", id, isFirstPlayerTurn ? firstPlayer.getPlayer()
                .getUser().getNickname() : secondPlayer.getPlayer().getUser().getNickname());
        gameTurnController.drawPhase();
        getView().showPhase(Phase.DRAW);
        if (getCurrentPlayerController() instanceof AIPlayerController) {
            ((AIPlayerController) getCurrentPlayerController()).startRoundAction();
        }
    }


    public void exchangeSideDeckCards() {

    }

    public void removeMonsterCard(GameCard card) {
        Player cardPlayer = getPlayerByCard(card);
        if (cardPlayer != null && card.getCard().getCardType().equals(CardType.MONSTER)) {
            if (cardPlayer.getField().isInMonsterZone(card))
                removeEffects(card);
            cardPlayer.getField().removeFromHand(card);
            cardPlayer.getField().removeFromMonsterZone(card);
            cardPlayer.getField().putInGraveyard(card);
        }
    }

    public void removeSpellTrapCard(GameCard card) {
        Player cardPlayer = getPlayerByCard(card);
        if (cardPlayer != null && !card.getCard().getCardType().equals(CardType.MONSTER)) {
            if (card.getCard().getCardType().equals(CardType.TRAP)) {
                cardPlayer.getField().removeFromSpellZone(card);
                cardPlayer.getField().putInGraveyard(card);
            } else if (((Spell) card.getCard()).getProperty().equals(SpellProperty.FIELD)) {
                cardPlayer.getField().removeFromFieldZone(card);
                cardPlayer.getField().putInGraveyard(card);
            } else {
                if (cardPlayer.getField().isInHand(card))
                    cardPlayer.getField().removeFromHand(card);
                else {
                    cardPlayer.getField().removeFromSpellZone(card);
                }
                cardPlayer.getField().putInGraveyard(card);
            }
            removeEffects(card);
        }
    }

    public void removeEffects(GameCard card) {
        getCurrentPlayerEffectControllers().removeIf(effectController -> effectController.getEffectCard().equals(card));
        getRivalPlayerEffectControllers().removeIf(effectController -> effectController.getEffectCard().equals(card));
    }

    public EffectResponse knockOutMonster(GameCard monster) {
        removeMonsterCard(monster);
        return applyEffect(Trigger.AFTER_MONSTER_KNOCK_OUT);
    }

    public void activeEffect() {
        GameCard selectedCard = selectionController.getCard();
        EffectResponse response;
        if (!isCardSelected()) {
            view.showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        } else if (!selectedCard.getCard().getCardType().equals(CardType.SPELL)) {
            view.showError(GameView.ERROR_WRONG_CARD_TYPE, "spell card");
        } else if (!(gameTurnController.getPhase().equals(Phase.MAIN1)
                || gameTurnController.getPhase().equals(Phase.MAIN2))) {
            view.showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
        } else if (selectedCard.isRevealed()) {
            view.showError(GameView.ERROR_SPELL_ALREADY_ACTIVATED,"spell");
        } else if ((response = applyEffect(Trigger.BEFORE_ACTIVE_SPELL)) != null && response.equals(EffectResponse.ACTIVE_SPELL_CANT_BE_DONE)) {
            view.showError(GameView.ERROR_CARD_CANT_BE_ACTIVATED, "spell");
        } else if (selectedCard.getCard().getCardType().equals(CardType.SPELL) &&
                (!((Spell) selectedCard.getCard()).getProperty().equals(SpellProperty.QUICK_PLAY)
                        && !getCurrentPlayer().getField().isInSpellZone(selectedCard))) {
            view.showError(GameView.ERROR_CARD_CANT_BE_ACTIVATED,"spell");
        } else {
            EffectController effectController = new EffectController(id, selectedCard);
            selectedCard.setRevealed(true);
            for (Effects cardEffect : selectedCard.getCard().getCardEffects()) {
                if (cardEffect.equals(Effects.SPECIAL_SUMMON_FROM_GRAVEYARD)) {
                    effectController.specialSummonFromGraveyard();
                }
                if (cardEffect.equals(Effects.ADD_FIELD_SPELL_TO_HAND)) {
                    effectController.addFieldSpellToHand();
                }
                if (cardEffect.equals(Effects.DRAW_TWO_CARD)) {
                    effectController.drawCard(2);
                }
                if (cardEffect.equals(Effects.DESTROY_ALL_RIVAL_MONSTERS)) {
                    effectController.destroyAllRivalCards();
                }
                if (cardEffect.equals(Effects.CONTROL_ONE_RIVAL_MONSTER)) {
                    effectController.controlRivalMonster();
                }
                if (cardEffect.equals(Effects.DESTROY_ALL_RIVAL_SPELL_TRAPS)) {
                    effectController.destroyRivalSpellTraps();
                }
                if (cardEffect.equals(Effects.SWORD_OF_REVEALING_LIGHT)) {
                    effectController.flipAllRivalFaceDownMonsters();
                    getCurrentPlayerEffectControllers().add(new EffectController(id, selectedCard, 3));
                }
                if (cardEffect.equals(Effects.DESTROY_ALL_MONSTERS)) {
                    effectController.destroyCurrentPlayerMonsters();
                    effectController.destroyRivalMonsters();
                }
                if (cardEffect.equals(Effects.TWIN_TWISTERS)) {
                    effectController.twinTwisters();
                }
                if (cardEffect.equals(Effects.DESTROY_SPELL_OR_TRAP)) {
                    effectController.destroySpellTrap();
                }
                if (cardEffect.equals(Effects.YAMI)) {
                    effectController.yami();
                }
                if (cardEffect.equals(Effects.FOREST)) {
                    effectController.forest();
                }
                if (cardEffect.equals(Effects.CLOSED_FOREST)) {
                    effectController.closedForest();
                }
                if (cardEffect.equals(Effects.UMIIRUKA)) {
                    effectController.umiiruka();
                }
                if (cardEffect.equals(Effects.SWORD_OF_DARK_DESTRUCTION)) {
                    effectController.swordOfDarkDestruction();
                }
                if (cardEffect.equals(Effects.BLACK_PENDANT)) {
                    effectController.blackPendant();
                }
                if (cardEffect.equals(Effects.UNITED_WE_STAND)) {
                    effectController.unitedWeStand();
                }
                if (cardEffect.equals(Effects.MAGNUM_SHIELD)) {
                    effectController.magnumShield();
                }
                if (cardEffect.equals(Effects.DRAW_CARD_IF_MONSTER_DESTROYED)) {
                    getCurrentPlayerEffectControllers().add(effectController);
                }
                if (cardEffect.equals(Effects.INCREASE_LP_IF_SPELL_ACTIVATED)) {
                    getCurrentPlayerEffectControllers().add(effectController);
                }
                if (cardEffect.equals(Effects.MESSENGER_OF_PEACE)) {
                    getCurrentPlayerEffectControllers().add(effectController);
                }
                if (cardEffect.equals(Effects.RING_OF_DEFENSE)) {
                    getCurrentPlayerEffectControllers().add(effectController);
                }
            }
            if (!((Spell)selectedCard.getCard()).getProperty().equals(SpellProperty.CONTINUOUS)) {
                removeSpellTrapCard(selectedCard);
            }
            applyEffect(Trigger.AFTER_ACTIVE_SPELL);
            view.showSuccess(GameView.SUCCESS_SPELL_ACTIVATED, selectedCard.getCard().getName());
        }
    }

    public boolean canActiveTrap(EffectController effectController) {
        if (getRivalPlayerEffectControllers().contains(effectController))
            return !applyEffect(Trigger.BEFORE_ACTIVE_TRAP).equals(EffectResponse.ACTIVE_TRAP_CANT_BE_DONE)
                    && getRivalPlayerController().confirm("do you want to active " + effectController.getEffectCard().getCard().getName() + " trap");
        else if (getCurrentPlayerEffectControllers().contains(effectController))
            return !applyEffect(Trigger.BEFORE_ACTIVE_TRAP).equals(EffectResponse.ACTIVE_TRAP_CANT_BE_DONE)
                    && getCurrentPlayerController().confirm("do you want to active " + effectController.getEffectCard().getCard().getName() + " trap");
        return true;
    }

    public EffectResponse applyEffect(Trigger trigger) {
        for (EffectController effectController : new ArrayList<>(getCurrentPlayerEffectControllers())) {
            //ignore disposable effects
            if (gameTurnController.getDisposableUsedEffects().contains(effectController)) continue;
            //effects without trigger
            if (effectController.containEffect(Effects.SELECT_FACE_UP_MONSTERS)) {
                effectController.selectFaceUpMonsters();
            }
            //Mind Crush
            if (effectController.containEffect(Effects.MIND_CRUSH)) {
                effectController.mindCrush();
            }
            //Calculator
            if (effectController.containEffect(Effects.COMBINE_LEVELS_OF) &&
                    effectController.containEffect(Effects.SET_ATTACK)) {
                effectController.combineLevelsOfFaceUpCards();
            }
            //effects with trigger
            if (trigger.equals(Trigger.STANDBY)) {
                //Messenger of peace
                if (effectController.containEffect(Effects.MESSENGER_OF_PEACE)) {
                    effectController.messengerOfPeace();
                }
            } else if (trigger.equals(Trigger.DRAW)) {
                //Herald of Creation
                if (effectController.containEffect(Effects.HERALD_OF_CREATION)) {
                    effectController.drawCardFromGraveyard(7);
                    effectController.disposableEffect();
                }
                //Scanner
                if (effectController.containEffect(Effects.SCAN_A_DESTROYED_MONSTER)) {
                    effectController.scanDestroyedRivalMonster();
                    effectController.disposableEffect();
                }
            } else if (trigger.equals(Trigger.SET)) {

            } else if (trigger.equals(Trigger.BEFORE_SUMMON)) {

            } else if (trigger.equals(Trigger.AFTER_FLIP_SUMMON)) {
                //Man-Eater Bug
                if (effectController.containEffect(Effects.DESTROY_ONE_OF_RIVAL_MONSTERS_AFTER_FLIP)) {
                    effectController.destroyOneOfRivalMonsters();
                }
            } else if (trigger.equals(Trigger.AFTER_SUMMON)) {
                //Command Knight
                if (effectController.containEffect(Effects.ADD_ATTACK_TO_ALL_MONSTERS)) {
                    //we can change this value (400) if we want
                    effectController.addAttackToAllMonsters(400);
                }
            } else if (trigger.equals(Trigger.AFTER_NORMAL_SUMMON)) {
                //Terratiger
                if (effectController.containEffect(Effects.TERRATIGER)) {
                    effectController.specialSetFromHand();
                    getCurrentPlayerEffectControllers().remove(effectController);
                }
            } else if (trigger.equals(Trigger.BEFORE_ATTACK)) {
                //Messenger of peace
                if (effectController.containEffect(Effects.MESSENGER_OF_PEACE)) {
                    if (effectController.isAttackerMonsterPowerful(1500))
                        return EffectResponse.ATTACK_CANT_BE_DONE;
                }
                //Exploder Dragon
                if (effectController.containEffect(Effects.LPS_DOESNT_CHANGE)
                        && attackController.getAttackedMonster().equals(effectController.getEffectCard())) {
                    effectController.lpsCantChange();
                }
            } else if (trigger.equals(Trigger.AFTER_ATTACK)) {
                //Exploder Dragon
                if (effectController.containEffect(Effects.DESTROY_ANOTHER_CARD_IN_BATTLE_IF_DESTROYED)
                        && (attackController.getAttackedMonster().equals(effectController.getEffectCard())
                        || attackController.getAttackingMonster().equals(effectController.getEffectCard()))) {
                    effectController.destroyAnotherCardInBattleIfDestroyed();
                }
            } else if (trigger.equals(Trigger.BEFORE_ACTIVE_TRAP)) {

            } else if (trigger.equals(Trigger.AFTER_ACTIVE_SPELL)) {
                //Spell Absorption
                if (effectController.containEffect(Effects.INCREASE_LP_IF_SPELL_ACTIVATED)) {
                    effectController.increaseLP(500);
                }
            } else if (trigger.equals(Trigger.AFTER_MONSTER_KNOCK_OUT)) {
                //Supply Squad
                if (effectController.containEffect(Effects.DRAW_CARD_IF_MONSTER_DESTROYED)) {
                    effectController.drawCard(1);
                    effectController.disposableEffect();
                }
            }
        }
        for (EffectController effectController : new ArrayList<>(getRivalPlayerEffectControllers())) {
            //ignore disposable effects
            if (gameTurnController.getDisposableUsedEffects().contains(effectController)) continue;
            //effects without trigger
            if (effectController.containEffect(Effects.SELECT_FACE_UP_MONSTERS)) {
                effectController.selectFaceUpMonsters();
            }
            //Calculator
            if (effectController.containEffect(Effects.COMBINE_LEVELS_OF) &&
                    effectController.containEffect(Effects.SET_ATTACK)) {
                effectController.combineLevelsOfFaceUpCards();
            }
            //Mind Crush
            if (effectController.containEffect(Effects.MIND_CRUSH)) {
                effectController.mindCrush();
            }
            //effects with trigger
            if (trigger.equals(Trigger.DRAW)) {
                if (effectController.containEffect(Effects.SWORD_OF_REVEALING_LIGHT)) {
                    effectController.decreaseRemainTurns();
                    if (effectController.getRemainsTurn() == 1) {
                        getRivalPlayerEffectControllers().remove(effectController);
                        removeMonsterCard(effectController.getEffectCard());
                    }
                }
            } else if (trigger.equals(Trigger.SET)) {

            } else if (trigger.equals(Trigger.BEFORE_SUMMON)) {

            } else if (trigger.equals(Trigger.AFTER_FLIP_SUMMON)) {

            } else if (trigger.equals(Trigger.AFTER_SUMMON)) {

            } else if (trigger.equals(Trigger.AFTER_NORMAL_SUMMON)) {

            } else if (trigger.equals(Trigger.BEFORE_ATTACK)) {
                //Magic Cylinder
                if (effectController.containEffect(Effects.MAGIC_CYLINDER) && canActiveTrap(effectController)) {
                    effectController.magicCylinder();
                    return EffectResponse.ATTACK_CANT_BE_DONE;
                }
                //Mirror Force
                if (effectController.containEffect(Effects.DESTROY_ALL_RIVAL_FACE_UP_MONSTERS) && canActiveTrap(effectController)) {
                    effectController.destroyAllRivalFaceUpMonsters();
                }
                //Command Knight
                if (effectController.containEffect(Effects.CAN_NOT_BE_ATTACKED_WHEN_WE_HAVE_ANOTHER_MONSTER) &&
                        attackController.getAttackedMonster().equals(effectController.getEffectCard())) {
                    if (effectController.isThereAnotherMonster()) {
                        return EffectResponse.ATTACK_CANT_BE_DONE;
                    }
                }
                //Suijin
                if (effectController.containEffect(Effects.ZERO_ATTACK_POWER_FOR_ATTACKER_ON_THAT_TURN)
                        && attackController.getAttackedMonster().equals(effectController.getEffectCard())) {
                    effectController.setZeroAttackForAttackerCard();
                }
                //Marshmallon
                if (effectController.containEffect(Effects.DECREASE_ATTACKER_LP_IF_FACE_DOWN)
                        && attackController.getAttackedMonster().equals(effectController.getEffectCard())) {
                    effectController.decreaseAttackerLPIfAttackedCardFaceDown(1000);
                }
                //Texchanger
                if (effectController.containEffect(Effects.SPECIAL_SUMMON_A_NORMAL_CYBERSE_MONSTER)
                        && attackController.getAttackedMonster().equals(effectController.getEffectCard())) {
                    effectController.summonNormalCyberseMonster();
                }
                if (effectController.containEffect(Effects.NEUTRAL_ONE_ATTACK_IN_EACH_TURN)
                        && attackController.getAttackedMonster().equals(effectController.getEffectCard())) {
                    effectController.disposableEffect();
                    getGameTurnController().getAttackedMonsters().add(attackController.getAttackingMonster());
                    return EffectResponse.ATTACK_CANT_BE_DONE;
                }
                //Sword of Revealing Light
                if (effectController.containEffect(Effects.SWORD_OF_REVEALING_LIGHT)
                        && effectController.getRemainsTurn() > 0) {
                    return EffectResponse.ATTACK_CANT_BE_DONE;
                }
                //Messenger of peace
                if (effectController.containEffect(Effects.MESSENGER_OF_PEACE)) {
                    if (effectController.isAttackerMonsterPowerful(1500))
                        return EffectResponse.ATTACK_CANT_BE_DONE;
                }
                //Exploder Dragon
                if (effectController.containEffect(Effects.LPS_DOESNT_CHANGE)
                        && attackController.getAttackedMonster().equals(effectController.getEffectCard())) {
                    effectController.lpsCantChange();
                }
            } else if (trigger.equals(Trigger.AFTER_ATTACK)) {
                //Yomi ship
                if (effectController.containEffect(Effects.DESTROY_ATTACKER_CARD_IF_DESTROYED)
                        && attackController.getAttackedMonster().equals(effectController.getEffectCard())) {
                    effectController.destroyAttackerCardIfDestroyed();
                }
                //Exploder Dragon
                if (effectController.containEffect(Effects.DESTROY_ANOTHER_CARD_IN_BATTLE_IF_DESTROYED)
                        && (attackController.getAttackedMonster().equals(effectController.getEffectCard())
                        || attackController.getAttackingMonster().equals(effectController.getEffectCard()))) {
                    effectController.destroyAnotherCardInBattleIfDestroyed();
                }
            } else if (trigger.equals(Trigger.BEFORE_ACTIVE_TRAP)) {
                //Mirage Dragon
                if (effectController.containEffect(Effects.RIVAL_CANT_ACTIVE_TRAP)) {
                    return EffectResponse.ACTIVE_TRAP_CANT_BE_DONE;
                }
            } else if (trigger.equals(Trigger.AFTER_ACTIVE_SPELL)) {

            }
        }
        return null;
    }

    public void resetEffect() {
        for (GameCard monster : getFirstPlayer().getPlayer().getField().getAllFieldMonsterCards()) {
            monster.getAttackModifier().removeIf(modifier -> modifier.isFromEffect() && modifier.isDisposableEachTurn());
        }
    }

    private void isRoundEnded() {
        if (firstPlayer.getPlayer().getLifePoints() <= 0) {
            firstPlayer.getPlayer().setLifePoints(0);
            endRound(false);
        } else if (secondPlayer.getPlayer().getLifePoints() <= 0) {
            secondPlayer.getPlayer().setLifePoints(0);
            endRound(true);
        }
    }

    public void endRound(boolean isFirstPlayerWin) {
        RoundResult result = new RoundResult();
        result.setFirstPlayerWin(isFirstPlayerWin);
        result.setFirstPlayerLifePoints(firstPlayer.getPlayer().getLifePoints());
        result.setSecondPlayerLifePoints(secondPlayer.getPlayer().getLifePoints());
        roundResults.add(result);
        getView().showRoundResult(roundResults, firstPlayer.getPlayer(), secondPlayer.getPlayer());
        if (numberOfRounds == 1) {
            endGame();
            return;
        }
        if (roundResults.size() == 3) {
            endGame();
            return;
        } else if (roundResults.size() == 2) {
            if (roundResults.get(0).isFirstPlayerWin() == roundResults.get(1).isFirstPlayerWin()) {
                endGame();
                return;
            }
        }
        firstPlayer.getPlayer().resetField();
        firstPlayer.getPlayer().setLifePoints(8000);
        secondPlayer.getPlayer().resetField();
        secondPlayer.getPlayer().setLifePoints(8000);
        passedTurns = 0;
        isFirstPlayerTurn = (roundResults.size() % 2 == 0) == isFirstPlayerTurn;
        play();
    }

    public void surrender() {
        endRound(!isFirstPlayerTurn);
    }

    public void endGame() {
        if (numberOfRounds == 1) {
            Player winner = roundResults.get(0).isFirstPlayerWin() ? firstPlayer.getPlayer() : secondPlayer.getPlayer();
            Player loser = roundResults.get(0).isFirstPlayerWin() ? secondPlayer.getPlayer() : firstPlayer.getPlayer();
            int winnerLP = roundResults.get(0).isFirstPlayerWin() ? roundResults.get(0).getFirstPlayerLifePoints() :
                    roundResults.get(0).getSecondPlayerLifePoints();
            winnerLP = Math.max(winnerLP, 0);
            winner.getUser().setScore(winner.getUser().getScore() + 1000);
            Inventory.getInventoryByUserID(winner.getUser().getId()).setMoney(Inventory.getInventoryByUserID(winner.getUser().getId()).getMoney() + 1000 + winnerLP);
            Inventory.getInventoryByUserID(loser.getUser().getId()).setMoney(Inventory.getInventoryByUserID(loser.getUser().getId()).getMoney() + 100);
            getView().showGameResult(winner, numberOfRounds, winnerLP);
        } else {
            int maxFirstPlayerLP = roundResults.stream().mapToInt(RoundResult::getFirstPlayerLifePoints).max().getAsInt();
            int maxSecondPlayerLP = roundResults.stream().mapToInt(RoundResult::getSecondPlayerLifePoints).max().getAsInt();
            Player winner = roundResults.get(roundResults.size() - 1).isFirstPlayerWin() ? firstPlayer.getPlayer() : secondPlayer.getPlayer();
            Player loser = roundResults.get(roundResults.size() - 1).isFirstPlayerWin() ? secondPlayer.getPlayer() : firstPlayer.getPlayer();
            int winnerLP = winner.getUser().getUsername().equals(firstPlayer.getPlayer().getUser().getUsername()) ? maxFirstPlayerLP : maxSecondPlayerLP;
            winnerLP = Math.max(winnerLP, 0);
            winner.getUser().setScore(winner.getUser().getScore() + 1000);
            Inventory.getInventoryByUserID(winner.getUser().getId()).setMoney(Inventory.getInventoryByUserID(winner.getUser().getId()).getMoney() + 3000 + 3 * winnerLP);
            Inventory.getInventoryByUserID(loser.getUser().getId()).setMoney(Inventory.getInventoryByUserID(loser.getUser().getId()).getMoney() + 300);
            getView().showGameResult(winner, numberOfRounds, winnerLP);
        }
        DatabaseManager.updateInventoriesToDB();
        ProgramController.setGameControllerID(-1);
    }

    public PlayerController getPlayerControllerByPlayer(Player player) {
        if (firstPlayer.getPlayer().equals(player))
            return firstPlayer;
        return secondPlayer;
    }

    public List<EffectController> getCurrentPlayerEffectControllers() {
        if (getCurrentPlayerController().equals(firstPlayer))
            return firstPlayerEffectControllers;
        return secondPlayerEffectControllers;
    }

    public List<EffectController> getRivalPlayerEffectControllers() {
        if (getRivalPlayerController().equals(firstPlayer))
            return firstPlayerEffectControllers;
        return secondPlayerEffectControllers;
    }

    public void showCurrentPlayerBoard() {
        getView().showBoard(getCurrentPlayer(), getRivalPlayer());
    }

    public void showRivalPlayerBoard() {
        getView().showBoard(getRivalPlayer(), getCurrentPlayer());
    }

    public void showCurrentPlayerGraveyard() {
        getView().showGraveyard(getCurrentPlayer());
    }

    public void showRivalPlayerGraveyard() {
        getView().showGraveyard(getRivalPlayer());
    }

    public boolean isCardSelected() {
        return selectionController != null;
    }

    public Player getPlayerByCard(GameCard card) {
        if (getCurrentPlayer().getField().isInField(card)) return getCurrentPlayer();
        if (getRivalPlayer().getField().isInField(card)) return getRivalPlayer();
        return null;
    }

    public PlayerController getCurrentPlayerController() {
        return isFirstPlayerTurn ? firstPlayer : secondPlayer;
    }

    public PlayerController getRivalPlayerController() {
        return isFirstPlayerTurn ? secondPlayer : firstPlayer;
    }

    public Player getCurrentPlayer() {
        return getCurrentPlayerController().getPlayer();
    }

    public Player getRivalPlayer() {
        return getRivalPlayerController().getPlayer();
    }
}
