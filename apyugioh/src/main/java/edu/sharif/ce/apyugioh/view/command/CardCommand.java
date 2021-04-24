package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

@Command(name = "card", mixinStandardHelpOptions = true, description = "card commands")
public class CardCommand {

    @Command(name = "show", description = "show all cards")
    public void show(@Option(names = {"-s", "--selected"}, description = "show selected card") boolean isSelected,
                     @Parameters(index = "0", description = "card name") String name) {
        if (!isAvailable()) return;
        ShopController.getInstance().showCard(name.replaceAll("_", " "));
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.SHOP) || ProgramController.getState().equals(MenuState.DECK)
                || ProgramController.getState().equals(MenuState.DUEL)) {
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

}
