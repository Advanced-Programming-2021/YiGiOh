package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.MainMenuView;
import lombok.Getter;
import lombok.Setter;

public class MainMenuController {

    @Getter
    private static MainMenuController instance;
    private static MainMenuView view;

    static {
        instance = new MainMenuController();
        view = new MainMenuView();
    }

    @Getter
    @Setter
    private User user;

    public void logout() {
        user = null;
        view.showSuccess(MainMenuView.SUCCESS_LOGOUT);
        ProgramController.setState(MenuState.LOGIN);
        Utils.clearScreen();
    }
}
