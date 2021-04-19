package edu.sharif.ce.apyugioh.controller;

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

    @Getter
    @Setter
    private User user;

    public void showScoreboard() {
        List<User> users = DatabaseController.getUserList();
        Collections.sort(users);
        view.showScoreboard(users);
    }

}
