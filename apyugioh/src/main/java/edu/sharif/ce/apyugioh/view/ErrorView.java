package edu.sharif.ce.apyugioh.view;

import edu.sharif.ce.apyugioh.controller.Utils;

public class ErrorView {

    public static final int COMMAND_INVALID = 0;

    public static void showError(int errorID) {
        switch (errorID) {
            case COMMAND_INVALID:
                Utils.printError("invalid command");
        }
    }

}
