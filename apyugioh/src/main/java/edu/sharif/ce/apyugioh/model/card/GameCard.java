package edu.sharif.ce.apyugioh.model.card;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class GameCard {
    private Card card;
    private List<Integer> attackModifier;
    private List<Integer> defenceModifier;
    private boolean isFaceDown;
    private boolean isRevealed;
    private int id;

    public GameCard(){
        attackModifier = new ArrayList<>();
        defenceModifier = new ArrayList<>();
    }
}
