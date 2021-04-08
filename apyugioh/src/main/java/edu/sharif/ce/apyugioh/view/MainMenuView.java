package edu.sharif.ce.apyugioh.view;

import edu.sharif.ce.apyugioh.controller.Utils;

public class MainMenuView {

    public static final int SUCCESS_LOGOUT = 1;

    public void showSuccess(int successID) {
        switch (successID) {
            case SUCCESS_LOGOUT:
                Utils.printSuccess("user logged out successfully!");
                break;
        }
    }
}
