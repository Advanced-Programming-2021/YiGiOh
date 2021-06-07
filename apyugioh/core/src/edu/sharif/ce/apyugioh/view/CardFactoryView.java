package edu.sharif.ce.apyugioh.view;

public class CardFactoryView extends View {

    public static final int ERROR_CARD_NAME_INVALID = -1;
    public static final int ERROR_BACKUP_PATH_INVALID = -2;

    public static final int SUCCESS_CARDS_IMPORTED_SUCCESSFULLY = 1;
    public static final int SUCCESS_CARDS_EXPORTED_SUCCESSFULLY = 2;

    {
        errorMessages.put(ERROR_CARD_NAME_INVALID, "%s is not a valid card name");
        errorMessages.put(ERROR_BACKUP_PATH_INVALID, "can't access backup file");

        successMessages.put(SUCCESS_CARDS_IMPORTED_SUCCESSFULLY, "imported cards successfully");
        successMessages.put(SUCCESS_CARDS_EXPORTED_SUCCESSFULLY, "exported cards successfully");
    }
}
