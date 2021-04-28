package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.DeckController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "deck", mixinStandardHelpOptions = true, description = "deck commands")
public class DeckCommand {

    @Command(name = "create", description = "create a new deck")
    public void create(@Parameters(index = "0", paramLabel = "deck name", description = "deck name") String name) {
        if (!isAvailable()) return;
        if (!isOptionsValid(name)) return;
        DeckController.getInstance().create(name);
    }

    @Command(name = "delete", description = "delete an existing deck")
    public void delete(@Parameters(index = "0", paramLabel = "deck name", description = "deck name") String name) {
        if (!isAvailable()) return;
        DeckController.getInstance().remove(name);
    }

    @Command(name = "activate", description = "set an existing deck active")
    public void activate(@Parameters(index = "0", paramLabel = "deck name", description = "deck name") String name) {
        if (!isAvailable()) return;
        DeckController.getInstance().activate(name);
    }

    @Command(name = "add-card", description = "add a card to your deck")
    public void addCard(@Option(names = {"-c", "--card"}, description = "card name", paramLabel = "card name") String cardName,
                        @Option(names = {"-d", "--deck"}, description = "deck name", paramLabel = "deck name") String deckName,
                        @Option(names = {"-s", "--side"}, description = "is side deck", paramLabel = "side deck") boolean isSideDeck) {
        if (!isAvailable()) return;
        DeckController.getInstance().addCard(deckName, cardName.replaceAll("_", " ").trim(), isSideDeck);
    }

    @Command(name = "remove-card", description = "removes a card from your deck")
    public void removeCard(@Option(names = {"-c", "--card"}, description = "card name", paramLabel = "card name") String cardName,
                           @Option(names = {"-d", "--deck"}, description = "deck name", paramLabel = "deck name") String deckName,
                           @Option(names = {"-s", "--side"}, description = "is side deck", paramLabel = "side deck") boolean isSideDeck) {
        if (!isAvailable()) return;
        DeckController.getInstance().removeCard(deckName, cardName.replaceAll("_", " ").trim(), isSideDeck);
    }

    @Command(name = "show", description = "show cards command")
    public void show(@Option(names = {"-d", "--deck-name"}, description = "deck name", paramLabel = "deck name") String deckName,
                     @Option(names = {"-s", "--side"}, description = "is side deck", paramLabel = "side deck") boolean isSideDeck,
                     @Option(names = {"-a", "--all"}, description = "is all decks", paramLabel = "all decks") boolean isAll,
                     @Option(names = {"-c", "--cards"}, description = "is all cards", paramLabel = "cards") boolean isCards) {
        if (!isAvailable()) return;
        if (isAll && !isSideDeck && !isCards && deckName == null) {
            DeckController.getInstance().showAllDecks();
        } else if (deckName != null && !isAll && !isCards) {
            DeckController.getInstance().showDeck(deckName, isSideDeck);
        } else if (isCards && !isAll && !isSideDeck && deckName == null) {
            DeckController.getInstance().showAllInventoryCards();
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
        }
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.DECK)) {
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

    private boolean isOptionsValid(String... options) {
        for (String option : options) {
            if (!option.matches("\\w+")) return false;
        }
        return true;
    }

}
