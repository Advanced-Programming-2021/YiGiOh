package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.player.ArraySelectionAction;
import edu.sharif.ce.apyugioh.controller.player.ConfirmationAction;
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
import java.util.concurrent.ArrayBlockingQueue;

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

    public SummonController(int gameControllerID, GameCard card, Player summoningPlayer) {
        this.gameControllerID = gameControllerID;
        this.card = card;
        this.summoningPlayer = summoningPlayer;
    }

    public SummonController(int gameControllerID, GameCard card) {
        System.out.println("Create SummonController");
        this.gameControllerID = gameControllerID;
        this.card = card;
        summoningPlayer = getGameController().getPlayerByCard(card);
        System.out.println("SummonController Created");
    }

    public void normalSummon() {
        System.out.println("Normal Summon");
        if (specialCases.contains(card.getCard().getName()))
            summonSpecialMonsters();
        checkForTribute(new ConfirmationAction() {
            @Override
            public Boolean call() throws Exception {
                if (choice == null) return false;
                Boolean isTributePossible = choice.peek();
                if (isTributePossible == null) return false;
                if (isTributePossible) summon();
                return false;
            }
        });
    }

    private void summon() {
        card.setRevealed(true);
        card.setFaceDown(false);
        moveMonsterToMonsterZone();
        getGameController().getEffectControllersByPlayer(getGameController().getPlayerByCard(card))
                .add(new EffectController(gameControllerID, card));
        logger.info("in game with id {}: summon successful", gameControllerID);
        getGameController().applyEffect(Trigger.AFTER_SUMMON, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                System.out.println("After summon action");
                return null;
            }
        });
        getGameController().applyEffect(Trigger.AFTER_NORMAL_SUMMON, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                System.out.println("After normal summon action");
                return null;
            }
        });
        if (summoningPlayer.getField().isInMonsterZone(card)) {
            GameController.getView().showSuccess(GameView.SUCCESS_SUMMON_SUCCESSFUL);
            getGameTurnController().setSummonedMonster(card);
        }
    }

    private void summonSpecialMonsters() {
        ArrayList<String> choices = new ArrayList<>();
        switch (card.getCard().getName()) {
            case "Beast King Barbaros":
                choices.add("1. normal summon (by tributing 2 monsters)");
                choices.add("2. normal summon without tribute (Attack points decreases to 1900");
                choices.add("3. summon by tributing 3 monsters (all cards the your rival control will be kicked out");
                int choice = getGameController().getPlayerControllerByPlayer(summoningPlayer).chooseHowToSummon(choices) + 1;
                if (choice == 1) {
                    checkForTribute(new ConfirmationAction() {
                        @Override
                        public Boolean call() throws Exception {
                            if (choice == null) return false;
                            Boolean isTributePossible = choice.peek();
                            if (isTributePossible == null) return false;
                            if (isTributePossible) summon();
                            return false;
                        }
                    });
                    return;
                } else if (choice == 2) {
                    card.addAttackModifier(-1100, false);
                    summon();
                } else if (choice == 3) {
                    tribute(3, new ConfirmationAction() {
                        @Override
                        public Boolean call() throws Exception {
                            if (choice == null) return false;
                            Boolean isTributePossible = choice.peek();
                            if (isTributePossible == null) return false;
                            if (isTributePossible) summon();
                            return false;
                        }
                    });
                    if (summoningPlayer.getField().isInMonsterZone(card)) {
                        getGameController().getEffectControllersByPlayer(getGameController().getPlayerByCard(card))
                                .add(new EffectController(gameControllerID, card));
                        getGameController().applyEffect(Trigger.AFTER_SPECIAL_SUMMON, new EffectAction() {
                            @Override
                            public EffectResponse call() throws Exception {
                                return null;
                            }
                        });
                    }
                    return;
                } else
                    return;
            case "Gate Guardian":
                if (summoningPlayer.getField().getAvailableMonstersInZoneCount() < 3) {
                    GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                    return;
                }
                tribute(3, new ConfirmationAction() {
                    @Override
                    public Boolean call() throws Exception {
                        if (choice == null) return false;
                        Boolean isTributePossible = choice.peek();
                        if (isTributePossible == null) return false;
                        if (isTributePossible) summon();
                        return false;
                    }
                });
            case "The Tricky":
                choices.add("1. summon normally (tribute 1 monster)");
                choices.add("2. summon by removing a card from your hand");
                int result = 1 + getGameController().getPlayerControllerByPlayer(summoningPlayer).chooseHowToSummon(choices);
                if (result == 1) {
                    checkForTribute(new ConfirmationAction() {
                        @Override
                        public Boolean call() throws Exception {
                            if (choice == null) return false;
                            Boolean isTributePossible = choice.peek();
                            if (isTributePossible == null) return false;
                            if (isTributePossible) summon();
                            return false;
                        }
                    });
                    return;
                } else if (result == 2) {
                    GameCard selectedCardFromHand;
                    if ((selectedCardFromHand = getGameController().getPlayerControllerByPlayer(summoningPlayer).selectCardFromHand(card)) != null) {
                        getGameController().removeMonsterCard(selectedCardFromHand);
                        summon();
                    }
                }
        }
    }

    private void checkForTribute(ConfirmationAction action) {
        ArrayBlockingQueue<Boolean> checkForTribute = new ArrayBlockingQueue<>(1);

        int availableMonsters = summoningPlayer.getField().getAvailableMonstersInZoneCount();
        if (((Monster) card.getCard()).getLevel() == 5 || ((Monster) card.getCard()).getLevel() == 6) {
            if (availableMonsters < 1) {
                logger.info("in game with id {}: can't summon | not enough cards to tribute", gameControllerID);
                GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                checkForTribute.add(Boolean.FALSE);
            }
            tribute(1, new ConfirmationAction() {
                @Override
                public Boolean call() throws Exception {
                    if (choice == null) return false;
                    Boolean isTributePossible = choice.peek();
                    if (isTributePossible == null) return false;
                    if (!isTributePossible) checkForTribute.add(Boolean.FALSE);
                    return false;
                }
            });
        } else if (((Monster) card.getCard()).getLevel() >= 7) {
            if (availableMonsters < 2) {
                logger.info("in game with id {}: can't summon | not enough cards to tribute", gameControllerID);
                GameController.getView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
                checkForTribute.add(Boolean.FALSE);
            }
            tribute(2, new ConfirmationAction() {
                @Override
                public Boolean call() throws Exception {
                    if (choice == null) return false;
                    Boolean isTributePossible = choice.peek();
                    if (isTributePossible == null) return false;
                    if (!isTributePossible) checkForTribute.add(Boolean.FALSE);
                    return false;
                }
            });
        }
        if (checkForTribute.isEmpty()) checkForTribute.add(Boolean.TRUE);

        getGameController().getExecutor().submit(() -> {
            while (checkForTribute.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(checkForTribute);
        getGameController().getExecutor().submit(action);
    }

    public void specialSummon() {
        if (summoningPlayer.getField().getAvailableMonstersInZoneCount() == 5) {
            GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
            return;
        }
        summon();
        if (!summoningPlayer.getField().isInMonsterZone(card))
            return;
        getGameController().applyEffect(Trigger.AFTER_SPECIAL_SUMMON, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                EffectResponse response;
                if ((response = result.peek()) != null && response.equals(EffectResponse.SUMMON_CANT_BE_DONE)) {
                    Utils.printError("you can't special summon this card");
                }
                return null;
            }
        });
    }

    public void ritualSummon() {
        getGameController().getCurrentPlayerController().selectCardsForRitualTribute(((Monster) card.getCard()).getLevel(), new ArraySelectionAction() {
            @Override
            public GameCard[] call() throws Exception {
                if (choices == null) return null;
                GameCard[] cards = choices.peek();
                if (cards == null)
                    return null;
                else if (getGameController().getPlayerByCard(card).getField().getAvailableMonstersInZoneCount() == 5) {
                    GameController.getView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
                    return null;
                }
                for (GameCard gameCard : cards)
                    getGameController().removeMonsterCard(gameCard);
                summon();
                return null;
            }
        });
    }

    public void flipSummon() {
        card.setRevealed(true);
        card.setFaceDown(false);
        getGameTurnController().setChangedPositionMonster(card);
        getGameController().getEffectControllersByPlayer(getGameController().getPlayerByCard(card))
                .add(new EffectController(gameControllerID, card));
        getGameController().applyEffect(Trigger.AFTER_SUMMON, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                return null;
            }
        });
        getGameController().applyEffect(Trigger.AFTER_FLIP_SUMMON, new EffectAction() {
            @Override
            public EffectResponse call() throws Exception {
                return null;
            }
        });
        if (summoningPlayer.getField().isInMonsterZone(card)) {
            GameController.getView().showSuccess(GameView.SUCCESS_FLIP_SUMMON_SUCCESSFUL);
            getGameTurnController().setSummonedMonster(card);
        }
    }

    private void tribute(int amount, ConfirmationAction action) {
        ArrayBlockingQueue<Boolean> choice = new ArrayBlockingQueue<>(1);
        getGameController().getPlayerControllerByPlayer(summoningPlayer).tributeMonster(amount, new ArraySelectionAction() {
            @Override
            public GameCard[] call() throws Exception {
                if (choices == null) return null;
                GameCard[] tributeMonsters = choices.peek();
                if (tributeMonsters == null) return null;
                for (GameCard tributeMonster : tributeMonsters) {
                    if (tributeMonster == null) {
                        choice.add(Boolean.FALSE);
                        return null;
                    }
                }
                for (GameCard tributeMonster : tributeMonsters) {
                    getGameController().removeMonsterCard(tributeMonster);
                }
                choice.add(Boolean.TRUE);
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

    private void moveMonsterToMonsterZone() {
        if (summoningPlayer.getField().isInHand(card))
            summoningPlayer.getField().removeFromHand(card);
        if (summoningPlayer.getField().isInDeck(card))
            summoningPlayer.getField().removeFromDeck(card);
        if (summoningPlayer.getField().isInGraveyard(card))
            summoningPlayer.getField().removeFromGraveyard(card);
        summoningPlayer.getField().putInMonsterZone(card);
    }

    private GameTurnController getGameTurnController() {
        return getGameController().getGameTurnController();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }

}
