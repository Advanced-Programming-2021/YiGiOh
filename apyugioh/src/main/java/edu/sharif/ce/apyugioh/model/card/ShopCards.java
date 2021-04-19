package edu.sharif.ce.apyugioh.model.card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopCards {

    private HashMap<Monster, Integer> monsters;
    private HashMap<Spell, Integer> spells;
    private HashMap<Trap, Integer> traps;

    {
        monsters = new HashMap<>();
        spells = new HashMap<>();
        traps = new HashMap<>();
    }

    public ShopCards() {
    }


    public void addMonster(Monster monster, int price) {
        monsters.put(monster, price);
    }

    public void addMonsters(Map<Monster, Integer> monsters) {
        this.monsters.putAll(monsters);
    }

    public List<Monster> getMonsterList() {
        return new ArrayList<>(monsters.keySet());
    }

    public int getMonsterPrice(String monsterName) {
        Monster monster = new ArrayList<>(monsters.keySet()).stream().filter(e -> e.getName().equals(monsterName)).findAny().orElse(null);
        if (monster != null) return monsters.get(monster);
        return -1;
    }

    public void addSpell(Spell spell, int price) {
        spells.put(spell, price);
    }

    public void addSpells(Map<Spell, Integer> spells) {
        this.spells.putAll(spells);
    }

    public List<Spell> getSpellList() {
        return new ArrayList<>(spells.keySet());
    }

    public int getSpellPrice(String spellName) {
        Spell spell = new ArrayList<>(spells.keySet()).stream().filter(e -> e.getName().equals(spellName)).findAny().orElse(null);
        if (spell != null) return spells.get(spell);
        return -1;
    }

    public void addTrap(Trap trap, int price) {
        traps.put(trap, price);
    }

    public void addTraps(Map<Trap, Integer> traps) {
        this.traps.putAll(traps);
    }

    public List<Trap> getTrapList() {
        return new ArrayList<>(traps.keySet());
    }

    public int getTrapPrice(String trapName) {
        Trap trap = new ArrayList<>(traps.keySet()).stream().filter(e -> e.getName().equals(trapName)).findAny().orElse(null);
        if (trap != null) return traps.get(trap);
        return -1;
    }

    public List<Card> getAllCards() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(monsters.keySet());
        cards.addAll(spells.keySet());
        cards.addAll(traps.keySet());
        return cards;
    }

}
