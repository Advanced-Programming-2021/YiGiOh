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

    public GameCard() {
        attackModifier = new ArrayList<>();
        defenceModifier = new ArrayList<>();
    }

    public void addAttackModifier(int amount) {
        attackModifier.add(amount);
    }

    public void addDefenceModifier(int amount) {
        defenceModifier.add(amount);
    }

    public void removeAttackModifier(int amount) {
        attackModifier.remove(Integer.valueOf(amount));
    }

    public void removeDefenceModifier(int amount) {
        defenceModifier.remove(Integer.valueOf(amount));
    }

    public int getCurrentAttack() {
        if (card.getCardType().equals(CardType.MONSTER)) {
            Monster monster = (Monster) card;
            int finalAttack = monster.getAttackPoints();
            for (int modifier : attackModifier) {
                finalAttack += modifier;
            }
            return Math.max(finalAttack, 0);
        }
        return 0;
    }

    public int getCurrentDefense() {
        if (card.getCardType().equals(CardType.MONSTER)) {
            Monster monster = (Monster) card;
            int finalDefense = monster.getDefensePoints();
            for (int modifier : defenceModifier) {
                finalDefense += modifier;
            }
            return Math.max(finalDefense, 0);
        }
        return 0;
    }
}
