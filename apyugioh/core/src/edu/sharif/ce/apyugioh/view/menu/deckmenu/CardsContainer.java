package edu.sharif.ce.apyugioh.view.menu.deckmenu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.sharif.ce.apyugioh.controller.DeckMenuController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.view.model.CardActor;
import lombok.Getter;
import lombok.Setter;

class CardsContainer {
    @Getter
    @Setter
    private Table cardsTable;
    private Map<String, Integer> monsterCards;
    private Map<String, Integer> spellCards;
    private Map<String, Integer> trapCards;
    private int rowCardsCount;
    private float pad = 20;
    private float cardWidth;
    private float cardHeight;

    public CardsContainer(int rowCardsCount, float cardWidth, float cardHeight, float pad) {
        this.rowCardsCount = rowCardsCount;
        this.cardWidth = cardWidth;
        this.cardHeight = cardHeight;
        this.pad = pad;
        cardsTable = new Table();
        monsterCards = new HashMap<>();
        spellCards = new HashMap<>();
        trapCards = new HashMap<>();
    }

    private Card getCardByName(String cardName) {
        return DatabaseManager.getCards().getCardByName(cardName);
    }

    public Table getCardsTable() {
        return cardsTable;
    }

    public void updateCards(Map<String, Integer> monsterCards, Map<String, Integer> spellCards,
                            Map<String, Integer> trapCards) {
        this.monsterCards = monsterCards;
        this.spellCards = spellCards;
        this.trapCards = trapCards;
        loadCards();
    }

    public void setPad(float pad) {
        this.pad = pad;
    }

    public void loadCards() {
        cardsTable.clearChildren();
        ArrayList<CardActor> cardActors = new ArrayList<>();
        if (monsterCards != null) {
            for (String cardName : monsterCards.keySet()) {
                cardActors.add(new CardActor(getCardByName(cardName), 200,
                        340, monsterCards.get(cardName)));
            }
        }
        if (spellCards != null) {
            for (String cardName : spellCards.keySet()) {
                cardActors.add(new CardActor(getCardByName(cardName), 200,
                        340, spellCards.get(cardName)));
            }
        }
        if (trapCards != null) {
            for (String cardName : trapCards.keySet()) {
                cardActors.add(new CardActor(getCardByName(cardName), 200,
                        340, trapCards.get(cardName)));
            }
        }
        for (int i = 0; i < cardActors.size(); ++i) {
            CardActor cardActor = cardActors.get(i);
            makeCardDraggable(cardActor);
            cardActor.setSize(cardWidth, cardHeight);
            if (i % rowCardsCount == 0 && i > 0)
                cardsTable.row();
            cardsTable.add(cardActor).padRight(pad).padTop(pad).center();
        }
    }

    private void makeCardDraggable(CardActor cardActor) {
        CardsContainer thisContainer = this;
        cardActor.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                DeckMenuController.getInstance().getView().selectCard(cardActor, thisContainer);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

}
