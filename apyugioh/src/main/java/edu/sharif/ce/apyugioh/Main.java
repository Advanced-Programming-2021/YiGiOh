package edu.sharif.ce.apyugioh;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import org.fusesource.jansi.AnsiConsole;

public class Main {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        ProgramController.getInstance().initialize();
    }

}
