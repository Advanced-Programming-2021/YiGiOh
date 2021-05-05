package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "set", mixinStandardHelpOptions = true, description = "card set commands")
public class SetCommand implements Callable<Integer> {

    @Option(names = {"-p", "--position"}, description = "set card position", paramLabel = "position")
    String state;

    @Override
    public Integer call() {
        if (!isAvailable()) return -1;
        if (state == null) {
            ProgramController.getCurrentPlayerController().set();
        } else if (state.equalsIgnoreCase("attack") || state.equalsIgnoreCase("defense")) {

        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return -1;
        }
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
