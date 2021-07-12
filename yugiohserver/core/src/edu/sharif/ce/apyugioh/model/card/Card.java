package edu.sharif.ce.apyugioh.model.card;

import edu.sharif.ce.apyugioh.model.Effects;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Card {
    @EqualsAndHashCode.Include
    @Setter
    protected String name;
    protected String number;
    protected String description;
    @EqualsAndHashCode.Include
    protected CardType cardType;
    @Setter
    protected List<Effects> cardEffects;

}
