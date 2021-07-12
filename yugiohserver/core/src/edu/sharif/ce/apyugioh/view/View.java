package edu.sharif.ce.apyugioh.view;

import edu.sharif.ce.apyugioh.controller.Utils;

import java.util.HashMap;

public class View {

    public final int COMMAND_INVALID = 0;
    protected HashMap<Integer, String> errorMessages;
    protected HashMap<Integer, String> successMessages;

    {
        successMessages = new HashMap<>();
        errorMessages = new HashMap<>();

        errorMessages.put(COMMAND_INVALID, "invalid command");

    }

    public void showSuccess(int successID, String... values) {
        if (successMessages.containsKey(successID)) {
            Utils.printSuccess(String.format(successMessages.get(successID), values));
        }
    }

    public void showError(int errorID, String... values) {
        if (errorMessages.containsKey(errorID)) {
            Utils.printError(String.format(errorMessages.get(errorID), values));
        }
    }

    public String showSuccessString(int successID, String... values) {
        if (successMessages.containsKey(successID)) {
            return String.format(successMessages.get(successID), values);
        }
        return "";
    }

    public String showErrorString(int errorID, String... values) {
        if (errorMessages.containsKey(errorID)) {
            return String.format(errorMessages.get(errorID), values);
        }
        return "";
    }

}
