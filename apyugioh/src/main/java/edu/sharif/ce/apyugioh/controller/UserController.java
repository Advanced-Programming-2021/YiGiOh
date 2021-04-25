package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.UserView;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserController {

    @Getter
    private static UserController instance;
    private static UserView view;
    private static Logger logger;

    static {
        instance = new UserController();
        view = new UserView();
        logger = LogManager.getLogger(UserController.class);
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
        User user = new User(username, password, nickname);
        MainMenuController.getInstance().setUser(user);
        view.showSuccess(UserView.SUCCESS_USER_CREATE);
        ProgramController.setState(MenuState.MAIN);
        Utils.clearScreen();
        logger.info("{} registered in with {} : {}", user.getNickname(), username, password);
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
        logger.info("{} logged in with {} : {}", user.getNickname(), username, password);
    }
}
