package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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
        if (deck.size() > 0) {
            putInHand(deck.get(deck.size() - 1));
            removeFromDeck(deck.get(deck.size() - 1));
            return hand.get(hand.size() - 1);
        }
        return null;
    }

    public void putInMonsterZone(GameCard card) {
        if (!isMonsterZoneFull()) {
            monsterZone[getFirstFreeMonsterZoneIndex()] = card;
        }
    }

    public void putInSpellZone(GameCard card) {
        if (!isSpellZoneFull()) {
            spellZone[getFirstFreeSpellZoneIndex()] = card;
        }
    }

    public void putInFieldZone(GameCard card) {
        if (fieldZone != null) {
            putInGraveyard(fieldZone);
        }
        fieldZone = card;
    }

    public void putInGraveyard(GameCard card) {
        graveyard.add(card);
    }

    public void putInBanished(GameCard card) {
        banished.add(card);
    }

    public void putInHand(GameCard card) {
        hand.add(card);
    }

    public void putInDeck(GameCard card) {
        deck.add(card);
    }

    public void removeFromMonsterZone(GameCard card) {
        for (int i = 0; i < 5; i++) {
            if (monsterZone[i] == null) continue;
            if (monsterZone[i].getId() == card.getId()) {
                monsterZone[i] = null;
                break;
            }
        }
    }

    public void removeFromSpellZone(GameCard card) {
        for (int i = 0; i < 5; i++) {
            if (spellZone[i] == null) continue;
            if (spellZone[i].getId() == card.getId()) {
                spellZone[i] = null;
                break;
            }
        }
    }

    public void removeFromFieldZone(GameCard card) {
        if (isInFieldZone(card)) {
            fieldZone = null;
        }
    }

    public void removeFromGraveyard(GameCard card) {
        graveyard.removeIf(e -> e.getId() == card.getId());
    }

    public void removeFromBanished(GameCard card) {
        banished.removeIf(e -> e.getId() == card.getId());
    }

    public void removeFromHand(GameCard card) {
        hand.removeIf(e -> e.getId() == card.getId());
    }

    public void removeFromDeck(GameCard card) {
        deck.removeIf(e -> e.getId() == card.getId());
    }

    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    public boolean isInMonsterZone(GameCard card) {
        return Arrays.stream(monsterZone).anyMatch(e -> e != null && e.getId() == card.getId());
    }

    public boolean isInSpellZone(GameCard card) {
        return Arrays.stream(spellZone).anyMatch(e -> e != null && e.getId() == card.getId());
    }

    public boolean isInFieldZone(GameCard card) {
        return fieldZone != null && fieldZone.getId() == card.getId();
    }

    public boolean isInGraveyard(GameCard card) {
        return graveyard.stream().anyMatch(e -> e.getId() == card.getId());
    }

    public boolean isInBanished(GameCard card) {
        return banished.stream().anyMatch(e -> e.getId() == card.getId());
    }

    public boolean isInHand(GameCard card) {
        return hand.stream().anyMatch(e -> e.getId() == card.getId());
    }

    public boolean isInDeck(GameCard card) {
        return deck.stream().anyMatch(e -> e.getId() == card.getId());
    }

    public int getAvailableMonstersInZoneCount() {
        return (int) Arrays.stream(monsterZone).filter(Objects::nonNull).count();
    }

    public boolean isHandFull() {
        return hand.size() >= 6;
    }

    public boolean isMonsterZoneFull() {
        for (GameCard monster : monsterZone) {
            if (monster == null) return false;
        }
        return true;
    }

    public boolean isSpellZoneFull() {
        return Arrays.stream(spellZone).noneMatch(Objects::nonNull);
    }

    public boolean isInField(GameCard card) {
        return isInMonsterZone(card) || isInSpellZone(card) || isInFieldZone(card) || isInGraveyard(card) ||
                isInBanished(card) || isInHand(card) || isInDeck(card);
    }

    public int getFirstFreeMonsterZoneIndex() {
        for (int i = 0; i < 5; i++) {
            if (monsterZone[i] == null) return i;
        }
        return -1;
    }

    public int getFirstFreeSpellZoneIndex() {
        for (int i = 0; i < 5; i++) {
            if (spellZone[i] == null) return i;
        }
        return -1;
    }

    public ArrayList<GameCard> getActiveTraps() {
        return null;
    }
}
