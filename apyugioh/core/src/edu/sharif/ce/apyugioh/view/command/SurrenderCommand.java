package edu.sharif.ce.apyugioh.view.command;

import java.util.concurrent.Callable;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;

public class SurrenderCommand implements Callable<Integer> {

    @Override
    public Integer call() {
        if (!isAvailable()) return -1;
        GameController.getGameControllerById(ProgramController.getGameControllerID()).surrender();
        return 0;
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }
}
