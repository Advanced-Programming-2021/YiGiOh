package edu.sharif.ce.apyugioh.controller.player;

import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.view.GameView;
import edu.sharif.ce.apyugioh.model.card.GameCard;
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
        if (isZoneSelected(location, location.isFromMonsterZone(), player.getField().getMonsterZone())) return;
        if (isZoneSelected(location, location.isFromSpellZone(), player.getField().getSpellZone())) return;
        if (isFieldZoneSelected(location)) return;
        if (isHandSelected(location)) return;
    }

    private boolean isHandSelected(CardLocation location) {
        if (location.isInHand()) {
            if (location.getPosition() >= player.getField().getHand().size() || location.getPosition() < 0) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_POSITION_INVALID);
            } else if (player.getField().getHand().get(location.getPosition()) == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                getGameController().select(location);
            }
            return true;
        }
        return false;
    }

    private boolean isFieldZoneSelected(CardLocation location) {
        if (location.isFromFieldZone()) {
            if (player.getField().getFieldZone() == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                getGameController().select(location);
            }
            return true;
        }
        return false;
    }

    private boolean isZoneSelected(CardLocation location, boolean isZoneSelected, GameCard[] zone) {
        if (isZoneSelected) {
            if (location.getPosition() > 4 || location.getPosition() < 0) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_POSITION_INVALID);
            } else if (zone[location.getPosition()] == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                getGameController().select(location);
            }
            return true;
        }
        return false;
    }

    public void deselect() {
        getGameController().deselect();
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

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }
}
