package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProfileController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class ProfileCommand {

    public void change(boolean isPassword, String username, String nickname, String newPassword, String currentPassword) {
        if (!isAvailable()) return;
        if (isPassword) {
            if (nickname != null || username != null || currentPassword == null || newPassword == null) {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return;
            }
            if (!isOptionsValid(currentPassword, newPassword)) return;
            ProfileController.getInstance().changePassword(currentPassword, newPassword);
        } else {
            if (nickname != null && username == null && newPassword == null && currentPassword == null) {
                if (!isOptionsValid(nickname)) return;
                ProfileController.getInstance().changeNickname(nickname);
            } else if (username != null && nickname == null && newPassword == null && currentPassword == null) {
                if (!isOptionsValid(username)) return;
                ProfileController.getInstance().changeUsername(username);
            } else {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return;
            }
        }
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.PROFILE)) {
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
