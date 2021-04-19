package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.Card;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Deck {

    @EqualsAndHashCode.Include
    private int id;
    private String username;
    @EqualsAndHashCode.Include
    private String name;
    private List<Card> mainDeck;
    private List<Card> sideDeck;

    public Deck() {
        mainDeck = new ArrayList<>();
        sideDeck = new ArrayList<>();
    }
}
