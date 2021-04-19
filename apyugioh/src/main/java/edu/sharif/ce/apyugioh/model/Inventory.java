package edu.sharif.ce.apyugioh.model;

import java.util.HashMap;

public class Inventory {

    private int money;
    private String username;
    private HashMap<String, Integer> cardStock;

    public Inventory() {
        cardStock = new HashMap<>();
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HashMap<String, Integer> getCardStock() {
        return cardStock;
    }

    public void setCardStock(HashMap<String, Integer> cardStock) {
        this.cardStock = cardStock;
    }
}
