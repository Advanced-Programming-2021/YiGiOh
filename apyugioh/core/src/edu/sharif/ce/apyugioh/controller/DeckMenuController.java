package edu.sharif.ce.apyugioh.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.menu.DeckMenuView;
import lombok.Getter;
import lombok.Setter;

public class DeckMenuController {

    @Getter
    private static DeckMenuController instance;
    private static Logger logger;

    static{
        instance = new DeckMenuController();
        logger = LogManager.getLogger(DeckMenuController.class);
    }

    @Getter
    @Setter
    private User user;
    private DeckMenuView view;

    private DeckMenuController(){
    }

    public void showDeckMenu(){
        if (view != null)
            view.dispose();
        view = new DeckMenuView(ProgramController.getGame());
        ProgramController.setState(MenuState.DECK);
        ProgramController.setCurrentMenu(view);
    }

    public void back(){
        MainMenuController.getInstance().showMainMenu();
    }

}
