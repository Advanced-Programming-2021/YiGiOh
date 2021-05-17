package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.EffectResponse;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.Trigger;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
import edu.sharif.ce.apyugioh.view.GameView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SummonController {

    private static Logger logger;
    private static HashSet<String> specialCases;

    static {
        logger = LogManager.getLogger(SummonController.class);
        specialCases = new HashSet<>();
        specialCases.add("Beast King Barbaros");
        specialCases.add("Gate Guardian");
        specialCases.add("The Tricky");
    }

    private Player summoningPlayer;
    private GameCard card;
    private int gameControllerID;

    public SummonController(int gameControllerID, GameCard card) {
        this.gameControllerID = gameControllerID;
        this.card = card;
        summoningPlayer = getGameController().getPlayerByCard(card);
    }

    public boolean normalSummon() {
        if (specialCases.contains(card.getCard().getName()))
            return summonSpecialMonsters();
        if (!checkForTribute())
            return false;
        return summon();
    }

    private boolean summon() {
        card.setRevealed(true);
        card.setFaceDown(false);
        moveMonsterToMonsterZone();
        getEffectControllersByPlayer().add(new EffectController(gameControllerID, card));
        logger.info("in game with id {}: summon successful", gameControllerID);
        getGameController().applyEffect(Trigger.AFTER_SUMMON);
        getGameController().applyEffect(Trigger.AFTER_NORMAL_SUMMON);
        if (summoningPlayer.getField().isInMonsterZone(card))
            GameController.getView().showSuccess(GameView.SUCCESS_SUMMON_SUCCESSFUL);
        return summoningPlayer.getField().isInMonsterZone(card);
    }

    private boolean summonSpecialMonsters() {
        ArrayList<String> choices = new ArrayList<>();
        switch (card.getCard().getName()) {
            case "Beast King Barbaros":
                choices.add("1. normal summon (by tributing 2 monsters)");
                choices.add("2. normal summon without tribute (Attack points decreases to 1900");
                choices.add("3. summon by tributing 3 monsters (all cards the your rival control will be kicked out");
                int choice = getGameController().getPlayerControllerByPlayer(summoningPlayer).chooseHowToSummon(choices) + 1;
                if (choice == 1) {
                    if (!checkForTribute())
                        return false;
                    return summon();
                } else if (choice == 2) {
                    card.addAttackModifier(-1100, false);
                    return summon();
                } else if (choice == 3) {
                    if (!tribute(3))
                        return false;
                    boolean result = summon();
                    if (result)
                        getGameController().applyEffect(Trigger.AFTER_SPECIAL_SUMMON);
                    return result;
                } else
                    return false;
            case "Gate Guardian":
                if (summoningPlayer.getField().getAvailableMonstersInZoneCount() < 3) {
                    GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                    return false;
                }
                if (!tribute(3))
                    return false;
                return summon();
            case "The Tricky":
                choices.add("1. summon normally (tribute 1 monster)");
                choices.add("2. summon by removing a card from your hand");
                int result = 1 + getGameController().getPlayerControllerByPlayer(summoningPlayer).chooseHowToSummon(choices);
                if (result == 1) {
                    if (!checkForTribute())
                        return false;
                    return summon();
                } else if (result == 2) {
                    GameCard selectedCardFromHand;
                    if ((selectedCardFromHand = getGameController().getPlayerControllerByPlayer(summoningPlayer).selectCardFromHand(card)) != null) {
                        getGameController().removeMonsterCard(selectedCardFromHand);
                        return summon();
                    }
                }
                return false;
            default:
                return false;
        }
    }

    private boolean checkForTribute() {
        int availableMonsters = summoningPlayer.getField().getAvailableMonstersInZoneCount();
        if (((Monster) card.getCard()).getLevel() == 5 || ((Monster) card.getCard()).getLevel() == 6) {
            if (availableMonsters < 1) {
                logger.info("in game with id {}: can't summon | not enough cards to tribute", gameControllerID);
                GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                return false;
            }
            if (!tribute(1))
                return false;
        } else if (((Monster) card.getCard()).getLevel() >= 7) {
            if (availableMonsters < 2) {
                logger.info("in game with id {}: can't summon | not enough cards to tribute", gameControllerID);
                GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                return false;
            }
            if (!tribute(2))
                return false;
        }
        return true;
    }

    public boolean specialSummon() {
        if (summoningPlayer.getField().getAvailableMonstersInZoneCount() == 5) {
            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
            return false;
        }
        boolean result = summon();
        if (!result)
            return false;
        EffectResponse response = getGameController().applyEffect(Trigger.AFTER_SPECIAL_SUMMON);
        if (response != null && response.equals(EffectResponse.SUMMON_CANT_BE_DONE)) {
            Utils.printError("you can't special summon this card");
            return false;
        }
        return true;
    }

    public boolean ritualSummon() {
        List<GameCard> cards = getGameController().getCurrentPlayerController().selectCardsForRitualTribute(((Monster) card.getCard()).getLevel());
        if (cards == null)
            return false;
        if (getGameController().getPlayerByCard(card).getField().getAvailableMonstersInZoneCount() == 5) {
            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
            return false;
        }
        for (GameCard gameCard : cards)
            getGameController().removeMonsterCard(gameCard);
        summon();
        return summoningPlayer.getField().isInMonsterZone(card);
    }

    public boolean flipSummon() {
        card.setRevealed(true);
        card.setFaceDown(false);
        getGameTurnController().setChangedPositionMonster(card);
        getEffectControllersByPlayer().add(new EffectController(gameControllerID, card));
        getGameController().applyEffect(Trigger.AFTER_SUMMON);
        getGameController().applyEffect(Trigger.AFTER_FLIP_SUMMON);
        return summoningPlayer.getField().isInMonsterZone(card);
    }

    private boolean tribute(int amount) {
        GameCard[] tributeMonsters = getGameController().getPlayerControllerByPlayer(summoningPlayer).tributeMonster(amount);
        if (tributeMonsters == null) return false;
        for (GameCard tributeMonster : tributeMonsters) {
            if (tributeMonster == null)
                return false;
        }
        for (GameCard tributeMonster : tributeMonsters) {
            getGameController().removeMonsterCard(tributeMonster);
        }
        return true;
    }

    private void moveMonsterToMonsterZone() {
        if (summoningPlayer.getField().isInHand(card))
            summoningPlayer.getField().removeFromHand(card);
        if (summoningPlayer.getField().isInDeck(card))
            summoningPlayer.getField().removeFromDeck(card);
        if (summoningPlayer.getField().isInGraveyard(card))
            summoningPlayer.getField().removeFromGraveyard(card);
        summoningPlayer.getField().putInMonsterZone(card);
    }

    private List<EffectController> getEffectControllersByPlayer() {
        if (summoningPlayer.equals(getGameController().getFirstPlayer().getPlayer()))
            return getGameController().getFirstPlayerEffectControllers();
        return getGameController().getSecondPlayerEffectControllers();
    }

    private GameTurnController getGameTurnController() {
        return getGameController().getGameTurnController();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }

}
