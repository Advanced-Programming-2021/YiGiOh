package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.MenuState;
import edu.sharif.ce.apyugioh.controller.ProfileController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.view.error.ErrorView;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "profile", mixinStandardHelpOptions = true, description = "profile commands", sortOptions = false)
public class ProfileCommand {

    @Command(name = "change", description = "change your game account nickname or password")
    public void changeNickname(@Option(names = {"-p", "--password"}, paramLabel = "password") boolean isPassword,
                               @Option(names = {"-n", "--nickname"}, paramLabel = "nickname") String nickname,
                               @Option(names = {"-np", "--new"}, paramLabel = "new password") String newPassword,
                               @Option(names = {"-c", "--current"}, paramLabel = "current password") String currentPassword) {
        if (!isAvailable()) return;
        if (isPassword) {
            if (nickname != null || currentPassword == null || newPassword == null) {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return;
            }
            if (!isOptionsValid(currentPassword, newPassword)) return;
            ProfileController.getInstance().changePassword(currentPassword, newPassword);
        } else {
            if (newPassword != null || currentPassword != null || nickname == null) {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return;
            }
            if (!isOptionsValid(nickname)) return;
            ProfileController.getInstance().changeNickname(nickname);
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
