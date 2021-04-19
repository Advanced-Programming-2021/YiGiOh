package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.UserController;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "user", mixinStandardHelpOptions = true, description = "game account commands", sortOptions = false)
public class UserCommand {

    @Command(name = "login", description = "login to your game account", sortOptions = false)
    public void login(@Option(names = {"-u", "--username"}, required = true, paramLabel = "username") String username,
                      @Option(names = {"-p", "--password"}, required = true, paramLabel = "password") String password) {
        if (!isAvailable()) return;
        if (!isOptionsValid(username, password)) return;
        UserController.getInstance().loginUser(username, password);
    }

    @Command(name = "create", description = "create a game account", sortOptions = false)
    public void create(@Option(names = {"-u", "--username"}, required = true, paramLabel = "username") String username,
                       @Option(names = {"-p", "--password"}, required = true, paramLabel = "password") String password,
                       @Option(names = {"-n", "--nickname"}, required = true, paramLabel = "nickname") String nickname) {
        if (!isAvailable()) return;
        if (!isOptionsValid(username, password, nickname)) return;
        UserController.getInstance().registerUser(username, password, nickname);
    }

    @Command(name = "logout", description = "logout of your game account")
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
