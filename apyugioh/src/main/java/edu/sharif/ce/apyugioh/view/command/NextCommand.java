package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Command;

@Command(name = "next", mixinStandardHelpOptions = true, description = "next commands")
public class NextCommand {

    @Command(name = "phase", mixinStandardHelpOptions = true, description = "moves to next phase")
    public void nextPhase() {
        if (!isAvailable()) return;
        GameController.getGameControllerById(ProgramController.getGameControllerID()).nextPhase();
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.DUEL)) {
            return ProgramController.getGameControllerID() != -1;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

}
