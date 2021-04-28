package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "shop", mixinStandardHelpOptions = true, description = "game shop commands")
public class ShopCommand {

    @Command(name = "buy", description = "buy game cards")
    public void buy(@Parameters(index = "0", description = "card name") String name) {
        if (!isAvailable()) return;
        ShopController.getInstance().buyCard(name.replaceAll("_", " ").trim());
    }

    @Command(name = "show", description = "show all cards")
    public void show(@Option(names = {"-a", "--all"}, description = "isAll") boolean isAll) {
        if (!isAvailable()) return;
        ShopController.getInstance().showAllCards();
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.SHOP)) {
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

}
