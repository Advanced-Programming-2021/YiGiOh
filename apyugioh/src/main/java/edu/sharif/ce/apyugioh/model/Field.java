package edu.sharif.ce.apyugioh.model;

import java.util.*;
import edu.sharif.ce.apyugioh.model.card.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Field {
    private List<GameCard> deck;
    private List<GameCard> hand;
    private List<GameCard> graveyard;
    private List<GameCard> banished;
    private GameCard[] monsterZone;
    private GameCard[] spellZone;
    private GameCard fieldZone;

    public Field() {
        deck = new ArrayList<>();
        hand = new ArrayList<>();
        graveyard = new ArrayList<>();
        banished = new ArrayList<>();
        monsterZone = new GameCard[5];
        spellZone = new GameCard[5];
    }
}
