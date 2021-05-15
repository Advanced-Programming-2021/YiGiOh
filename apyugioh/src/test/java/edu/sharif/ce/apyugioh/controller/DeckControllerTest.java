package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckControllerTest {

    DeckController controller;
    Deck deck;
    User user;
    int mainDeckID;
    Inventory inventory;
    int stock;

    @BeforeEach
    void setUp() {
        DatabaseManager.init();
        controller = DeckController.getInstance();
        user = User.getUserByID(1);
        mainDeckID = user.getMainDeckID();
        controller.setUser(user);
        deck = Deck.getDeckByID(1);
        inventory = Inventory.getInventoryByUserID(1);
        stock = inventory.getCardStock().get("Blue Eyes White Dragon");
        inventory.getCardStock().put("Blue Eyes White Dragon", 1);
    }

    @Test
    void create() {
        controller.create("Test Deck");
        assertEquals(Deck.getDeckByName(1, "Test Deck").getName(), "Test Deck");
        assertEquals(Deck.getDeckByName(1, "Test Deck").getUserID(), 1);
    }

    @Test
    void remove() {
        controller.create("Test Deck");
        controller.remove("Test Deck");
        assertNull(Deck.getDeckByName(1, "Test Deck"));
    }

    @Test
    void activate() {
        controller.create("Test Deck");
        controller.activate("Test Deck");
        assertEquals(user.getMainDeckID(), Deck.getDeckByName(1, "Test Deck").getId());
    }

    @Test
    void addCard() {
        controller.create("Test Deck");
        controller.addCard("Test Deck", "Blue Eyes White Dragon", false);
        assertEquals(Deck.getDeckByName(1, "Test Deck").getMainDeck().get("Blue Eyes White Dragon"), 1);
        controller.addCard("Test Deck", "Blue Eyes White Dragon", false);
        assertEquals(Deck.getDeckByName(1, "Test Deck").getMainDeck().get("Blue Eyes White Dragon"), 1);
        inventory.getCardStock().put("Blue Eyes White Dragon", 4);
        controller.addCard("Test Deck", "Blue Eyes White Dragon", false);
        controller.addCard("Test Deck", "Blue Eyes White Dragon", false);
        controller.addCard("Test Deck", "Blue Eyes White Dragon", false);
        assertEquals(Deck.getDeckByName(1, "Test Deck").getMainDeck().get("Blue Eyes White Dragon"), 3);
    }

    @Test
    void removeCard() {
        controller.create("Test Deck");
        controller.addCard("Test Deck", "Blue Eyes White Dragon", false);
        controller.removeCard("Test Deck", "Blue Eyes White Dragon", false);
        assertNull(Deck.getDeckByName(1, "Test Deck").getMainDeck().get("Blue Eyes White Dragon"));
        controller.removeCard("Test Deck", "Blue Eyes White Dragon", false);
        assertNull(Deck.getDeckByName(1, "Test Deck").getMainDeck().get("Blue Eyes White Dragon"));
    }

    @AfterEach
    void tearDown() {
        user.setMainDeckID(mainDeckID);
        controller.remove("Test Deck");
        inventory.getCardStock().put("Blue Eyes White Dragon", stock);
    }
}