package edu.sharif.ce.apyugioh.view;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.asciithemes.u8.U8_Grids;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.card.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

public class ShopView extends View {

    public static final int SUCCESS_BUY_CARD = 1;

    public static final int ERROR_CARD_NAME_INVALID = -1;
    public static final int ERROR_MONEY_NOT_ENOUGH = -2;

    {
        successMessages.put(SUCCESS_BUY_CARD, "bought %s successfully.\nbalance: %s");

        errorMessages.put(ERROR_CARD_NAME_INVALID, "card with name %s doesn't exist");
        errorMessages.put(ERROR_MONEY_NOT_ENOUGH, "not enough money to buy %s.\nyou currently have %s and you need %s more to buy this card");
    }

    public void showAllCards(ShopCards cards) {
        AsciiTable cardsTable = new AsciiTable();
        CWC_LongestLine cwc = new CWC_LongestLine();
        cardsTable.addRule();
        boolean isEmpty = (cards.getMonsterPrices().values().stream().mapToInt(e -> e).sum() +
                cards.getSpellPrices().values().stream().mapToInt(e -> e).sum() +
                cards.getTrapPrices().values().stream().mapToInt(e -> e).sum()) == 0;
        if (!isEmpty) cardsTable.addRow(null, null, "Shop");
        else cardsTable.addRow(null, "Shop");
        addCardsToTable(isEmpty, new TreeMap<>(cards.getMonsterPrices()), new TreeMap<>(cards.getSpellPrices()), new TreeMap<>(cards.getTrapPrices()), cardsTable);
        cwc.add(10, 30).add(40, ProgramController.getReader().getTerminal().getWidth() / 2);
        if (!isEmpty) cwc.add(20, 30);
        cardsTable.getRenderer().setCWC(cwc);
        System.out.println(cardsTable.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
    }

    private void addCardsToTable(boolean isEmpty, Map<String, Integer> monsters, Map<String, Integer> spells, Map<String, Integer> traps, AsciiTable table) {
        table.addRule();
        if (isEmpty) table.addRow(null, "Monsters:");
        else table.addRow(null, null, "Monsters:");
        table.addStrongRule();
        addCardsToTable(table, monsters);
        if (isEmpty) table.addRow(null, "Spells:");
        else table.addRow(null, null, "Spells:");
        table.addStrongRule();
        addCardsToTable(table, spells);
        if (isEmpty) table.addRow(null, "Traps:");
        else table.addRow(null, null, "Traps:");
        table.addStrongRule();
        addCardsToTable(table, traps);
        table.setTextAlignment(TextAlignment.CENTER);
        table.getContext().setGrid(U8_Grids.borderStrongDoubleLight());
    }

    private void addCardsToTable(AsciiTable table, Map<String, Integer> cards) {
        int counter = 0;
        for (Map.Entry<String, Integer> card : cards.entrySet()) {
            if (counter == 0) {
                table.addRow("", "name", "price");
                table.addStrongRule();
            }
            table.addRow(++counter, card.getKey(), card.getValue());
            table.addRule();
        }
    }

    public void showCard(Card card) {
        showCardImage(card);
        showCardStats(card);
    }

    private void showCardStats(Card card) {
        AsciiTable cardInfo = new AsciiTable();
        CWC_LongestLine cwc = new CWC_LongestLine();
        cardInfo.addRule();
        if (card.getCardType().equals(CardType.MONSTER)) {
            Monster monster = (Monster) card;
            addMonsterRow(cardInfo, cwc, monster);
        } else if (card.getCardType().equals(CardType.SPELL)) {
            Spell spell = (Spell) card;
            addSpellTrapRow(cardInfo, cwc, spell.getName(), spell.getProperty(), spell.getDescription());
        } else if (card.getCardType().equals(CardType.TRAP)) {
            Trap trap = (Trap) card;
            addSpellTrapRow(cardInfo, cwc, trap.getName(), trap.getProperty(), trap.getDescription());
        }
        cardInfo.addRule();
        cardInfo.setTextAlignment(TextAlignment.CENTER);
        cardInfo.getContext().setGrid(U8_Grids.borderDouble());
        cardInfo.getRenderer().setCWC(cwc);
        System.out.println(cardInfo.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
    }

    private void addMonsterRow(AsciiTable cardInfo, CWC_LongestLine cwc, Monster monster) {
        cardInfo.addRow("name", "level", "type", "attack", "defense", "description");
        cardInfo.addRule();
        cardInfo.addRow(monster.getName(), monster.getLevel() + "<br>" + "★".repeat(monster.getLevel()),
                Utils.firstUpperOnly(monster.getType().toString()), monster.getAttackPoints(),
                monster.getDefensePoints(), monster.getDescription());
        cwc.add(4, 40).add(5, 20).add(4, 20).add(6, 20).add(7, 20).add(11, ProgramController.getReader().getTerminal().getWidth() / 2);
    }

    private void addSpellTrapRow(AsciiTable cardInfo, CWC_LongestLine cwc, String name, SpellProperty property, String description) {
        cardInfo.addRow("name", "type", "description");
        cardInfo.addRule();
        cardInfo.addRow(name, Utils.firstUpperOnly(property.toString()), description);
        cwc.add(4, 20).add(4, 20).add(11, ProgramController.getReader().getTerminal().getWidth() / 2);
    }

    private void showCardImage(Card card) {
        String imageName = Utils.firstUpperOnly(card.getName()).replaceAll("[\',]", "")
                .replaceAll(" ", "");
        Path imagePath = Path.of("assets", "cards", imageName + ".png");
        if (Files.exists(imagePath)) {
            System.out.println(new ImageToASCII("cards/" + imageName, Utils.getTerminalScale(3), false).getASCII());
        }
    }

}
