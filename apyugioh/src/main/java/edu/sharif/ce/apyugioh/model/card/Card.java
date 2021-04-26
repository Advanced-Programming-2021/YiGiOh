package edu.sharif.ce.apyugioh.model.card;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Card {

    @EqualsAndHashCode.Include
    protected String name;
    protected String number;
    protected String description;
    @EqualsAndHashCode.Include
    protected CardType cardType;
}
