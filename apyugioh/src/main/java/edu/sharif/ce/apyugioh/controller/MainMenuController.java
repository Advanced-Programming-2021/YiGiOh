package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.MainMenuView;

public class MainMenuController {

    private static MainMenuController instance;
    private static MainMenuView view;

    static {
        instance = new MainMenuController();
        view = new MainMenuView();
    }

    private User user;

    public static MainMenuController getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void logout() {
        user = null;
        view.showSuccess(MainMenuView.SUCCESS_LOGOUT);
        ProgramController.setState(MenuState.LOGIN);
    }
}
