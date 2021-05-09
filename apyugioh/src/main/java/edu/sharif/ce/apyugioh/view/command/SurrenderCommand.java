package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "surrender", mixinStandardHelpOptions = true, description = "surrender to your opponent")
public class SurrenderCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        GameController.getGameControllerById(ProgramController.getGameControllerID()).surrender();
        return 0;
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }
}
