package edu.sharif.ce.apyugioh.model.card;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Monster extends Card {
    @Getter
    private final int level;
    private final int attackPoints;
    private final int defensePoints;
    private final MonsterAttribute attribute;
    private final MonsterEffect effect;
    private final MonsterSummon summon;
    private final MonsterType type;

    public Monster(String name, String description, int level, int attackPoints, int defensePoints, MonsterAttribute attribute, MonsterType type, MonsterEffect effect) {
        this.name = name;
        this.description = description;
        this.cardType = CardType.MONSTER;
        this.level = level;
        this.attackPoints = attackPoints;
        this.defensePoints = defensePoints;
        this.attribute = attribute;
        this.type = type;
        this.effect = effect;
        if (name.equals("Crab Turtle") || name.equals("Skull Guardian")) {
            summon = MonsterSummon.RITUAL;
        } else {
            summon = level > 4 ? MonsterSummon.TRIBUTE : MonsterSummon.NORMAL;
        }
    }

}
