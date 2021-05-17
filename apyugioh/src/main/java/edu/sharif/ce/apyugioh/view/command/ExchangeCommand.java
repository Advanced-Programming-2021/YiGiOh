package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "exchange", mixinStandardHelpOptions = true, description = "in game show commands")
public class ExchangeCommand implements Callable<Integer> {

    @Parameters(index = "0", arity = "1", paramLabel = "side deck card")
    String sideDeckCard;

    @Parameters(index = "1", arity = "0..1", paramLabel = "main deck card")
    String mainDeckCard;

    @Override
    public Integer call() throws Exception {
        if (GameController.getGameControllerById(ProgramController.getGameControllerID()).isDeckExchange()) {
            if (sideDeckCard.equalsIgnoreCase("done") && mainDeckCard == null) {
                GameController.getGameControllerById(ProgramController.getGameControllerID()).nextPlayerExchangeStart();
            } else if (mainDeckCard != null) {
                ProgramController.getCurrentPlayerController().exchange(Utils.firstUpperOnly(sideDeckCard), Utils.firstUpperOnly(mainDeckCard));
            }
            return 0;
        }
        return -1;
    }
}
