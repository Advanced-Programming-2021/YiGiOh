package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.Cheats;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "cheat", mixinStandardHelpOptions = true, description = "cheat commands")
public class CheatCommand implements Callable<Integer> {

    @Option(names = {"--set", "-s"}, required = true, description = "")
    Cheats set;

    @Parameters(arity = "1..*")
    String[] options;

    @Override
    public Integer call() {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(set, options);
        return 0;
    }
}
