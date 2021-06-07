package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.DeckController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class DeckCommand {

    public void create(String name) {
        if (!isAvailable()) return;
        if (!isOptionsValid(name)) return;
        DeckController.getInstance().create(name);
    }

    public void delete(String name) {
        if (!isAvailable()) return;
        DeckController.getInstance().remove(name);
    }

    public void activate(String name) {
        if (!isAvailable()) return;
        DeckController.getInstance().activate(name);
    }

    public void addCard(String cardName, String deckName, boolean isSideDeck) {
        if (!isAvailable()) return;
        DeckController.getInstance().addCard(deckName, cardName.replaceAll("_", " ").trim(), isSideDeck);
    }

    public void removeCard(String cardName, String deckName, boolean isSideDeck) {
        if (!isAvailable()) return;
        DeckController.getInstance().removeCard(deckName, cardName.replaceAll("_", " ").trim(), isSideDeck);
    }

    public void show(String deckName, boolean isSideDeck, boolean isAll, boolean isCards) {
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
