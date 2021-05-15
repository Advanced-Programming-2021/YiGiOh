package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShopControllerTest {

    ShopController controller;
    Inventory inventory;
    int money;
    int blueEyesWhiteDragonStock;

    @BeforeEach
    void setUp() {
        DatabaseManager.init();
        controller = ShopController.getInstance();
        controller.setUser(User.getUserByID(1));
        inventory = Inventory.getInventoryByUserID(1);
        money = inventory.getMoney();
        inventory.setMoney(100000);
        blueEyesWhiteDragonStock = inventory.getCardStock().get("Blue Eyes White Dragon") != null
                ? inventory.getCardStock().get("Blue Eyes White Dragon") : 0;
    }

    @Test
    void buyCardNoStock() {
        inventory.getCardStock().remove("Blue Eyes White Dragon");
        controller.buyCard("Blue Eyes White Dragon");
        assertEquals(inventory.getCardStock().get("Blue Eyes White Dragon"), 1);
    }

    @Test
    void buyCardWithStock() {
        inventory.getCardStock().put("Blue Eyes White Dragon", 1);
        controller.buyCard("Blue Eyes White Dragon");
        assertEquals(inventory.getCardStock().get("Blue Eyes White Dragon"), 2);
    }

    @AfterEach
    void tearDown() {
        if (blueEyesWhiteDragonStock != 0) {
            inventory.getCardStock().put("Blue Eyes White Dragon", blueEyesWhiteDragonStock);
        } else {
            inventory.getCardStock().remove("Blue Eyes White Dragon");
        }
        inventory.setMoney(money);
    }
}