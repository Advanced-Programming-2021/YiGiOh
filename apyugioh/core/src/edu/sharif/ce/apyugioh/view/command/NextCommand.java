package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;

public class NextCommand {

    public void nextPhase(boolean isEnd) {
        if (!isAvailable()) return;
        if (isEnd) {
            GameController.getGameControllerById(ProgramController.getGameControllerID()).endPhase();
        } else {
            GameController.getGameControllerById(ProgramController.getGameControllerID()).nextPhase();
        }
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
