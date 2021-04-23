package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.controller.DatabaseController;
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

    public static Inventory getInventoryByUsername(String username) {
        return DatabaseController.getInventoryList().stream().filter(e -> e.username.equals(username))
                .findAny().orElse(null);
    }
}
