package edu.sharif.ce.apyugioh.model.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardLocation {
    private int position;
    private boolean isInHand;
    private boolean isFromEnemy;
    private boolean isFromMonsterZone;
    private boolean isFromSpellZone;
    private boolean isFromFieldZone;
    private boolean isFromGraveyard;

    @Override
    public String toString() {
        if (isInHand) return (isFromEnemy ? "enemy " : "") + "hand " + position;
        if (isFromMonsterZone) return (isFromEnemy ? "enemy " : "") + "monster zone " + position;
        if (isFromSpellZone) return (isFromEnemy ? "enemy " : "") + "spell zone " + position;
        if (isFromFieldZone) return (isFromEnemy ? "enemy " : "") + "field zone";
        if (isFromGraveyard) return (isFromEnemy ? "enemy " : "") + "graveyard " + position;
        return "nothing selected";
    }

    public static CardLocation getPositionInHand(int position) {
        CardLocation location = new CardLocation();
        location.setPosition(position);
        location.setInHand(true);
        return location;
    }

    public static CardLocation getPositionInMonsterZone(int position) {
        CardLocation location = new CardLocation();
        location.setPosition(position);
        location.setFromMonsterZone(true);
        return location;
    }

    public static CardLocation getPositionInSpellZone(int position) {
        CardLocation location = new CardLocation();
        location.setPosition(position);
        location.setFromSpellZone(true);
        return location;
    }

    public static CardLocation getPositionInFieldZone() {
        CardLocation location = new CardLocation();
        location.setFromFieldZone(true);
        return location;
    }

    public static CardLocation getPositionInGraveyard(int position) {
        CardLocation location = new CardLocation();
        location.setPosition(position);
        location.setFromGraveyard(true);
        return location;
    }
}
