package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.view.ShopView;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

public class ShopController {

    @Getter
    private static ShopController instance;
    private static ShopView view;

    static {
        instance = new ShopController();
        view = new ShopView();
    }

    @Getter
    @Setter
    private User user;

    public void buyCard(String cardName) {
        Inventory userInventory = Inventory.getInventoryByUsername(user.getUsername());
        Card card = DatabaseController.getCards().getAllCards().stream().filter(e -> e.getName().equalsIgnoreCase(cardName))
                .findAny().orElse(null);
        if (card != null) {
            if (userInventory.getMoney() >= DatabaseController.getCards().getCardPrice(cardName)) {
                userInventory.buyCard(cardName);
                view.showSuccess(ShopView.SUCCESS_BUY_CARD, cardName, String.valueOf(userInventory.getMoney()));
            } else {
                view.showError(ShopView.ERROR_MONEY_NOT_ENOUGH, cardName,
                        String.valueOf(userInventory.getMoney()),
                        String.valueOf(userInventory.getMoney() - DatabaseController.getCards().getCardPrice(cardName)));
            }
        } else {
            view.showError(ShopView.ERROR_CARD_NAME_INVALID);
        }
    }

    public void showAllCards() {
        String[] cardNames = DatabaseController.getCards().getAllCardNames();
        int[] cardPrices = Arrays.stream(cardNames).mapToInt(e -> DatabaseController.getCards().getCardPrice(e)).toArray();
        view.showAllCards(cardNames, cardPrices);
    }

    public void showCard(String cardName) {
        Card card = DatabaseController.getCards().getAllCards().stream().filter(e -> e.getName().equalsIgnoreCase(cardName))
                .findAny().orElse(null);
        if (card != null) {
            view.showCard(card);
        } else {
            view.showError(ShopView.ERROR_CARD_NAME_INVALID);
        }
    }
}
