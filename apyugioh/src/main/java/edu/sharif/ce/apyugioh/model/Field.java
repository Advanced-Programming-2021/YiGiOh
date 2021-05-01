package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.GameCard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        return hand.stream().anyMatch(e -> e.getId() == card.getId());
    }

    public boolean isFromMonsterZone(GameCard card) {
        return Arrays.stream(monsterZone).anyMatch(e -> e.getId() == card.getId());
    }

    public boolean isFromSpellZone(GameCard card) {
        return Arrays.stream(spellZone).anyMatch(e -> e.getId() == card.getId());
    }

    public boolean isFromFieldZone(GameCard card) {
        return fieldZone.getId() == card.getId();
    }

    public boolean isFromGraveyard(GameCard card) {
        return graveyard.stream().anyMatch(e -> e.getId() == card.getId());
    }

    public boolean isMonsterZoneFull() {
        return Arrays.stream(monsterZone).noneMatch(Objects::nonNull);
    }

    public boolean isSpellZoneFull() {
        return Arrays.stream(spellZone).noneMatch(Objects::nonNull);
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
