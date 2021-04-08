package edu.sharif.ce.apyugioh.view;

import edu.sharif.ce.apyugioh.controller.Utils;

public class ProfileView {

    public static final int SUCCESS_CHANGE_NICKNAME = 1;
    public static final int SUCCESS_CHANGE_PASSWORD = 2;

    public static final int ERROR_USER_NICKNAME_ALREADY_TAKEN = -1;
    public static final int ERROR_USER_PASSWORD_WRONG = -2;
    public static final int ERROR_USER_PASSWORD_REPEATED = -3;

    public void showSuccess(int successID) {
        switch (successID) {
            case SUCCESS_CHANGE_NICKNAME:
                Utils.printSuccess("nickname changed successfully!");
                break;
            case SUCCESS_CHANGE_PASSWORD:
                Utils.printSuccess("password changed successfully!");
                break;
        }
    }

    public void showError(int errorID) {
        switch (errorID) {
            case ERROR_USER_PASSWORD_WRONG:
                Utils.printError("current password is invalid");
                break;
            case ERROR_USER_PASSWORD_REPEATED:
                Utils.printError("please enter a new password");
                break;
        }
    }

    public void showParameterizedError(int errorID, String value) {
        switch (errorID) {
            case ERROR_USER_NICKNAME_ALREADY_TAKEN:
                Utils.printError("user with nickname " + value + " already exists\n");
                break;
        }
    }

}
