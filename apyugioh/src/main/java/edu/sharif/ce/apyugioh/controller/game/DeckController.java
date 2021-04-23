package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.User;
import lombok.Getter;
import lombok.Setter;

public class DeckController {
    @Getter
    private static DeckController instance;

    //initialize block
    static {
        instance = new DeckController();
    }

    @Getter
    @Setter
    private User user;

    public void creat(String name) {

    }

    public void delete(String name) {

    }

    public void active(String name) {

    }

    public void addCard(String deckName, String cardName, boolean isSideDeck) {

    }

    public void removeCard(String deckName, String cardName, boolean isSideDeck) {

    }

    public void showAllDecks() {

    }

    public void showAllCards() {

    }

    public void showDeck() {

    }
}
