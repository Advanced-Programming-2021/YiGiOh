package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.*;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;
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
    private boolean isFirstPlayerTurn;
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
        gameTurnController = new GameTurnController(id);
        logger.info("in game with id {}: it's {}'s turn", id, isFirstPlayerTurn ? firstPlayer.getPlayer()
                .getUser().getNickname() : secondPlayer.getPlayer().getUser().getNickname());
        gameTurnController.drawPhase();
        showCurrentPlayerBoard();
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
    }

    public void summon() {
        gameTurnController.summon();
    }

    public void changePosition(boolean isChangeToAttack) {
        gameTurnController.changePosition(isChangeToAttack);
    }

    public void flipSummon() {
        gameTurnController.flipSummon();
    }

    public void attack(int position) {

    }

    public void directAttack() {
        gameTurnController.directAttack();
    }

    public void nextPhase() {
        gameTurnController.nextPhase();
    }

    public void endRound(boolean isFirstPlayerWin) {

    }

    public void startRound() {

    }

    public void surrender() {

    }

    public void cancel() {

    }

    public void exchangeSideDeckCards() {

    }

    public void activeEffect() {
        GameCard selectedCard = selectionController.getCard();
        if (selectedCard == null) {
            //no card is selected
        } else if (!selectedCard.getCard().getCardType().equals(CardType.SPELL)) {
            //activate effect is only for spell cards
        } else if (!(gameTurnController.getPhase().equals(Phase.MAIN1)
                || gameTurnController.getPhase().equals(Phase.MAIN2))) {
            //you can't activate an effect on this turn
        } else if (!selectedCard.isFaceDown()) {
            //you have already activate this card
        } else {
            EffectController effectController = new EffectController(id, selectedCard);
            for (Effects cardEffect : selectedCard.getCard().getCardEffects()) {
                if (cardEffect.equals(Effects.SPECIAL_SUMMON_FROM_GRAVEYARD)) {
                    effectController.specialSummonFromGraveyard();
                } else if (cardEffect.equals(Effects.ADD_FIELD_SPELL_TO_HAND)) {
                    effectController.addFieldSpellToHand();
                } else if (cardEffect.equals(Effects.DRAW_TWO_CARD)) {
                    effectController.drawCard(2);
                } else if (cardEffect.equals(Effects.DESTROY_ALL_RIVAL_MONSTERS)) {
                    effectController.destroyAllRivalCards();
                } else if (cardEffect.equals(Effects.CONTROL_ONE_RIVAL_MONSTER)) {
                    effectController.controlRivalMonster();
                } else if (cardEffect.equals(Effects.DESTROY_ALL_RIVAL_SPELL_TRAPS)) {
                    effectController.destroyRivalSpellTraps();
                } else if (cardEffect.equals(Effects.SWORD_OF_REVEALING_LIGHT)) {
                    effectController.flipAllRivalFaceDownMonsters();
                } else if (cardEffect.equals(Effects.DESTROY_ALL_MONSTERS)) {
                    effectController.destroyCurrentPlayerMonsters();
                    effectController.destroyRivalMonsters();
                } else if (cardEffect.equals(Effects.TWIN_TWISTERS)) {
                    effectController.twinTwisters();
                } else if (cardEffect.equals(Effects.DESTROY_SPELL_OR_TRAP)) {
                    effectController.destroySpellTrap();
                } else if (cardEffect.equals(Effects.YAMI)) {
                    effectController.yami();
                } else if (cardEffect.equals(Effects.FOREST)) {
                    effectController.forest();
                } else if (cardEffect.equals(Effects.CLOSED_FOREST)) {
                    effectController.closedForest();
                } else if (cardEffect.equals(Effects.UMIIRUKA)) {
                    effectController.umiiruka();
                } else if (cardEffect.equals(Effects.SWORD_OF_DARK_DESTRUCTION)) {

                }
            }
        }
    }

    public void applyEffect(Trigger trigger) {
        for (EffectController effectController : firstPlayerEffectControllers) {
            if (trigger.equals(Trigger.SET)) {

            } else if (trigger.equals(Trigger.BEFORE_SUMMON)) {

            } else if (trigger.equals(Trigger.AFTER_FLIP_SUMMON)) {
                if (effectController.containEffect(Effects.DESTROY_ONE_OF_RIVAL_MONSTERS_AFTER_FLIP)) {
                    effectController.destroyOneOfRivalMonsters();
                }
            } else if (trigger.equals(Trigger.AFTER_SUMMON)) {
                if (effectController.containEffect(Effects.ADD_ATTACK_TO_FACE_UP_CARDS)) {
                    effectController.selectFaceUpMonsters();
                    //we can change this value (400) if we want
                    effectController.changeAttack(400);
                }
            } else if (trigger.equals(Trigger.BEFORE_ATTACK)) {

            } else if (trigger.equals(Trigger.AFTER_ATTACK)) {
                if (effectController.containEffect(Effects.DESTROY_ATTACKER_CARD_IF_DESTROYED)) {
                    effectController.destroyAttackerCard();
                }
            } else if (trigger.equals(Trigger.ALWAYS)) {

            }
        }
    }

    public void showCurrentPlayerBoard() {
        getView().showBoard(getCurrentPlayer(), getRivalPlayer());
    }

    public void showRivalPlayerBoard() {
        getView().showBoard(getRivalPlayer(), getCurrentPlayer());
    }

    public boolean isCardSelected() {
        return selectionController != null;
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
