package edu.sharif.ce.apyugioh.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.Spell;
import edu.sharif.ce.apyugioh.model.card.SpellLimit;
import edu.sharif.ce.apyugioh.model.card.Trap;
import edu.sharif.ce.apyugioh.view.menu.deckmenu.DeckMenuView;
import lombok.Getter;
import lombok.Setter;

public class DeckMenuController {

    @Getter
    private static DeckMenuController instance;
    private static Logger logger;

    static{
        instance = new DeckMenuController();
        logger = LogManager.getLogger(DeckMenuController.class);
    }

    @Getter
    @Setter
    private User user;
    private DeckMenuView view;
    private Deck selectedDeck;
    private Inventory userInventory;
    @Getter
    private List<Deck> userDecks;

    private DeckMenuController(){
    }

    public static Card getCardByName(String cardName) {
        Card card = DatabaseManager.getCards().getCardByName(cardName);
        return card;
    }

    public Deck getUserActiveDeck(){
        return Deck.getDeckByID(user.getMainDeckID());
    }

    public DeckMenuView getView() {
        return view;
    }

    public Inventory getUserInventory() {
        return userInventory;
    }

    public Deck getSelectedDeck() {
        return selectedDeck;
    }

    public void loadUserInventory() {
        userInventory = Inventory.getInventoryByUserID(DeckMenuController.getInstance().getUser().getId());
    }

    public void deleteDeck() {
        Deck.remove(selectedDeck.getId());
        if (selectedDeck.getId() == user.getMainDeckID())
            user.setMainDeckID(-1);
        DatabaseManager.updateUsersToDB();
        selectDeck(null);
        view.updateCardContainers();
    }

    public void activateDeck() {
        user.setMainDeckID(selectedDeck.getId());
        DatabaseManager.updateUsersToDB();
    }

    public void loadUserDecks() {
        userDecks = Deck.getUserDecks(user.getId());
        if (selectedDeck == null && userDecks.size()>0)
            selectDeck(userDecks.get(0));
    }

    public void selectDeck(Deck deck) {
        selectedDeck = deck;
    }

    public void selectDeckByName(String deckName){
        for(Deck deck:userDecks){
            if (deckName.equals(deck.getName()))
                selectDeck(deck);
        }
    }

    public void createDeck(String deckName){
        Deck deck = Deck.getDeckByName(user.getId(), deckName);
        if (deck == null) {
            new Deck(user.getId(), deckName);
        } else
            view.showErrorDialog("You have another deck with this name!");
        loadUserDecks();
        selectDeckByName(deckName);
    }

    public void addCardToSideDeck(Card card) {
        if (selectedDeck == null)
            return;
        if (canAddCardToDeck(card,true))
            selectedDeck.addCardToDeck(card.getName(),true);
    }

    public void addCardToMainDeck(Card card) {
        if (selectedDeck == null)
            return;
        if (canAddCardToDeck(card,false))
            selectedDeck.addCardToDeck(card.getName(),false);
    }

    private boolean canAddCardToDeck(Card card,boolean isSide){
        if (!isSide) {
            if (selectedDeck.isMainDeckFull()) {
                view.showErrorDialog("Main deck is full!");
                return false;
            }
        } else {
            if (selectedDeck.isSideDeckFull()) {
                view.showErrorDialog("Side deck is full!");
                return false;
            }
        }
        if (isCardAddLimited(card, selectedDeck)) {
            view.showErrorDialog("Card add limit is reached!");
            return false;
        }
        if (selectedDeck.getCardTotalCount(card.getName()) >= userInventory.getCardStock()
                .getOrDefault(card.getName(), 0)) {
            view.showErrorDialog("You don't have enough cards of this!");
            return false;
        }
        return true;
    }

    public void removeCardFromSideDeck(Card card){
        if (selectedDeck == null)
            return;
        selectedDeck.removeCardFromDeck(card.getName(),true);
    }

    public void removeCardFromMainDeck(Card card){
        if (selectedDeck == null)
            return;
        selectedDeck.removeCardFromDeck(card.getName(),false);
    }

    private boolean isCardAddLimited(Card card, Deck deck) {
        if (card.getCardType().equals(CardType.MONSTER)) {
            if (deck.getCardTotalCount(card.getName()) >= 3)
                return true;
        } else if (card.getCardType().equals(CardType.SPELL)) {
            return isSpellTrapLimitReached(card, deck, ((Spell) card).getLimit());
        } else {
            return isSpellTrapLimitReached(card, deck, ((Trap) card).getLimit());
        }
        return false;
    }

    private boolean isSpellTrapLimitReached(Card card, Deck deck, SpellLimit limit) {
        if (limit.equals(SpellLimit.LIMITED)) {
            if (deck.getCardTotalCount(card.getName()) >= 1)
                return true;
        } else {
            if (deck.getCardTotalCount(card.getName()) >= 3)
                return true;
        }
        return false;
    }

    public void preset(){
        userDecks = new ArrayList<>();
        loadUserDecks();
    }

    public void showDeckMenu(){
        if (view != null)
            view.dispose();
        preset();
        view = new DeckMenuView(ProgramController.getGame());
        ProgramController.setState(MenuState.DECK);
        ProgramController.setCurrentMenu(view);
    }

    public void back(){
        view.dispose();
        view = null;
        MainMenuController.getInstance().showMainMenu();
    }

}
