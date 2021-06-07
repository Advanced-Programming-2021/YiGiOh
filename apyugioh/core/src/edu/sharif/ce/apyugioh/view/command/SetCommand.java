package edu.sharif.ce.apyugioh.view.command;

import java.util.concurrent.Callable;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class SetCommand implements Callable<Integer> {

    String state;

    @Override
    public Integer call() {
        if (!isAvailable()) return -1;
        if (state == null) {
            ProgramController.getCurrentPlayerController().set();
        } else if (state.equalsIgnoreCase("attack") || state.equalsIgnoreCase("defense")) {
            ProgramController.getCurrentPlayerController().changePosition(state.equalsIgnoreCase("attack"));
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return -1;
        }
        return 0;
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
