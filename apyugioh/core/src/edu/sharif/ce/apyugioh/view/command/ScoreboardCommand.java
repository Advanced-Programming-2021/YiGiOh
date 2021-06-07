package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ScoreboardController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class ScoreboardCommand {

    public void show() {
        if (!isAvailable()) return;
        ScoreboardController.getInstance().showScoreboard();
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.SCOREBOARD)) {
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }
}
