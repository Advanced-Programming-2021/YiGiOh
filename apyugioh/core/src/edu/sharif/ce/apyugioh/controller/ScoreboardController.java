package edu.sharif.ce.apyugioh.controller;

import java.util.List;
import java.util.stream.Collectors;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.ScoreboardView;
import edu.sharif.ce.apyugioh.view.menu.ProfileMenuView;
import edu.sharif.ce.apyugioh.view.menu.ScoreboardMenuView;
import lombok.Getter;
import lombok.Setter;

public class ScoreboardController {

    @Getter
    private static ScoreboardController instance;
    private static ScoreboardMenuView view;

    static {
        instance = new ScoreboardController();
        view = new ScoreboardMenuView(ProgramController.getGame());
    }

    private ScoreboardController() {
    }

    @Setter
    @Getter
    private User user;

    public void showScoreboard() {
        if (view != null)
            view.dispose();
        view = new ScoreboardMenuView(ProgramController.getGame());
        ProgramController.setState(MenuState.SCOREBOARD);
        ProgramController.setCurrentMenu(view);
    }

}
