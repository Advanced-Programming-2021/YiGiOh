package edu.sharif.ce.apyugioh.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private User user;
    private Deck deck;
    private Field field;
    private int lifePoints;
}
