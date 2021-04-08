package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.ScoreboardView;

import java.util.Collections;
import java.util.List;

public class ScoreboardController {

    private static ScoreboardController instance;
    private static ScoreboardView view;

    static {
        instance = new ScoreboardController();
        view = new ScoreboardView();
    }

    private User user;

    public static ScoreboardController getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void showScoreboard() {
        List<User> users = DatabaseController.getUserList();
        Collections.sort(users);
        view.showScoreboard(users);
    }

}
