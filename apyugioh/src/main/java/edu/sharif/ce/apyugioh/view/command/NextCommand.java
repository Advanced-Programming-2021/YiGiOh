package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

@Command(name = "next", mixinStandardHelpOptions = true, description = "next commands")
public class NextCommand {

    @Command(name = "phase", mixinStandardHelpOptions = true, description = "moves to next phase")
    public void nextPhase(@Option(names = {"-e", "--end"}, paramLabel = "end phase", description = "end phase") boolean isEnd) {
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
