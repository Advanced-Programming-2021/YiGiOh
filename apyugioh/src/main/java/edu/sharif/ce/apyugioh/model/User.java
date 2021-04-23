package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.controller.DatabaseController;
import edu.sharif.ce.apyugioh.controller.Utils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User implements Comparable<User> {

    @Getter
    @EqualsAndHashCode.Include
    private final String username;
    private String password;
    @Getter
    @EqualsAndHashCode.Include
    private String nickname;
    @Getter
    @Setter
    private int score;
    @Getter
    @Setter
    private int mainDeckID;

    public User(String username, String password, String nickname) {
        this.username = username;
        this.password = Utils.hash(password);
        this.nickname = nickname;
        mainDeckID = -1;
        DatabaseController.addUser(this);
        new Inventory(username);
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

    public void setPassword(String password) {
        this.password = Utils.hash(password);
        DatabaseController.updateUsersToDB();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        DatabaseController.updateUsersToDB();
    }

    @Override
    public int compareTo(@NotNull User o) {
        if (o.score != score) return -Integer.compare(score, o.score);
        return nickname.compareTo(o.nickname);
    }
}
