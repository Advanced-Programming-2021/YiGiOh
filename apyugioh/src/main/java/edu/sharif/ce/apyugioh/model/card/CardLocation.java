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
}
