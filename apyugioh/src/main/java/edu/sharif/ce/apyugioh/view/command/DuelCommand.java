package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.DuelController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "duel", mixinStandardHelpOptions = true, description = "game duel commands")
public class DuelCommand implements Callable<Integer> {

    @Option(names = {"-n", "--new"}, paramLabel = "new", required = true)
    boolean isNew;

    @Option(names = {"-a", "--ai"}, paramLabel = "single player")
    boolean isAI;

    @Option(names = {"-s", "--second-player"}, paramLabel = "second player")
    String secondPlayer;

    @Option(names = {"-r", "--rounds"}, paramLabel = "round count", required = true)
    int rounds;

    @Override
    public Integer call() {
        if (!isAvailable()) return 0;
        if (!isNew) {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return 0;
        }
        if (secondPlayer != null && !isAI) {
            DuelController.getInstance().startMultiplayerDuel(MainMenuController.getInstance().getUser().getUsername(),
                    secondPlayer, rounds);
        } else if (isAI && secondPlayer == null) {

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
