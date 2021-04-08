package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.MenuState;
import edu.sharif.ce.apyugioh.controller.ProfileController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.view.error.ErrorView;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "profile", mixinStandardHelpOptions = true, description = "profile commands",
        commandListHeading = "Commands:%n", sortOptions = false)
public class ProfileCommand {

    @Command(name = "change", description = "change your game account nickname or password")
    public void changeNickname(@Option(names = {"-p", "--password"}, paramLabel = "password", order = 0) boolean isPassword,
                               @Option(names = {"--nickname"}, paramLabel = "nickname", order = 1) String nickname,
                               @Option(names = {"--new"}, paramLabel = "new password", order = 2) String newPassword,
                               @Option(names = {"-n"}, order = 4) String nValue,
                               @Option(names = {"-c", "--current"}, paramLabel = "current password", order = 3) String currentPassword) {
        if (!isAvailable()) return;
        if (isPassword) {
            if (nickname != null || currentPassword == null || ((newPassword == null && nValue == null) || (newPassword != null && nValue != null))) {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return;
            }
            if (newPassword == null) newPassword = nValue;
            if (!isOptionsValid(currentPassword, newPassword)) return;
            ProfileController.getInstance().changePassword(currentPassword, newPassword);
        } else {
            if (newPassword != null || currentPassword != null || ((nickname == null && nValue == null) || (nickname != null && nValue != null))) {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return;
            }
            if (nickname == null) nickname = nValue;
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
