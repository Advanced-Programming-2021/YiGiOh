package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.ImageToASCII;
import edu.sharif.ce.apyugioh.view.UserView;

public class UserController {

    private static UserController instance;
    private static UserView view;

    static {
        instance = new UserController();
        view = new UserView();
    }

    public static UserController getInstance() {
        return instance;
    }

    public void registerUser(String username, String password, String nickname) {
        if (User.getUserByUsername(username) != null) {
            view.showError(UserView.ERROR_USER_USERNAME_ALREADY_TAKEN, username);
            return;
        }
        if (User.getUserByNickname(nickname) != null) {
            view.showError(UserView.ERROR_USER_NICKNAME_ALREADY_TAKEN, nickname);
            return;
        }
        new User(username, password, nickname);
        view.showSuccess(UserView.SUCCESS_USER_CREATE);
        ProgramController.setState(MenuState.MAIN);
        Utils.clearScreen();
    }

    public void loginUser(String username, String password) {
        User user = User.getUserByUsername(username);
        if (user == null) {
            view.showError(UserView.ERROR_USER_INCORRECT_USERNAME_PASSWORD);
            return;
        }
        if (!user.isPasswordCorrect(password)) {
            view.showError(UserView.ERROR_USER_INCORRECT_USERNAME_PASSWORD);
            return;
        }
        MainMenuController.getInstance().setUser(user);
        view.showSuccess(UserView.SUCCESS_USER_LOGIN);
        ProgramController.setState(MenuState.MAIN);
        Utils.clearScreen();
        Utils.printHorizontalCenter(new ImageToASCII("characters/YamiYugi", (float) 4).getASCII());
    }
}
