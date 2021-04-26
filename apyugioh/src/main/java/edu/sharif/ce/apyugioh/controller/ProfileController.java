package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.view.ProfileView;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfileController {

    @Getter
    private static ProfileController instance;
    private static ProfileView view;
    private static Logger logger;

    static {
        instance = new ProfileController();
        view = new ProfileView();
        logger = LogManager.getLogger(ProfileController.class);
    }

    @Setter
    private User user;

    public void changeNickname(String nickname) {
        if (User.getUserByNickname(nickname) != null) {
            view.showError(ProfileView.ERROR_USER_NICKNAME_ALREADY_TAKEN, nickname);
            return;
        }
        logger.info("{} changed nickname to {}", user.getNickname(), nickname);
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
        logger.info("{} changed password from {} to {}", user.getNickname(), currentPassword, newPassword);
    }

}
