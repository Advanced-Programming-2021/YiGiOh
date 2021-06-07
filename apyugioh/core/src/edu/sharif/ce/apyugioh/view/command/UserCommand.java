package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.UserController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class UserCommand {

    public void login(String username, String password) {
        if (!isAvailable()) return;
        if (!isOptionsValid(username, password)) return;
        UserController.getInstance().loginUser(username, password);
    }

    public void create(String username, String password, String nickname) {
        if (!isAvailable()) return;
        if (!isOptionsValid(username, password, nickname)) return;
        UserController.getInstance().registerUser(username, password, nickname);
    }


    public void logout() {
        if (!isLogoutAvailable()) return;
        MainMenuController.getInstance().logout();
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.LOGIN)) {
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

    private boolean isLogoutAvailable() {
        if (ProgramController.getState().equals(MenuState.MAIN)) {
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

    private boolean isOptionsValid(String... options) {
        for (String option : options) {
            if (!option.matches("\\w+")) return false;
        }
        return true;
    }
}
