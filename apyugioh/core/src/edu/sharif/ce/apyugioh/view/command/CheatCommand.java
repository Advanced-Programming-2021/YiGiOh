package edu.sharif.ce.apyugioh.view.command;

import java.util.concurrent.Callable;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.Cheats;

public class CheatCommand implements Callable<Integer> {

    Cheats set;
    String[] options;

    @Override
    public Integer call() {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(set, options);
        return 0;
    }
}
