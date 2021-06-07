package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;

public class ActivateCommand {

    public void activateEffect() {
        if (!isAvailable()) return;
        ProgramController.getCurrentPlayerController().activeEffect();
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
