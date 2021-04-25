package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.MainMenuView;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainMenuController {

    @Getter
    private static MainMenuController instance;
    private static MainMenuView view;
    private static Logger logger;

    static {
        instance = new MainMenuController();
        view = new MainMenuView();
        logger = LogManager.getLogger(MainMenuController.class);
    }

    @Getter
    @Setter
    private User user;

    public void logout() {
        logger.info("{} logged out", user.getNickname());
        user = null;
        view.showSuccess(MainMenuView.SUCCESS_LOGOUT);
        ProgramController.setState(MenuState.LOGIN);
        Utils.clearScreen();
    }
}
