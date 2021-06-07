package edu.sharif.ce.apyugioh.view;

public class DuelView extends View {

    public static final int ERROR_USERNAME_INVALID = -1;
    public static final int ERROR_ACTIVE_DECK_NOT_SET = -2;
    public static final int ERROR_DECK_INVALID = -3;
    public static final int ERROR_ROUNDS_INVALID = -4;
    public static final int ERROR_USERNAME_SAME = -5;

    {
        errorMessages.put(ERROR_USERNAME_SAME, "second player can't be the same player as the first one");
        errorMessages.put(ERROR_USERNAME_INVALID, "there is no player with username %s");
        errorMessages.put(ERROR_ACTIVE_DECK_NOT_SET, "%s has no active deck");
        errorMessages.put(ERROR_DECK_INVALID, "%s's deck is invalid");
        errorMessages.put(ERROR_ROUNDS_INVALID, "%s number of rounds is not supported");
    }

}
