package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.controller.DatabaseController;
import edu.sharif.ce.apyugioh.model.card.Card;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

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
        DatabaseController.getInventoryList().add(this);
        DatabaseController.updateInventoriesToDB();
    }

    public static Inventory getInventoryByUsername(String username) {
        return DatabaseController.getInventoryList().stream().filter(e -> e.username.equals(username))
                .findAny().orElse(null);
    }

    public void buyCard(String cardName) {
        Card card = DatabaseController.getCards().getAllCards().stream().filter(e -> e.getName().equals(cardName))
                .findAny().orElse(null);
        if (card != null) {
            int price = DatabaseController.getCards().getCardPrice(cardName);
            if (money >= price) {
                cardStock.merge(cardName, 1, Integer::sum);
                money -= price;
                DatabaseController.updateInventoriesToDB();
            }
        }
    }
}
