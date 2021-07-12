package edu.sharif.ce.apyugioh.view;

import edu.sharif.ce.apyugioh.controller.Utils;

public class ErrorView {

    public static final int COMMAND_INVALID = 0;
    public static final int CARD_NOT_SELECTED = -1;
    public static final int CARD_NOT_VISIBLE = -2;

    public static void showError(int errorID) {
        switch (errorID) {
            case COMMAND_INVALID:
                Utils.printError("invalid command");
                break;
            case CARD_NOT_SELECTED:
                Utils.printError("no card is selected");
                break;
            case CARD_NOT_VISIBLE:
                Utils.printError("card is not visible");
                break;
        }
    }

}
