package edu.sharif.ce.apyugioh.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.networking.response.LoginResponse;
import edu.sharif.ce.apyugioh.model.networking.response.RegisterResponse;
import edu.sharif.ce.apyugioh.view.menu.UserMenuView;
import lombok.Getter;

public class UserController {

    @Getter
    private static UserController instance;
    private static UserMenuView view;
    private static Logger logger;

    static {
        instance = new UserController();
        view = new UserMenuView(ProgramController.getGame());
        logger = LogManager.getLogger(UserController.class);
    }

    private UserController() {
    }

    public RegisterResponse registerUser(String username, String password, String nickname) {
        if (User.getUserByUsername(username) != null) {
            return new RegisterResponse(false, view.showErrorString(UserMenuView.ERROR_USER_USERNAME_ALREADY_TAKEN, username));
        }
        if (User.getUserByNickname(nickname) != null) {
            return new RegisterResponse(false, view.showErrorString(UserMenuView.ERROR_USER_NICKNAME_ALREADY_TAKEN, nickname));
        }
        User user = new User(username, password, nickname, "default.png");
        MainMenuController.getInstance().setUser(user);
        logger.info("{} registered in with {} : {}", user.getNickname(), username, password);
        return new RegisterResponse(false, view.showSuccessString(UserMenuView.SUCCESS_USER_CREATE));
    }

    public LoginResponse loginUser(String username, String password) {
        User user = User.getUserByUsername(username);
        if (user == null) {
            return new LoginResponse(false, view.showErrorString(UserMenuView.ERROR_USER_INCORRECT_USERNAME_PASSWORD), null);
        }
        if (!user.isPasswordCorrect(password)) {
            return new LoginResponse(false, view.showErrorString(UserMenuView.ERROR_USER_INCORRECT_USERNAME_PASSWORD), null);
        }
        logger.info("{} logged in with {} : {}", user.getNickname(), username, password);
        return new LoginResponse(true, view.showSuccessString(UserMenuView.SUCCESS_USER_LOGIN), user);
    }

    public void logoutUser() {
        User user = MainMenuController.getInstance().getUser();
        logger.info("{} logged in with {} : {}", user.getNickname(),
                user.getUsername(), user.getPassword());
        showMenu();
    }

    public void showMenu() {
        if (view != null)
            view.dispose();
        view = new UserMenuView(ProgramController.getGame());
        ProgramController.setState(MenuState.LOGIN);
        ProgramController.getGame().setScreen(view);
    }
}
