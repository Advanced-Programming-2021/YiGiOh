package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {
    private User user;
    private GameDeck deck;
    private Field field;
    private int lifePoints;

    public void resetField() {
        field = new Field();
        List<GameCard> gameDeck = new ArrayList<>();
        int idCounter = 1;
        for (Card card : deck.getMainDeck()) {
            GameCard gameCard = new GameCard();
            gameCard.setCard(card);
            gameCard.setId(Integer.parseInt(user.getMainDeckID() + ("00" + idCounter)));
            gameDeck.add(gameCard);
            idCounter++;
        }
        field.setDeck(gameDeck);
        field.shuffleDeck();
    }

    public void increaseLifePoints(int amount) {
        lifePoints += amount;
    }

    public void decreaseLifePoints(int amount) {
        lifePoints = lifePoints > amount ? lifePoints - amount : 0;
    }
}
