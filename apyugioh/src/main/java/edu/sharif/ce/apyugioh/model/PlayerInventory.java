package edu.sharif.ce.apyugioh.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PlayerInventory {
    private int money;
    private Map<String, Integer> cardsStock;
    private String username;

    public PlayerInventory() {
        cardsStock = new HashMap<>();
    }
}
