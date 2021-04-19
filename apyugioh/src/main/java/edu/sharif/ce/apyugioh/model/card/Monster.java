package edu.sharif.ce.apyugioh.model.card;

public class Monster extends Card {

    private int level;
    private int attackPoints;
    private int defensePoints;
    private MonsterAttribute attribute;
    private MonsterEffect effect;
    private MonsterSummon summon;
    private MonsterType type;

    {
        setCardType(CardType.MONSTER);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAttackPoints() {
        return attackPoints;
    }

    public void setAttackPoints(int attackPoints) {
        this.attackPoints = attackPoints;
    }

    public int getDefensePoints() {
        return defensePoints;
    }

    public void setDefensePoints(int defensePoints) {
        this.defensePoints = defensePoints;
    }

    public MonsterAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(MonsterAttribute attribute) {
        this.attribute = attribute;
    }

    public MonsterEffect getEffect() {
        return effect;
    }

    public void setEffect(MonsterEffect effect) {
        this.effect = effect;
    }

    public MonsterSummon getSummon() {
        return summon;
    }

    public void setSummon(MonsterSummon summon) {
        this.summon = summon;
    }

    public MonsterType getType() {
        return type;
    }

    public void setType(MonsterType type) {
        this.type = type;
    }
}
