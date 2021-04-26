package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.*;
import edu.sharif.ce.apyugioh.model.MenuState;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "menu", mixinStandardHelpOptions = true, description = "menu commands")
public class MenuCommand {

    @Command(name = "exit", description = "exits out of current menu")
    public void exit() {
        ProgramController.setState(MenuState.MAIN);
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
    }

    @Command(name = "enter", description = "enters another menu")
    public void enter(@Parameters(index = "0", description = "menu name: ${COMPLETION-CANDIDATES}") MenuState menuState) {
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
                Utils.clearScreen();
            } else if (menuState.equals(MenuState.LOGIN))
                System.out.println("you have to logout first");
            else System.out.println("you are already in main menu");
        } else if (ProgramController.getState().equals(MenuState.LOGIN)) {
            System.out.println("please login first");
        } else {
            System.out.println("menu navigation is not possible");
        }
    }

    @Command(name = "show-current", description = "displays the current menu")
    public void showCurrent() {
        System.out.println(Utils.firstUpperOnly(ProgramController.getState().name()) + " Menu");
    }


}
