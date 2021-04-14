package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.ProfileView;

public class ProfileController {

    private static ProfileController instance;
    private static ProfileView view;

    static {
        instance = new ProfileController();
        view = new ProfileView();
    }

    private User user;

    public static ProfileController getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void changeNickname(String nickname) {
        if (User.getUserByNickname(nickname) != null) {
            view.showError(ProfileView.ERROR_USER_NICKNAME_ALREADY_TAKEN, nickname);
            return;
        }
        user.setNickname(nickname);
        view.showSuccess(ProfileView.SUCCESS_CHANGE_NICKNAME);
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (!user.isPasswordCorrect(currentPassword)) {
            view.showError(ProfileView.ERROR_USER_PASSWORD_WRONG);
            return;
        }
        if (user.isPasswordCorrect(newPassword)) {
            view.showError(ProfileView.ERROR_USER_PASSWORD_REPEATED);
            return;
        }
        user.setPassword(newPassword);
        view.showSuccess(ProfileView.SUCCESS_CHANGE_PASSWORD);
    }

}
