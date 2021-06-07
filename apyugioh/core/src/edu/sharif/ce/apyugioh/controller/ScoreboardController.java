package edu.sharif.ce.apyugioh.controller;

import java.util.List;
import java.util.stream.Collectors;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.ScoreboardView;
import lombok.Getter;
import lombok.Setter;

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
        List<User> users = DatabaseManager.getUserList().stream()
                .filter(e -> !e.getUsername().equals("AIHard") && !e.getUsername().equals("AIMediocre")
                        && !e.getUsername().equals("AIEasy")).sorted().collect(Collectors.toList());
        view.showScoreboard(users);
    }

}
