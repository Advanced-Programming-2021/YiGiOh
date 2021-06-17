package edu.sharif.ce.apyugioh.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.RegisterMenuView;
import lombok.Getter;
import lombok.Setter;

public class MainMenuController {

    @Getter
    private static MainMenuController instance;
    private static RegisterMenuView view;
    private static Logger logger;

    static {
        instance = new MainMenuController();
        view = new RegisterMenuView(ProgramController.getGame());
        logger = LogManager.getLogger(MainMenuController.class);
    }

    private MainMenuController() {
    }

    @Getter
    @Setter
    private User user;

    public void logout() {
        logger.info("{} logged out", user.getNickname());
        user = null;
        view.showSuccess(RegisterMenuView.SUCCESS_LOGOUT);
        ProgramController.setState(MenuState.LOGIN);
    }

    public void showMainMenu() {
        ProgramController.getGame().setScreen(view);
    }
}
