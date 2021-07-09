package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.menu.CardFactoryMenu;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CardFactoryMenuController {
    @Getter
    private static CardFactoryMenuController instance;
    private static Logger logger;

    static{
        instance = new CardFactoryMenuController();
        logger = LogManager.getLogger(CardFactoryMenuController.class);
    }

    @Getter
    @Setter
    private User user;
    private CardFactoryMenu view;

    private CardFactoryMenuController(){
    }

    public void showCardFactoryMenu(){
        if (view != null)
            view.dispose();
        view = new CardFactoryMenu(ProgramController.getGame());
        ProgramController.setState(MenuState.CARD_FACTORY);
        ProgramController.setCurrentMenu(view);
    }

    public void back(){
        view.dispose();
        view = null;
        MainMenuController.getInstance().showMainMenu();
    }
}
