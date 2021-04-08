package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.controller.DatabaseController;
import edu.sharif.ce.apyugioh.controller.Utils;
import org.jetbrains.annotations.NotNull;

public class User implements Comparable<User> {

    private String username;
    private String password;
    private String nickname;
    private int score;

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = Utils.hash(password);
        this.nickname = nickname;
        DatabaseController.addUser(this);
    }

    public static User getUserByUsername(String username) {
        return DatabaseController.getUserList().stream().filter(e -> e.username.equals(username)).findFirst()
                .orElse(null);
    }

    public static User getUserByNickname(String nickname) {
        return DatabaseController.getUserList().stream().filter(e -> e.nickname.equals(nickname)).findFirst()
                .orElse(null);
    }

    public boolean isPasswordCorrect(String password) {
        return Utils.hash(password).equals(this.password);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Utils.hash(password);
        DatabaseController.updateUsersToDB();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        DatabaseController.updateUsersToDB();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(@NotNull User o) {
        if (o.score != score) return -Integer.compare(score, o.score);
        return nickname.compareTo(o.nickname);
    }
}
