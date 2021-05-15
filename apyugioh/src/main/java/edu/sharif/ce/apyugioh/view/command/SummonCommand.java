package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "summon", mixinStandardHelpOptions = true, description = "monster summon command")
public class SummonCommand implements Callable<Integer> {

    @Option(names = {"-f", "--flip"}, description = "card flip", paramLabel = "flip")
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
