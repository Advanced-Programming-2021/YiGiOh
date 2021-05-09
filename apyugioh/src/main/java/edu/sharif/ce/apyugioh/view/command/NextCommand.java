package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import picocli.CommandLine.Command;

@Command(name = "next", mixinStandardHelpOptions = true, description = "next commands")
public class NextCommand {

    @Command(name = "phase", mixinStandardHelpOptions = true, description = "moves to next phase")
    public void nextPhase() {
        if (!isAvailable()) return;
        GameController.getGameControllerById(ProgramController.getGameControllerID()).nextPhase();
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
