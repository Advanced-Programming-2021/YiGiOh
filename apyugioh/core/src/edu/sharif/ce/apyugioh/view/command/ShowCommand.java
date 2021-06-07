package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;

public class ShowCommand {

    public void showBoard() {
        if (!isAvailable()) return;
        GameController.getGameControllerById(ProgramController.getGameControllerID()).showCurrentPlayerBoard();
    }

    public void showGraveyard(boolean isOpponent) {
        if (!isAvailable()) return;
        if (isOpponent) {
            GameController.getGameControllerById(ProgramController.getGameControllerID()).showRivalPlayerGraveyard();
        } else {
            GameController.getGameControllerById(ProgramController.getGameControllerID()).showCurrentPlayerGraveyard();
        }
    }

    public void showDuelDecks() {
        if (!isAvailable() || !GameController.getGameControllerById(ProgramController.getGameControllerID()).isDeckExchange())
            return;
        GameController.getView().showDuelDecks(ProgramController.getCurrentPlayerController().getPlayer().getDeck());
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
