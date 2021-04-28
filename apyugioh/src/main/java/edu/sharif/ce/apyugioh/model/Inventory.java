package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.CardType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventory {

    private int money;
    @EqualsAndHashCode.Include
    private String username;
    private Map<String, Integer> cardStock;

    public Inventory() {
        cardStock = new HashMap<>();
        money = 100000;
    }

    public Inventory(String username) {
        this();
        this.username = username;
        DatabaseManager.getInventoryList().add(this);
        DatabaseManager.updateInventoriesToDB();
    }

    public static Inventory getInventoryByUsername(String username) {
        return DatabaseManager.getInventoryList().stream().filter(e -> e.username.equals(username))
                .findAny().orElse(null);
    }

    public void buyCard(String cardName) {
        Card card = DatabaseManager.getCards().getAllCards().stream().filter(e -> e.getName().equals(cardName))
                .findAny().orElse(null);
        if (card != null) {
            int price = DatabaseManager.getCards().getCardPrice(cardName);
            if (money >= price) {
                cardStock.merge(cardName, 1, Integer::sum);
                money -= price;
                DatabaseManager.updateInventoriesToDB();
            }
        }
    }

    public int getCardsCount() {
        return cardStock.values().stream().mapToInt(e -> e).sum();
    }

    public Map<String, Integer> getMonsters() {
        return new TreeMap<>(getCardsByType(CardType.MONSTER));
    }

    public Map<String, Integer> getSpells() {
        return new TreeMap<>(getCardsByType(CardType.SPELL));
    }

    public Map<String, Integer> getTraps() {
        return new TreeMap<>(getCardsByType(CardType.TRAP));
    }

    private Map<String, Integer> getCardsByType(CardType type) {
        Map<String, Integer> cards = new HashMap<>();
        for (Map.Entry<String, Integer> cardEntry : cardStock.entrySet()) {
            if (DatabaseManager.getCards().getCardByName(cardEntry.getKey()).getCardType().equals(type)) {
                cards.put(cardEntry.getKey(), cardEntry.getValue());
            }
        }
        return cards;
    }
}
