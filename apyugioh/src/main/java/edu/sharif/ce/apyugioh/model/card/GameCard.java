package edu.sharif.ce.apyugioh.model.card;

import edu.sharif.ce.apyugioh.model.Modifier;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class GameCard {
    private Card card;
    private List<Modifier> attackModifier;
    private List<Modifier> defenceModifier;
    private boolean isFaceDown;
    private boolean isRevealed;
    private int id;

    public GameCard() {
        attackModifier = new ArrayList<>();
        defenceModifier = new ArrayList<>();
    }

    public void addAttackModifier(int amount, boolean isDisposableEachTurn) {
        attackModifier.add(new Modifier(amount,  isDisposableEachTurn));
    }

    public void addAttackModifier(int amount, GameCard effectCard, boolean isDisposableEachTurn) {
        attackModifier.add(new Modifier(amount, effectCard, isDisposableEachTurn));
    }

    public void addDefenceModifier(int amount, boolean isDisposableEachTurn) {
        defenceModifier.add(new Modifier(amount,  isDisposableEachTurn));
    }

    public void addDefenceModifier(int amount, GameCard effectCard, boolean isDisposableEachTurn) {
        defenceModifier.add(new Modifier(amount, effectCard, isDisposableEachTurn));
    }

    public int getCurrentAttack() {
        if (card.getCardType().equals(CardType.MONSTER)) {
            Monster monster = (Monster) card;
            int finalAttack = monster.getAttackPoints();
            for (Modifier modifier : attackModifier) {
                finalAttack += modifier.getAmount();
            }
            return Math.max(finalAttack, 0);
        }
        return 0;
    }

    public int getCurrentDefense() {
        if (card.getCardType().equals(CardType.MONSTER)) {
            Monster monster = (Monster) card;
            int finalDefense = monster.getDefensePoints();
            for (Modifier modifier : defenceModifier) {
                finalDefense += modifier.getAmount();
            }
            return Math.max(finalDefense, 0);
        }
        return 0;
    }
}
