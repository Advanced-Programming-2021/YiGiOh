package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.Card;

import java.util.ArrayList;
import java.util.List;

public class Deck {

    private int id;
    private String username;
    private String name;
    private List<Card> mainDeck;
    private List<Card> sideDeck;

    public Deck() {
        mainDeck = new ArrayList<>();
        sideDeck = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Card> getMainDeck() {
        return mainDeck;
    }

    public void setMainDeck(List<Card> mainDeck) {
        this.mainDeck = mainDeck;
    }

    public List<Card> getSideDeck() {
        return sideDeck;
    }

    public void setSideDeck(List<Card> sideDeck) {
        this.sideDeck = sideDeck;
    }
}
