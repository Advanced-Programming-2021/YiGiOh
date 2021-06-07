package edu.sharif.ce.apyugioh.view.command;

import java.util.concurrent.Callable;

import edu.sharif.ce.apyugioh.controller.DuelController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.AILevel;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class DuelCommand implements Callable<Integer> {

    boolean isNew;
    AILevel AILevel;
    AILevel secondAILevel;
    String secondPlayer;
    int rounds;

    @Override
    public Integer call() {
        if (!isAvailable()) return 0;
        if (!isNew) {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return 0;
        }
        if (secondPlayer != null && AILevel == null && secondAILevel == null) {
            DuelController.getInstance().startMultiplayerDuel(MainMenuController.getInstance().getUser().getUsername(),
                    secondPlayer, rounds);
        } else if (AILevel != null && secondPlayer == null && secondAILevel == null) {
            DuelController.getInstance().startSinglePlayerDuel(MainMenuController.getInstance().getUser().getUsername(),
                    AILevel, rounds);
        } else if (AILevel != null && secondAILevel != null && secondPlayer == null) {
            DuelController.getInstance().startNoPlayerDuel(AILevel, secondAILevel, rounds);
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
        }
        return 1;
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.DUEL)) {
            if (ProgramController.getGameControllerID() != -1) {
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
