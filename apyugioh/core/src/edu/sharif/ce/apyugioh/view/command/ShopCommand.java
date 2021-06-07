package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class ShopCommand {

    public void buy(String name) {
        if (!isAvailable()) return;
        ShopController.getInstance().buyCard(name.replaceAll("_", " ").trim());
    }

    public void show(boolean isAll) {
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
