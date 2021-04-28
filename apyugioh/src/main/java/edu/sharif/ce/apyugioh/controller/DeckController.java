package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.card.*;
import edu.sharif.ce.apyugioh.view.DeckView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

public class DeckController {

    @Getter
    private static DeckController instance;
    private static DeckView view;

    static {
        instance = new DeckController();
        view = new DeckView();
    }

    private DeckController() {
    }

    @Setter
    private User user;

    public void create(String name) {
        Deck deck = Deck.getDeckByName(user.getUsername(), name);
        if (deck == null) {
            new Deck(user.getUsername(), name);
            view.showSuccess(DeckView.SUCCESS_DECK_CREATE);
        } else {
            view.showError(DeckView.ERROR_DECK_NAME_ALREADY_EXISTS, name);
        }
    }

    public void remove(String name) {
        Deck deck = Deck.getDeckByName(user.getUsername(), name);
        if (deck == null) {
            view.showError(DeckView.ERROR_DECK_NAME_NOT_FOUND, name);
        } else {
            Deck.remove(deck.getId());
            if (user.getMainDeckID() == deck.getId()) {
                user.setMainDeckID(-1);
                DatabaseManager.updateUsersToDB();
            }
            view.showSuccess(DeckView.SUCCESS_DECK_REMOVE);
        }
    }

    public void activate(String name) {
        Deck deck = Deck.getDeckByName(user.getUsername(), name);
        if (deck == null) {
            view.showError(DeckView.ERROR_DECK_NAME_NOT_FOUND, name);
        } else {
            user.setMainDeckID(deck.getId());
            view.showSuccess(DeckView.SUCCESS_DECK_ACTIVATE);
            DatabaseManager.updateUsersToDB();
        }
    }

    public void addCard(String deckName, String cardName, boolean isSide) {
        Card card = DatabaseManager.getCards().getCardByName(cardName);
        Deck deck = Deck.getDeckByName(user.getUsername(), deckName);
        if (isCardOrDeckInvalid(cardName, deckName, card, deck)) return;
        if (!isSide) {
            if (deck.isMainDeckFull()) {
                view.showError(DeckView.ERROR_DECK_FULL, "main");
                return;
            }
        } else {
            if (deck.isSideDeckFull()) {
                view.showError(DeckView.ERROR_DECK_FULL, "side");
                return;
            }
        }
        if (isCardAddLimited(card, deck)) return;
        Inventory inventory = DatabaseManager.getInventoryList().stream().filter(e -> e.getUsername()
                .equals(user.getUsername())).findAny().orElse(null);
        if (inventory != null && deck.getCardTotalCount(card.getName()) >= inventory.getCardStock()
                .getOrDefault(card.getName(), 0)) {
            view.showError(DeckView.ERROR_INVENTORY_CARDS_NOT_ENOUGH, card.getName());
            return;
        }
        deck.addCardToDeck(card.getName(), isSide);
        view.showSuccess(DeckView.SUCCESS_DECK_CARD_ADD);
    }

    public void removeCard(String deckName, String cardName, boolean isSide) {
        Card card = DatabaseManager.getCards().getCardByName(cardName);
        Deck deck = Deck.getDeckByName(user.getUsername(), deckName);
        if (isCardOrDeckInvalid(cardName, deckName, card, deck)) return;
        if (!isSide) {
            if (deck.getMainDeckCardCount(card.getName()) <= 0) {
                view.showError(DeckView.ERROR_CARD_NOT_IN_DECK, card.getName(), "main");
                return;
            }
        } else {
            if (deck.getSideDeckCardCount(card.getName()) <= 0) {
                view.showError(DeckView.ERROR_CARD_NOT_IN_DECK, card.getName(), "side");
                return;
            }
        }
        deck.removeCardFromDeck(card.getName(), isSide);
        view.showSuccess(DeckView.SUCCESS_DECK_CARD_REMOVE);
    }

    private boolean isCardAddLimited(Card card, Deck deck) {
        if (card.getCardType().equals(CardType.MONSTER)) {
            if (deck.getCardTotalCount(card.getName()) >= 3) {
                view.showError(DeckView.ERROR_CARD_ENOUGH_IN_DECK, "are", "three", "s", card.getName(), deck.getName());
                return true;
            }
        } else if (card.getCardType().equals(CardType.SPELL)) {
            return isSpellTrapLimitReached(card, deck, ((Spell) card).getLimit());
        } else {
            return isSpellTrapLimitReached(card, deck, ((Trap) card).getLimit());
        }
        return false;
    }

    private boolean isSpellTrapLimitReached(Card card, Deck deck, SpellLimit limit) {
        if (limit.equals(SpellLimit.LIMITED)) {
            if (deck.getCardTotalCount(card.getName()) >= 1) {
                view.showError(DeckView.ERROR_CARD_ENOUGH_IN_DECK, "is", "one", "", card.getName(), deck.getName());
                return true;
            }
        } else {
            if (deck.getCardTotalCount(card.getName()) >= 3) {
                view.showError(DeckView.ERROR_CARD_ENOUGH_IN_DECK, "are", "three", "s", card.getName(), deck.getName());
                return true;
            }
        }
        return false;
    }

    private boolean isCardOrDeckInvalid(String cardName, String deckName, Card card, Deck deck) {
        if (card == null) {
            view.showError(DeckView.ERROR_CARD_NAME_INVALID, cardName);
            return true;
        }
        if (deck == null) {
            view.showError(DeckView.ERROR_DECK_NAME_NOT_FOUND, deckName);
            return true;
        }
        return false;
    }

    public void showAllDecks() {
        if (user.getMainDeckID() != -1) {
            Deck activeDeck = Deck.getDeckByID(user.getMainDeckID());
            List<Deck> deactivatedDecks = Deck.getUserDecks(user.getUsername()).stream()
                    .filter(e -> e.getId() != user.getMainDeckID()).collect(Collectors.toList());
            view.showAll(activeDeck, deactivatedDecks);
        } else {
            view.showAll(null, Deck.getUserDecks(user.getUsername()));
        }
    }

    public void showDeck(String deckName, boolean isSideDeck) {
        Deck deck = Deck.getDeckByName(user.getUsername(), deckName);
        if (deck == null) {
            view.showError(DeckView.ERROR_DECK_NAME_NOT_FOUND, deckName);
        } else {
            view.showDeck(deck, isSideDeck);
        }
    }

    public void showAllInventoryCards() {
        view.showInventory(Inventory.getInventoryByUsername(user.getUsername()));
    }
}
