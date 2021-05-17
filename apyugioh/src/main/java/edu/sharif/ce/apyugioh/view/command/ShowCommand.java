package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "show", mixinStandardHelpOptions = true, description = "in game show commands")
public class ShowCommand {

    @Command(name = "board", mixinStandardHelpOptions = true, description = "show board")
    public void showBoard() {
        if (!isAvailable()) return;
        GameController.getGameControllerById(ProgramController.getGameControllerID()).showCurrentPlayerBoard();
    }

    @Command(name = "graveyard", mixinStandardHelpOptions = true, description = "show graveyard")
    public void showGraveyard(@Option(names = {"-o", "--opponent"}) boolean isOpponent) {
        if (!isAvailable()) return;
        if (isOpponent) {
            GameController.getGameControllerById(ProgramController.getGameControllerID()).showRivalPlayerGraveyard();
        } else {
            GameController.getGameControllerById(ProgramController.getGameControllerID()).showCurrentPlayerGraveyard();
        }
    }

    @Command(name = "deck", mixinStandardHelpOptions = true, description = "show duel decks")
    public void showDuelDecks() {
        if (!isAvailable() || !GameController.getGameControllerById(ProgramController.getGameControllerID()).isDeckExchange())
            return;
        GameController.getView().showDuelDecks(ProgramController.getCurrentPlayerController().getPlayer().getDeck());
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
