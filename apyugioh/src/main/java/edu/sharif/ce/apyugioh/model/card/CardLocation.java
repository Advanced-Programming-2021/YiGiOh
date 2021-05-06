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
}
