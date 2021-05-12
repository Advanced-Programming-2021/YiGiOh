package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.Field;
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

    public SummonController(int gameControllerID,GameCard card) {
        this.gameControllerID = gameControllerID;
        this.card = card;
        summoningPlayer = getGameController().getPlayerByCard(card);
    }

    public boolean normalSummon() {
        if (specialCases.contains(card.getCard().getName()))
            return specialSummon();
        if (!checkForTribute())
            return false;
        summon();
        return true;
    }

    private void summon(){
        card.setRevealed(true);
        card.setFaceDown(false);
        summoningPlayer.getField().removeFromHand(card);
        summoningPlayer.getField().putInMonsterZone(card);
        getEffectControllersByPlayer().add(new EffectController(gameControllerID,card));
        logger.info("in game with id {}: summon successful", gameControllerID);
        getGameController().applyEffect(Trigger.AFTER_SUMMON);
        getGameController().applyEffect(Trigger.AFTER_NORMAL_SUMMON);
    }

    private boolean checkForTribute(){
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

    public boolean specialSummon(){
        ArrayList<String> choices = new ArrayList<String>();
        switch(card.getCard().getName()){
            case "Beast King Barbaros":
                choices.add("1. normal summon (by tributing 2 monsters)");
                choices.add("2. normal summon without tribute (Attack points decreases to 1900");
                choices.add("3. summon by tributing 3 monsters (all cards the your rival control will be kicked out");
                int choice = getGameController().getPlayerControllerByPlayer(summoningPlayer).chooseHowToSummon(choices);
                if (choice == 1) {
                    if (!checkForTribute())
                        return false;
                    summon();
                    return true;
                } else if (choice == 2){
                    card.addAttackModifier(-1100);
                    summon();
                    return true;
                } else if (choice == 3){
                    if (!tribute(3))
                        return false;
                    summon();
                    getGameController().applyEffect(Trigger.AFTER_SPECIAL_SUMMON);
                } else
                    return false;
            case "Gate Guardian":
                if (summoningPlayer.getField().getAvailableMonstersInZoneCount() < 3){
                    GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                    return false;
                }
                if (!tribute(3))
                    return false;
                summon();
                return true;
            case "The Tricky":
                choices.add("1. summon normally (tribute 1 monster)");
                choices.add("2. summon by removing a card from your hand");
                int result = getGameController().getPlayerControllerByPlayer(summoningPlayer).chooseHowToSummon(choices);
                if (result == 1){
                    if (!checkForTribute())
                        return false;
                    summon();
                    return true;
                }else if (result == 2){
                    GameCard selectedCardFromHand;
                    if ((selectedCardFromHand = getGameController().getPlayerControllerByPlayer(summoningPlayer).selectCardFromHand()) != null){
                        getGameController().removeCard(selectedCardFromHand);
                        summon();
                    }
                }
                return true;
            default:
                normalSummon();
                Utils.printError("Special summoned monster is not valid");
                return false;
        }

    }

    public boolean ritualSummon() {
        List<GameCard> cards = getGameController().getCurrentPlayerController().selectCardsForRitualTribute(((Monster)card.getCard()).getLevel());
        if (cards == null)
            return false;
        if (getGameController().getPlayerByCard(card).getField().getAvailableMonstersInZoneCount() == 5){
            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
            return false;
        }
        for(GameCard gameCard:cards)
            getGameController().removeCard(gameCard);
        summon();
        return true;
    }

    public boolean flipSummon() {
        card.setRevealed(true);
        card.setFaceDown(false);
        getGameTurnController().setChangedPositionMonster(card);
        getEffectControllersByPlayer().add(new EffectController(gameControllerID,card));
        getGameController().applyEffect(Trigger.AFTER_SUMMON);
        getGameController().applyEffect(Trigger.AFTER_FLIP_SUMMON);
        return true;
    }

    private boolean tribute(int amount) {
        GameCard[] tributeMonsters = getGameController().getPlayerControllerByPlayer(summoningPlayer).tributeMonster(amount);
        if (tributeMonsters == null) return false;
        for (GameCard tributeMonster : tributeMonsters) {
            if (tributeMonster == null)
                return false;
        }
        for (GameCard tributeMonster : tributeMonsters) {
            getGameController().removeCard(tributeMonster);
        }
        return true;
    }

    private List<EffectController> getEffectControllersByPlayer(){
        if (summoningPlayer.equals(getGameController().getFirstPlayer()))
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
