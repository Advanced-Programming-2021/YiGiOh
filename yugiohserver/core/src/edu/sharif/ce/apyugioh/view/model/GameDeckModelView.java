package edu.sharif.ce.apyugioh.view.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.card.GameCard;

public class GameDeckModelView {

    private Field firstPlayerField, secondPlayerField;
    private HashMap<Integer, CardModelView> cardMap;

    public GameDeckModelView(Field firstPlayerField, Field secondPlayerField) {
        this.firstPlayerField = firstPlayerField;
        this.secondPlayerField = secondPlayerField;
        cardMap = new HashMap<>();
        addFieldToMap(firstPlayerField);
        addFieldToMap(secondPlayerField);
    }

    private void addFieldToMap(Field playerField) {
        for (GameCard card : playerField.getDeck()) {
            addCard(card);
        }
        for (GameCard card : playerField.getHand()) {
            addCard(card);
        }
    }

    public CardModelView getCard(int cardID) {
        return cardMap.get(cardID);
    }

    public List<CardModelView> getAllCards() {
        return cardMap.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public void addCard(GameCard card) {
        CardModelView cardView = new CardModelView(Utils.firstUpperOnly(card.getCard().getName()),
                AssetController.getDeck().getAtlas().createSprite(Utils.firstUpperOnly(card.getCard().getName()).replaceAll("\\s+", "")),
                AssetController.getDeck().getAtlas().createSprite("Unknown"));
        cardMap.put(card.getId(), cardView);
    }
}
