package edu.sharif.ce.apyugioh.view;

public class GameView extends View {

    public static final int ERROR_SELECTION_CARD_POSITION_INVALID = -1;
    public static final int ERROR_SELECTION_CARD_NOT_FOUND = -2;
    public static final int ERROR_CARD_NOT_SELECTED = -3;
    public static final int ERROR_SELECTION_NOT_IN_HAND = -4;
    public static final int ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE = -5;

    public static final int SUCCESS_SELECTION_SUCCESSFUL = 1;
    public static final int SUCCESS_DESELECTION_SUCCESSFUL = 2;

    {
        errorMessages.put(ERROR_SELECTION_CARD_POSITION_INVALID, "card position invalid");
        errorMessages.put(ERROR_SELECTION_CARD_NOT_FOUND, "no card found in the given position");
        errorMessages.put(ERROR_CARD_NOT_SELECTED, "no card was selected");
        errorMessages.put(ERROR_SELECTION_NOT_IN_HAND, "you can't %s this card");
        errorMessages.put(ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE, "action not allowed in this phase");

        successMessages.put(SUCCESS_SELECTION_SUCCESSFUL, "%s selected successfully");
        successMessages.put(SUCCESS_DESELECTION_SUCCESSFUL, "%s deselected successfully");
    }

}
