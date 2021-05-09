package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Command;

@Command(name = "show", mixinStandardHelpOptions = true, description = "in game show commands")
public class ShowCommand {

    @Command(name = "board", mixinStandardHelpOptions = true, description = "show board")
    public void showBoard() {
        if (!isAvailable()) return;
        GameController.getGameControllerById(ProgramController.getGameControllerID()).showCurrentPlayerBoard();
    }

    @Command(name = "graveyard", mixinStandardHelpOptions = true, description = "show graveyard")
    public void showGraveyard() {
        if (!isAvailable()) return;
        GameController.getGameControllerById(ProgramController.getGameControllerID()).showCurrentPlayerGraveyard();
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
