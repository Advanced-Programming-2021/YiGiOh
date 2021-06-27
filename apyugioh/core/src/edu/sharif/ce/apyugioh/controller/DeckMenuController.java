package edu.sharif.ce.apyugioh.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.Inventory;
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
    private Deck selectedDeck;
    private Inventory userInventory;
    private ArrayList<Deck> userDecks;

    private DeckMenuController(){
    }

    public DeckMenuView getView() {
        return view;
    }

    public void preset(){
        userDecks = new ArrayList<>();

    }

    public void showDeckMenu(){
        if (view != null)
            view.dispose();
        view = new DeckMenuView(ProgramController.getGame());
        ProgramController.setState(MenuState.DECK);
        ProgramController.setCurrentMenu(view);
    }

    public void back(){
        view.dispose();
        view = null;
        MainMenuController.getInstance().showMainMenu();
    }

}
