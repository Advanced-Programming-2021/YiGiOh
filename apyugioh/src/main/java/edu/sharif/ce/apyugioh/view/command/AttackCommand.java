package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "attack", mixinStandardHelpOptions = true, description = "duel attack commands")
public class AttackCommand implements Callable<Integer> {

    @Parameters(arity = "0..1", defaultValue = "-1", paramLabel = "position", description = "card position")
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

    @Command(name = "direct", mixinStandardHelpOptions = true, description = "direct attack to opponent")
    public void directAttack() {
        if (!isAvailable()) return;
        ProgramController.getCurrentPlayerController().directAttack();
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
