package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.*;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.view.View;

public class CheatController {

    private int gameControllerID;
    private int fakeID = 0;

    public CheatController(int gameControllerID) {
        this.gameControllerID = gameControllerID;
    }

    public void cheat(Cheats set, String[] options) {
        switch (set) {
            case MONEY_AMOUNT:
                if (isParamsCountInvalid(options, 1)) break;
                setMoney(options[0]);
                break;
            case DRAW_CARD:
                if (isParamsCountInvalid(options, 1)) break;
                drawCard(options[0]);
                break;
            case INSTANT_WIN:
                if (isParamsCountInvalid(options, 0)) break;
                instantWin();
                break;
            case LIFE_POINTS_AMOUNT:
                if (isParamsCountInvalid(options, 1)) break;
                setLifePoints(options[0]);
                break;
            case SUMMON_MONSTER:
                if (isParamsCountInvalid(options, 1)) break;
                forceSummon(options[0], false);
                break;
            case SET_MONSTER:
                if (isParamsCountInvalid(options, 1)) break;
                forceSummon(options[0], true);
                break;
            case SET_SPELL:
                if (isParamsCountInvalid(options, 1)) break;
                forceSpell(options[0]);
                break;
        }
    }

    private boolean isParamsCountInvalid(String[] options, int i) {
        if (options.length != i) {
            Utils.printError("Invalid Command!");
            return true;
        }
        return false;
    }

    private void forceSpell(String option) {
        if (getCurrentPlayer().getField().isSpellZoneFull()) {
            Utils.printError("Spell zone is full!");
            return;
        }
        GameCard card = new GameCard();
        card.setCard(DatabaseManager.getCards().getCardByName(Utils.firstUpperOnly(option)));
        if (card.getCard() == null) {
            Utils.printError("Invalid card name!");
            return;
        }
        if (card.getCard().getCardType().equals(CardType.MONSTER)) {
            Utils.printError("Can't put monsters in spell zone!");
            return;
        }
        card.setId(++fakeID);
        card.setFaceDown(true);
        getGameController().getCurrentPlayerEffectControllers().add(new EffectController(gameControllerID, card));
        getCurrentPlayer().getField().getSpellZone()[getCurrentPlayer().getField().getFirstFreeSpellZoneIndex()] = card;
    }

    private void forceSummon(String option, boolean isSet) {
        if (getCurrentPlayer().getField().isMonsterZoneFull()) {
            Utils.printError("Monster zone is full!");
            return;
        }
        GameCard card = new GameCard();
        card.setCard(DatabaseManager.getCards().getCardByName(Utils.firstUpperOnly(option)));
        if (card.getCard() == null) {
            Utils.printError("Invalid card name!");
            return;
        }
        if (!card.getCard().getCardType().equals(CardType.MONSTER)) {
            Utils.printError("Can't put spells in monster zone!");
            return;
        }
        card.setId(++fakeID);
        card.setFaceDown(isSet);
        card.setRevealed(!isSet);
        getGameController().getCurrentPlayerEffectControllers().add(new EffectController(gameControllerID, card));
        getCurrentPlayer().getField().getMonsterZone()[getCurrentPlayer().getField().getFirstFreeMonsterZoneIndex()] = card;
    }

    private void setLifePoints(String option) {
        try {
            int amount = Integer.parseInt(option);
            getCurrentPlayer().setLifePoints(amount);
        } catch (NumberFormatException e) {
            Utils.printError("Invalid amount");
        }
    }

    private void instantWin() {
        boolean turn = getGameController().isFirstPlayerTurn();
        getGameController().endRound(turn);
        if (ProgramController.getGameControllerID() != -1) {
            getGameController().endRound(turn);
        }
    }

    private void drawCard(String option) {
        GameCard card = getCurrentPlayer().getField().getDeck().stream()
                .filter(e -> e.getCard().getName().equals(Utils.firstUpperOnly(option))).findFirst().orElse(null);
        if (card == null) {
            card = new GameCard();
            card.setCard(DatabaseManager.getCards().getCardByName(Utils.firstUpperOnly(option)));
            card.setId(++fakeID);
        }
        getCurrentPlayer().getField().getHand().add(card);
    }

    private void setMoney(String option) {
        Inventory playerInventory = Inventory.getInventoryByUsername(getCurrentPlayer().getUser().getUsername());
        try {
            int amount = Integer.parseInt(option);
            playerInventory.setMoney(amount);
        } catch (NumberFormatException e) {
            Utils.printError("Invalid amount");
        }
    }

    private View getGameControllerView() {
        return GameController.getView();
    }

    private PlayerController getCurrentPlayerController() {
        return getGameController().getCurrentPlayerController();
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

    private GameTurnController getGameTurnController() {
        return getGameController().getGameTurnController();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }
}
