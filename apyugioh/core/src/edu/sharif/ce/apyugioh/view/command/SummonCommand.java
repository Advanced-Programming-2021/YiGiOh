package edu.sharif.ce.apyugioh.view.command;

import java.util.concurrent.Callable;

import edu.sharif.ce.apyugioh.controller.ProgramController;

public class SummonCommand implements Callable<Integer> {

    boolean isFlip;

    @Override
    public Integer call() {
        if (!isAvailable()) return -1;
        if (!isFlip) {
            ProgramController.getCurrentPlayerController().summon();
        } else {
            ProgramController.getCurrentPlayerController().flipSummon();
        }
        return 0;
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
