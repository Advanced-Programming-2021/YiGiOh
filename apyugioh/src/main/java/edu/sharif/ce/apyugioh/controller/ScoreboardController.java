package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.ScoreboardView;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

public class ScoreboardController {

    @Getter
    private static ScoreboardController instance;
    private static ScoreboardView view;

    static {
        instance = new ScoreboardController();
        view = new ScoreboardView();
    }

    private ScoreboardController() {
    }

    @Setter
    private User user;

    public void showScoreboard() {
        List<User> users = DatabaseManager.getUserList();
        Collections.sort(users);
        view.showScoreboard(users);
    }

}
