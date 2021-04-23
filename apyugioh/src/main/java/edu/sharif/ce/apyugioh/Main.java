package edu.sharif.ce.apyugioh;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.view.ImageToASCII;
import org.fusesource.jansi.AnsiConsole;

public class Main {

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        ProgramController.getInstance().initialize();
    }

}
