package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.MenuState;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ScoreboardController;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Command;

@Command(name = "scoreboard", mixinStandardHelpOptions = true, description = "scoreboard commands",
        commandListHeading = "Commands:%n")
public class ScoreboardCommand {

    @Command(name = "show", description = "displays the scoreboard")
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
