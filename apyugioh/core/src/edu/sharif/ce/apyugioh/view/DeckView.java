package edu.sharif.ce.apyugioh.view;

import java.util.List;

import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.Inventory;

public class DeckView extends View {

    public static final int SUCCESS_DECK_CREATE = 1;
    public static final int SUCCESS_DECK_REMOVE = 2;
    public static final int SUCCESS_DECK_ACTIVATE = 3;
    public static final int SUCCESS_DECK_CARD_ADD = 4;
    public static final int SUCCESS_DECK_CARD_REMOVE = 5;

    public static final int ERROR_DECK_NAME_ALREADY_EXISTS = -1;
    public static final int ERROR_DECK_NAME_NOT_FOUND = -2;
    public static final int ERROR_CARD_NAME_INVALID = -3;
    public static final int ERROR_DECK_FULL = -4;
    public static final int ERROR_CARD_ENOUGH_IN_DECK = -5;
    public static final int ERROR_CARD_NOT_IN_DECK = -6;
    public static final int ERROR_INVENTORY_CARDS_NOT_ENOUGH = -7;

    {
        successMessages.put(SUCCESS_DECK_CREATE, "deck created successfully!");
        successMessages.put(SUCCESS_DECK_REMOVE, "deck deleted successfully!");
        successMessages.put(SUCCESS_DECK_ACTIVATE, "deck activated successfully!");
        successMessages.put(SUCCESS_DECK_CARD_ADD, "card added successfully!");
        successMessages.put(SUCCESS_DECK_CARD_REMOVE, "card removed successfully!");

        errorMessages.put(ERROR_DECK_NAME_ALREADY_EXISTS, "deck with name %s already exists");
        errorMessages.put(ERROR_DECK_NAME_NOT_FOUND, "deck with name %s doesn't exist");
        errorMessages.put(ERROR_CARD_NAME_INVALID, "card with name %s doesn't exist");
        errorMessages.put(ERROR_DECK_FULL, "%s deck is full");
        errorMessages.put(ERROR_CARD_ENOUGH_IN_DECK, "there %s already %s card%s with name %s in deck %s");
        errorMessages.put(ERROR_CARD_NOT_IN_DECK, "card with name %s doesn't exist in %s deck");
        errorMessages.put(ERROR_INVENTORY_CARDS_NOT_ENOUGH, "you don't have enough %s in your inventory");
    }

    public void showAll(Deck activeDeck, List<Deck> deactivatedDecks) {
        /*AsciiTable deckTable = new AsciiTable();
        deckTable.addRule();
        deckTable.addRow("name", "main deck", "side deck", "validity");
        deckTable.addRule();
        deckTable.addRow(null, null, null, "active deck:");
        if (activeDeck != null) {
            deckTable.addRule();
            deckTable.addRow(activeDeck.getName(), activeDeck.getMainDeckCardsCount(), activeDeck.getSideDeckCardsCount(), activeDeck.isDeckValid() ? "valid" : "invalid");
        }
        deckTable.addRule();
        deckTable.addRow(null, null, null, "other decks:");
        for (Deck deck : deactivatedDecks) {
            deckTable.addRule();
            deckTable.addRow(deck.getName(), deck.getMainDeckCardsCount(), deck.getSideDeckCardsCount(), deck.isDeckValid() ? "valid" : "invalid");
        }
        deckTable.addRule();
        deckTable.setTextAlignment(TextAlignment.CENTER);
        deckTable.getContext().setGrid(U8_Grids.borderDouble());
        System.out.println(deckTable.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));*/
    }

    public void showDeck(Deck deck, boolean isSideDeck) {
        /*AsciiTable deckTable = new AsciiTable();
        CWC_LongestLine cwc = new CWC_LongestLine();
        deckTable.addRule();
        boolean isEmpty = isSideDeck ? deck.getSideDeckCardsCount() == 0 : deck.getMainDeckCardsCount() == 0;
        if (!isEmpty) {
            deckTable.addRow("Deck:", null, deck.getName());
            deckTable.addRule();
            deckTable.addRow(null, null, (isSideDeck ? "Side" : "Main") + " Deck:");
        } else {
            deckTable.addRow("Deck:", deck.getName());
            deckTable.addRule();
            deckTable.addRow(null, (isSideDeck ? "Side" : "Main") + " Deck:");
        }
        addCardsToTable(isEmpty, deck.getMonsters(isSideDeck), deck.getSpells(isSideDeck), deck.getTraps(isSideDeck), deckTable);
        cwc.add(10, 30).add(40, ProgramController.getReader().getTerminal().getWidth() / 2);
        if (!isEmpty) cwc.add(20, 30);
        deckTable.getRenderer().setCWC(cwc);
        System.out.println(deckTable.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));*/
    }

    public void showInventory(Inventory inventory) {
        /*AsciiTable deckTable = new AsciiTable();
        CWC_LongestLine cwc = new CWC_LongestLine();
        deckTable.addRule();
        boolean isEmpty = inventory.getCardsCount() == 0;
        if (!isEmpty) deckTable.addRow(null, null, "Inventory");
        else deckTable.addRow(null, "Inventory");
        addCardsToTable(isEmpty, inventory.getMonsters(), inventory.getSpells(), inventory.getTraps(), deckTable);
        cwc.add(10, 30).add(40, ProgramController.getReader().getTerminal().getWidth() / 2);
        if (!isEmpty) cwc.add(20, 30);
        deckTable.getRenderer().setCWC(cwc);
        System.out.println(deckTable.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));*/
    }

    /*private void addCardsToTable(boolean isEmpty, Map<String, Integer> monsters, Map<String, Integer> spells, Map<String, Integer> traps, AsciiTable deckTable) {
        deckTable.addRule();
        if (isEmpty) deckTable.addRow(null, "Monsters:");
        else deckTable.addRow(null, null, "Monsters:");
        deckTable.addStrongRule();
        addCardsToTable(deckTable, monsters);
        if (isEmpty) deckTable.addRow(null, "Spells:");
        else deckTable.addRow(null, null, "Spells:");
        deckTable.addStrongRule();
        addCardsToTable(deckTable, spells);
        if (isEmpty) deckTable.addRow(null, "Traps:");
        else deckTable.addRow(null, null, "Traps:");
        deckTable.addStrongRule();
        addCardsToTable(deckTable, traps);
        deckTable.setTextAlignment(TextAlignment.CENTER);
        deckTable.getContext().setGrid(U8_Grids.borderStrongDoubleLight());
    }*/

    /*private void addCardsToTable(AsciiTable table, Map<String, Integer> cards) {
        int counter = 0;
        for (Map.Entry<String, Integer> card : cards.entrySet()) {
            if (counter == 0) {
                table.addRow("", "name", "count");
                table.addStrongRule();
            }
            table.addRow(++counter, card.getKey(), card.getValue());
            table.addRule();
        }
    }*/
}
