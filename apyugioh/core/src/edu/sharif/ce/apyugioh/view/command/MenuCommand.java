package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.DeckController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ProfileController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.MenuState;

public class MenuCommand {

    public void exit() {
        switch (ProgramController.getState()) {
            case LOGIN:
                System.exit(0);
            case MAIN:
                MainMenuController.getInstance().logout();
                break;
            case PROFILE:
                ProfileController.getInstance().setUser(null);
                break;
            case SHOP:
                ShopController.getInstance().setUser(null);
                break;
            case DECK:
                DeckController.getInstance().setUser(null);
                break;
        }
        if (!ProgramController.getState().equals(MenuState.LOGIN)) ProgramController.setState(MenuState.MAIN);
    }

    public void enter(MenuState menuState) {
        if (ProgramController.getState().equals(MenuState.MAIN)) {
            if (!(menuState.equals(MenuState.LOGIN) || menuState.equals(MenuState.MAIN))) {
                switch (menuState) {
                    case PROFILE:
                        ProfileController.getInstance().setUser(MainMenuController.getInstance().getUser());
                        break;
                    case SHOP:
                        ShopController.getInstance().setUser(MainMenuController.getInstance().getUser());
                        break;
                    case DECK:
                        DeckController.getInstance().setUser(MainMenuController.getInstance().getUser());
                        break;
                }
                ProgramController.setState(menuState);
            } else if (menuState.equals(MenuState.LOGIN))
                System.out.println("you have to logout first");
            else System.out.println("you are already in main menu");
        } else if (ProgramController.getState().equals(MenuState.LOGIN)) {
            System.out.println("please login first");
        } else {
            System.out.println("menu navigation is not possible");
        }
    }

    public void showCurrent() {
        System.out.println(Utils.firstUpperOnly(ProgramController.getState().name()) + " Menu");
    }


}
