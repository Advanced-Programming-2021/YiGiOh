package edu.sharif.ce.apyugioh.model.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Spell extends Card {

    private final SpellProperty property;
    private final SpellLimit limit;

    public Spell(String name, String description, SpellProperty property, SpellLimit limit) {
        this.name = name;
        this.description = description;
        this.cardType = CardType.SPELL;
        this.property = property;
        this.limit = limit;
    }
}
