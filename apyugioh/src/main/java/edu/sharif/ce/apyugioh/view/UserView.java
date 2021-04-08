package edu.sharif.ce.apyugioh.view;

import edu.sharif.ce.apyugioh.controller.Utils;

public class UserView {

    public static final int SUCCESS_USER_CREATE = 1;
    public static final int SUCCESS_USER_LOGIN = 2;

    public static final int ERROR_USER_USERNAME_ALREADY_TAKEN = -1;
    public static final int ERROR_USER_NICKNAME_ALREADY_TAKEN = -2;
    public static final int ERROR_USER_INCORRECT_USERNAME_PASSWORD = -3;

    public void showSuccess(int successID) {
        switch (successID) {
            case SUCCESS_USER_CREATE:
                Utils.printSuccess("user created successfully!");
                break;
            case SUCCESS_USER_LOGIN:
                Utils.printSuccess("user logged in successfully");
                break;
        }
    }

    public void showError(int errorID) {
        switch (errorID) {
            case ERROR_USER_INCORRECT_USERNAME_PASSWORD:
                Utils.printError("username and password doesn't match!");
        }
    }

    public void showParameterizedError(int errorID, String value) {
        switch (errorID) {
            case ERROR_USER_USERNAME_ALREADY_TAKEN:
                Utils.printError("user with username " + value + " already exists\n");
                break;
            case ERROR_USER_NICKNAME_ALREADY_TAKEN:
                Utils.printError("user with nickname " + value + " already exists\n");
                break;
        }
    }

}
