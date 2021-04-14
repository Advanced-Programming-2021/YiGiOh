package edu.sharif.ce.apyugioh.view;

public class ProfileView extends View {

    public static final int SUCCESS_CHANGE_NICKNAME = 1;
    public static final int SUCCESS_CHANGE_PASSWORD = 2;

    public static final int ERROR_USER_NICKNAME_ALREADY_TAKEN = -1;
    public static final int ERROR_USER_PASSWORD_WRONG = -2;
    public static final int ERROR_USER_PASSWORD_REPEATED = -3;

    {
        successMessages.put(SUCCESS_CHANGE_NICKNAME, "nickname changed successfully!");
        successMessages.put(SUCCESS_CHANGE_PASSWORD, "password changed successfully!");

        errorMessages.put(ERROR_USER_PASSWORD_WRONG, "current password is invalid");
        errorMessages.put(ERROR_USER_PASSWORD_REPEATED, "please enter a new password");
        errorMessages.put(ERROR_USER_NICKNAME_ALREADY_TAKEN, "user with nickname %s already exists");
    }

}
