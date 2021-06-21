package edu.sharif.ce.apyugioh.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.view.ShopView;
import edu.sharif.ce.apyugioh.view.menu.ShopMenuView;
import lombok.Getter;
import lombok.Setter;

public class ShopController {

    @Getter
    private static ShopController instance;
    private static ShopMenuView view;
    private static Logger logger;

    static {
        instance = new ShopController();
        view = new ShopMenuView(ProgramController.getGame());
        logger = LogManager.getLogger(ShopController.class);
    }

    private ShopController() {
    }

    @Setter
    @Getter
    private User user;

    public void buyCard(String cardName) {
        Inventory userInventory = Inventory.getInventoryByUserID(user.getId());
        Card card = DatabaseManager.getCards().getAllCards().stream().filter(e -> e.getName().equalsIgnoreCase(cardName))
                .findAny().orElse(null);
        if (card != null) {
            if (userInventory.getMoney() >= DatabaseManager.getCards().getCardPrice(cardName)) {
                userInventory.buyCard(cardName);
                logger.info("{} bought {}", user.getNickname(), card.getName());
                view.showSuccess(ShopView.SUCCESS_BUY_CARD, cardName, String.valueOf(userInventory.getMoney()));
            } else {
                view.showError(ShopView.ERROR_MONEY_NOT_ENOUGH, cardName,
                        String.valueOf(userInventory.getMoney()),
                        String.valueOf(DatabaseManager.getCards().getCardPrice(cardName) - userInventory.getMoney()));
            }
        } else {
            view.showError(ShopView.ERROR_CARD_NAME_INVALID, cardName);
        }
    }

    public void showAllCards() {
        //view.showAllCards(DatabaseManager.getCards());
    }

    public void showCard(String cardName) {
        Card card = DatabaseManager.getCards().getAllCards().stream().filter(e -> e.getName().equalsIgnoreCase(cardName))
                .findAny().orElse(null);
        if (card != null) {
            //view.showCard(card);
        } else {
            view.showError(ShopView.ERROR_CARD_NAME_INVALID);
        }
    }

    public void showShop() {
        ProgramController.getGame().setScreen(view);
    }
}
