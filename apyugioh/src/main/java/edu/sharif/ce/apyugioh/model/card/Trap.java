package edu.sharif.ce.apyugioh.model.card;

public class Trap extends Card {

    private SpellProperty property;
    private SpellLimit limit;

    {
        setCardType(CardType.TRAP);
    }

    public SpellProperty getProperty() {
        return property;
    }

    public void setProperty(SpellProperty property) {
        this.property = property;
    }

    public SpellLimit getLimit() {
        return limit;
    }

    public void setLimit(SpellLimit limit) {
        this.limit = limit;
    }
}
