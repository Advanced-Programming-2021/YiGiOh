package edu.sharif.ce.apyugioh.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Deck {

    @EqualsAndHashCode.Include
    private int id;
    private String username;
    @EqualsAndHashCode.Include
    private String name;
    private Map<String, Integer> mainDeck;
    private Map<String, Integer> sideDeck;

    public Deck(String username, String name) {
        mainDeck = new HashMap<>();
        sideDeck = new HashMap<>();
        this.username = username;
        this.name = name;
        this.id = DatabaseManager.getDeckList().stream().mapToInt(e -> e.id).max().orElse(0) + 1;
        DatabaseManager.getDeckList().add(this);
        DatabaseManager.updateDecksToDB();
    }

    public static Deck getDeckByID(int id) {
        return DatabaseManager.getDeckList().stream().filter(e -> e.id == id).findAny().orElse(null);
    }

    public static Deck getDeckByName(String username, String deckName) {
        return DatabaseManager.getDeckList().stream().
                filter(e -> e.username.equals(username) && e.name.equals(deckName)).findAny().orElse(null);
    }

    public static List<Deck> getUserDecks(String username) {
        return DatabaseManager.getDeckList().stream().filter(e -> e.username.equals(username)).collect(Collectors.toList());
    }

    public static void remove(int id) {
        DatabaseManager.getDeckList().removeIf(e -> e.id == id);
        DatabaseManager.updateDecksToDB();
    }

    public int getCardTotalCount(String cardName) {
        return getMainDeckCardCount(cardName) + getSideDeckCardCount(cardName);
    }

    public int getMainDeckCardCount(String cardName) {
        return mainDeck.getOrDefault(cardName, 0);
    }

    public int getSideDeckCardCount(String cardName) {
        return sideDeck.getOrDefault(cardName, 0);
    }

    public boolean isMainDeckFull() {
        return mainDeck.values().stream().mapToInt(e -> e).sum() >= 60;
    }

    public boolean isDeckValid() {
        int mainDeckSize = mainDeck.values().stream().mapToInt(e -> e).sum();
        int sideDeckSize = sideDeck.values().stream().mapToInt(e -> e).sum();
        return mainDeckSize >= 40 && mainDeckSize <= 60 && sideDeckSize >= 0 && sideDeckSize <= 15;
    }

    public boolean isSideDeckFull() {
        return sideDeck.values().stream().mapToInt(e -> e).sum() >= 15;
    }

    public void addCardToDeck(String name, boolean isSideDeck) {
        if (isSideDeck) {
            sideDeck.merge(name, 1, Integer::sum);
        } else {
            mainDeck.merge(name, 1, Integer::sum);
        }
        DatabaseManager.updateDecksToDB();
    }

    public void removeCardFromDeck(String name, boolean isSideDeck) {
        if (isSideDeck) {
            sideDeck.merge(name, -1, Integer::sum);
            if (sideDeck.get(name) == 0) sideDeck.remove(name);
        } else {
            mainDeck.merge(name, -1, Integer::sum);
            if (mainDeck.get(name) == 0) mainDeck.remove(name);
        }
        DatabaseManager.updateDecksToDB();
    }
}
