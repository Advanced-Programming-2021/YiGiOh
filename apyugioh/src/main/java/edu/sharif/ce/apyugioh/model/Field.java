package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    public GameCard drawCard() {
        return null;
    }

    public boolean isInHand(GameCard card) {
        return true;
    }

    public boolean isFromMonsterZone(GameCard card) {
        return true;
    }

    public boolean isFromSpellZone(GameCard card) {
        return true;
    }

    public boolean isFromFieldZone(GameCard card) {
        return true;
    }

    public boolean isFromGraveyard(GameCard card) {
        return true;
    }

    public boolean isMonsterZoneFull() {
        return monsterZone.length >= 5;
    }

    public boolean isSpellZoneFull() {
        return spellZone.length >= 5;
    }

    public boolean isInField(GameCard card) {
        return true;
    }

    public int getFirstFreeMonsterZone(){
        return -1;
    }

    public ArrayList<GameCard> getActiveTraps() {
        return null;
    }
}
