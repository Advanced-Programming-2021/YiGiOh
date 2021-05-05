package edu.sharif.ce.apyugioh.controller.player;

import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.view.GameView;
import lombok.Getter;
import lombok.Setter;

@Getter
public abstract class PlayerController {

    @Setter
    protected int gameControllerID;
    protected Player player;

    public PlayerController(Player player) {
        this.player = player;
    }

    public void select(CardLocation location) {
        if (location.isFromMonsterZone()) {
            if (location.getPosition() > 4 || location.getPosition() < 0) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_POSITION_INVALID);
            } else if (player.getField().getMonsterZone()[location.getPosition()] == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                GameController.getGameControllerById(gameControllerID).select(location);
            }
            return;
        }
        if (location.isFromSpellZone()) {
            if (location.getPosition() > 4 || location.getPosition() < 0) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_POSITION_INVALID);
            } else if (player.getField().getSpellZone()[location.getPosition()] == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                GameController.getGameControllerById(gameControllerID).select(location);
            }
            return;
        }
        if (location.isFromFieldZone()) {
            if (player.getField().getFieldZone() == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                GameController.getGameControllerById(gameControllerID).select(location);
            }
            return;
        }
        if (location.isInHand()) {
            if (location.getPosition() >= player.getField().getHand().size() || location.getPosition() < 0) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_POSITION_INVALID);
            } else if (player.getField().getHand().get(location.getPosition()) == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                GameController.getGameControllerById(gameControllerID).select(location);
            }
            return;
        }
    }

    public void deselect() {
        GameController.getGameControllerById(gameControllerID).deselect();
    }

    public void set() {

    }

    public void summon() {

    }

    public void nextPhase() {

    }

    public void endRound() {

    }

    public void startRound() {

    }

    public void attack(int position) {

    }

    public void directAttack() {

    }

    public void activeEffect() {

    }

    public void flipSummon() {

    }

    public void surrender() {

    }

    public void cancel() {

    }

    public void exchangeSideDeckCards() {

    }
}
