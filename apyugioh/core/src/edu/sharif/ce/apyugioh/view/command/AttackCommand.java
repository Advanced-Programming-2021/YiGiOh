package edu.sharif.ce.apyugioh.view.command;

import java.util.concurrent.Callable;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class AttackCommand implements Callable<Integer> {

    int position;

    @Override
    public Integer call() {
        if (!isAvailable()) return -1;
        if (position > 0 && position < 6) {
            ProgramController.getCurrentPlayerController().attack(position);
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return -1;
        }
        return 0;
    }

    public void directAttack() {
        if (!isAvailable()) return;
        ProgramController.getCurrentPlayerController().directAttack();
    }

    private boolean isAvailable() {
        return isDuelCommandsAvailable();
    }

    static boolean isDuelCommandsAvailable() {
        if (ProgramController.getState().equals(MenuState.DUEL)) {
            if (ProgramController.getGameControllerID() == -1) {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return false;
            }
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }
}
