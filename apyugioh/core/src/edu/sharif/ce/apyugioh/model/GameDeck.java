package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.Card;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class GameDeck {
    private List<Card> mainDeck;
    private List<Card> sideDeck;

    public GameDeck() {
        mainDeck = new ArrayList<>();
        sideDeck = new ArrayList<>();
    }
}
