package edu.sharif.ce.apyugioh.model;

import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.Monster;
import edu.sharif.ce.apyugioh.model.card.Spell;
import edu.sharif.ce.apyugioh.model.card.Trap;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class ShopCards {

    private Set<Monster> monsters;
    private Set<Spell> spells;
    private Set<Trap> traps;
    private Map<String, Integer> monsterPrices;
    private Map<String, Integer> spellPrices;
    private Map<String, Integer> trapPrices;

    public ShopCards() {
        monsters = new HashSet<>();
        monsterPrices = new HashMap<>();
        spells = new HashSet<>();
        spellPrices = new HashMap<>();
        traps = new HashSet<>();
        trapPrices = new HashMap<>();
    }

    public void addMonster(Monster monster, int price) {
        if (monsters.stream().noneMatch(e -> e.equals(monster))) {
            monsters.add(monster);
            monsterPrices.put(monster.getName(), price);
        }
    }

    public void addMonsters(List<Monster> monsters, Map<String, Integer> monsterPrices) {
        this.monsters.addAll(monsters);
        this.monsterPrices.putAll(monsterPrices);
    }

    public int getMonsterPrice(String monsterName) {
        Monster monster = monsters.stream().filter(e -> e.getName().equalsIgnoreCase(monsterName)).findAny().orElse(null);
        if (monster != null) {
            return monsterPrices.get(monster.getName());
        }
        return -1;
    }

    public void addSpell(Spell spell, int price) {
        if (spells.stream().noneMatch(e -> e.equals(spell))) {
            spells.add(spell);
            spellPrices.put(spell.getName(), price);
        }
    }

    public void addSpells(List<Spell> spells, Map<String, Integer> spellPrices) {
        this.spells.addAll(spells);
        this.spellPrices.putAll(spellPrices);
    }

    public int getSpellPrice(String spellName) {
        Spell spell = spells.stream().filter(e -> e.getName().equalsIgnoreCase(spellName)).findAny().orElse(null);
        if (spell != null) {
            return spellPrices.get(spell.getName());
        }
        return -1;
    }

    public void addTrap(Trap trap, int price) {
        if (traps.stream().noneMatch(e -> e.equals(trap))) {
            traps.add(trap);
            trapPrices.put(trap.getName(), price);
        }
    }

    public void addTraps(List<Trap> traps, Map<String, Integer> trapPrices) {
        this.traps.addAll(traps);
        this.trapPrices.putAll(trapPrices);
    }

    public int getTrapPrice(String trapName) {
        Trap trap = traps.stream().filter(e -> e.getName().equalsIgnoreCase(trapName)).findAny().orElse(null);
        if (trap != null) {
            return trapPrices.get(trap.getName());
        }
        return -1;
    }

    public List<Card> getAllCards() {
        ArrayList<Card> cards = new ArrayList<>();
        cards.addAll(monsters);
        cards.addAll(spells);
        cards.addAll(traps);
        return cards;
    }

    public String[] getAllCardNames() {
        return getAllCards().stream().map(Card::getName).sorted().collect(Collectors.toList()).toArray(String[]::new);
    }

    public String[] getAllCompleterCardNames() {
        return Arrays.stream(getAllCardNames()).map(e -> e.replaceAll("\\s+", "_"))
                .collect(Collectors.toList()).toArray(String[]::new);
    }

    public int getCardPrice(String cardName) {
        int price;
        if ((price = getMonsterPrice(cardName)) != -1) return price;
        if ((price = getSpellPrice(cardName)) != -1) return price;
        return getTrapPrice(cardName);
    }

    public Card getCardByName(String name) {
        Monster monster = monsters.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findAny().orElse(null);
        if (monster != null) return monster;
        Spell spell = spells.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findAny().orElse(null);
        if (spell != null) return spell;
        return traps.stream().filter(e -> e.getName().equalsIgnoreCase(name)).findAny().orElse(null);
    }

    public boolean addCard(Card card, int price) {
        switch (card.getCardType()) {
            case MONSTER:
                addMonster((Monster) card, price);
                break;
            case SPELL:
                addSpell((Spell) card, price);
                break;
            case TRAP:
                addTrap((Trap) card, price);
                break;
            default:
                return false;
        }
        return true;
    }

    public boolean addShopCards(ShopCards cards) {
        for (Card card : cards.getAllCards()) {
            if (!addCard(card, cards.getCardPrice(card.getName()))) {
                return false;
            }
        }
        return true;
    }

}
