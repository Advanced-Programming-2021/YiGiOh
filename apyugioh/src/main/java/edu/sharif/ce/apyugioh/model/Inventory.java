package edu.sharif.ce.apyugioh.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Inventory {

    private int money;
    @EqualsAndHashCode.Include
    private String username;
    private HashMap<String, Integer> cardStock;

    public Inventory() {
        cardStock = new HashMap<>();
        money = 100000;
    }
}
