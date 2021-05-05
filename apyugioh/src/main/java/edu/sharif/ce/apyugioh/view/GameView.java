package edu.sharif.ce.apyugioh.view;

public class GameView extends View {

    public static final int ERROR_SELECTION_CARD_POSITION_INVALID = -1;
    public static final int ERROR_SELECTION_CARD_NOT_FOUND = -2;

    {
        errorMessages.put(ERROR_SELECTION_CARD_POSITION_INVALID, "card position invalid");
        errorMessages.put(ERROR_SELECTION_CARD_NOT_FOUND, "no card found in the given position");
    }

}
