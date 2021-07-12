package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.view.menu.MainMenuView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.menu.UserMenuView;
import lombok.Getter;
import lombok.Setter;

public class MainMenuController {

    @Getter
    private static MainMenuController instance;
    @Getter
    private static MainMenuView view;
    private static Logger logger;

    static {
        instance = new MainMenuController();
        view = new MainMenuView(ProgramController.getGame());
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
        view.showSuccess(UserMenuView.SUCCESS_LOGOUT);
        ProgramController.setState(MenuState.LOGIN);
    }

    public void showMainMenu(){
        if (view != null)
            view.dispose();
        view = new MainMenuView(ProgramController.getGame());
        ProgramController.setState(MenuState.MAIN);
        ProgramController.setCurrentMenu(MainMenuController.getView());
    }

}
