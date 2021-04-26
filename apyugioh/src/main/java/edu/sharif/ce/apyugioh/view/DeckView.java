package edu.sharif.ce.apyugioh.view;

public class DeckView extends View {

    public static final int SUCCESS_DECK_CREATE = 1;
    public static final int SUCCESS_DECK_REMOVE = 2;
    public static final int SUCCESS_DECK_ACTIVATE = 3;
    public static final int SUCCESS_DECK_CARD_ADD = 4;
    public static final int SUCCESS_DECK_CARD_REMOVE = 5;

    public static final int ERROR_DECK_NAME_ALREADY_EXISTS = -1;
    public static final int ERROR_DECK_NAME_NOT_FOUND = -2;
    public static final int ERROR_CARD_NAME_INVALID = -3;
    public static final int ERROR_DECK_FULL = -4;
    public static final int ERROR_CARD_ENOUGH_IN_DECK = -5;
    public static final int ERROR_CARD_NOT_IN_DECK = -6;
    public static final int ERROR_INVENTORY_CARDS_NOT_ENOUGH = -7;

    {
        successMessages.put(SUCCESS_DECK_CREATE, "deck created successfully!");
        successMessages.put(SUCCESS_DECK_REMOVE, "deck deleted successfully!");
        successMessages.put(SUCCESS_DECK_ACTIVATE, "deck activated successfully!");
        successMessages.put(SUCCESS_DECK_CARD_ADD, "card added successfully!");
        successMessages.put(SUCCESS_DECK_CARD_REMOVE, "card removed successfully!");

        errorMessages.put(ERROR_DECK_NAME_ALREADY_EXISTS, "deck with name %s already exists");
        errorMessages.put(ERROR_DECK_NAME_NOT_FOUND, "deck with name %s doesn't exist");
        errorMessages.put(ERROR_CARD_NAME_INVALID, "card with name %s doesn't exist");
        errorMessages.put(ERROR_DECK_FULL, "%s deck is full");
        errorMessages.put(ERROR_CARD_ENOUGH_IN_DECK, "there %s already %s card%s with name %s in deck %s");
        errorMessages.put(ERROR_CARD_NOT_IN_DECK, "card with name %s doesn't exist in %s deck");
        errorMessages.put(ERROR_INVENTORY_CARDS_NOT_ENOUGH, "you don't have enough %s in your inventory");
    }
}
