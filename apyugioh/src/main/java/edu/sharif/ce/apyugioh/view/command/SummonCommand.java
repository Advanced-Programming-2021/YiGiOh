package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "summon", mixinStandardHelpOptions = true, description = "monster summon commands")
public class SummonCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        if (!isAvailable()) return -1;
        ProgramController.getCurrentPlayerController().summon();
        return 0;
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
