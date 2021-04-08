package edu.sharif.ce.apyugioh.controller;

import picocli.CommandLine.Help.Ansi;

public class ProgramController {

    private static ProgramController instance;
    private static MenuState state;

    static {
        instance = new ProgramController();
        state = MenuState.LOGIN;
    }

    public static ProgramController getInstance() {
        return instance;
    }

    public static MenuState getState() {
        return state;
    }

    public static void setState(MenuState state) {
        ProgramController.state = state;
    }

    public static String getPromptTitle() {
        return Ansi.AUTO.string("@|yellow " + Utils.firstUpperOnly(ProgramController.getState().name()) + " Menu>|@");
    }

    public void initialize() {
        DatabaseController.init();
    }
}
