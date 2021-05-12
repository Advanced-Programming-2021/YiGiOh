package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import picocli.CommandLine.Command;

@Command(name = "activate", mixinStandardHelpOptions = true, description = "activate commands")
public class ActivateCommand {

    @Command(name = "effect", mixinStandardHelpOptions = true, description = "activate effect")
    public void activateEffect() {
        if (!isAvailable()) return;
        ProgramController.getCurrentPlayerController().activeEffect();
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
