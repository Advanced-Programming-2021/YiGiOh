package edu.sharif.ce.apyugioh.view.command;

import java.util.concurrent.Callable;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.game.GameController;

public class ExchangeCommand implements Callable<Integer> {

    String sideDeckCard;
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
