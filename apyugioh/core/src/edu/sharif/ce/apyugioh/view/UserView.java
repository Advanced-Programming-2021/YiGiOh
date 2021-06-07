package edu.sharif.ce.apyugioh.view;

public class UserView extends View {

    public static final int SUCCESS_USER_CREATE = 1;
    public static final int SUCCESS_USER_LOGIN = 2;

    public static final int ERROR_USER_USERNAME_ALREADY_TAKEN = -1;
    public static final int ERROR_USER_NICKNAME_ALREADY_TAKEN = -2;
    public static final int ERROR_USER_INCORRECT_USERNAME_PASSWORD = -3;

    {
        successMessages.put(SUCCESS_USER_CREATE, "user created successfully!");
        successMessages.put(SUCCESS_USER_LOGIN, "user logged in successfully");

        errorMessages.put(ERROR_USER_INCORRECT_USERNAME_PASSWORD, "username and password doesn't match!");
        errorMessages.put(ERROR_USER_USERNAME_ALREADY_TAKEN, "user with username %s already exists");
        errorMessages.put(ERROR_USER_NICKNAME_ALREADY_TAKEN, "user with nickname %s already exists");
    }

}
