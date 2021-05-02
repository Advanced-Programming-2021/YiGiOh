package edu.sharif.ce.apyugioh.model.card;

import edu.sharif.ce.apyugioh.model.Effects;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Card {
    @EqualsAndHashCode.Include
    protected String name;
    protected String number;
    protected String description;
    @EqualsAndHashCode.Include
    protected CardType cardType;
    protected List<Effects> cardEffects;
}
