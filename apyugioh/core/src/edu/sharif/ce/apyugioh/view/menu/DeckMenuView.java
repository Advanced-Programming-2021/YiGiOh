package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.DeckController;
import edu.sharif.ce.apyugioh.controller.DeckMenuController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.Spell;
import edu.sharif.ce.apyugioh.model.card.SpellLimit;
import edu.sharif.ce.apyugioh.model.card.Trap;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.model.CardActor;
import lombok.Getter;
import lombok.Setter;

public class DeckMenuView extends Menu {

    private Stage stage;
    private Texture backgroundTexture;

    private Window inventoryWindow;
    private CardsContainer inventoryCards;

    private Window cardPreviewWindow;

    private Window sideDeckWindow;
    private CardsContainer sideDeckCards;

    private Window mainDeckWindow;
    private CardsContainer mainDeckCards;

    private Window decksListWindow;
    private Table decksListTable;
    private TextButton backButton;
    private TextButton activateButton;
    private TextButton deleteDeckButton;
    private TextButton newDeckButton;

    private Window currentDeckWindow;

    private SpriteBatch batch;
    private CardActor selectedCard;
    private CardActor draggingCard;
    private CardsContainer draggingCardContainer;

    public DeckMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 15, 5, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        assets.load("3D/puzzle/puzzle.g3db", Model.class);
        batch = new SpriteBatch();
        stage = new Stage();
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/main" + MathUtils.random(1, 10) + ".jpg"));
    }

    @Override
    public void show() {
        super.show();
        initialize();
        loadUserDecks();
        loadUserInventory();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        stage.act();
        stage.draw();
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) && draggingCard != null)
            dragCard(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        if (draggingCard != null) {
            draggingCard.setPosition(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
            stage.getBatch().begin();
            draggingCard.draw(stage.getBatch(), 1);
            stage.getBatch().end();
        }
        lastX = Gdx.input.getX();
        lastY = Gdx.input.getY();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    private void initialize() {
        inventoryCards = new CardsContainer(5, 200, 340, 50);
        sideDeckCards = new CardsContainer(3, 170, 170 * 1.6f, 20);
        mainDeckCards = new CardsContainer(5, 170, 170 * 1.6f, 20);
        selectedCard = new CardActor();
        inventoryWindow = new Window("Inventory", AssetController.getSkin("first"));
        cardPreviewWindow = new Window("Preview", AssetController.getSkin("first"));
        sideDeckWindow = new Window("Side Deck", AssetController.getSkin("first"));
        mainDeckWindow = new Window("Main Deck", AssetController.getSkin("first"));
        decksListWindow = new Window("Your Decks", AssetController.getSkin("first"));
        decksListTable = new Table();
        currentDeckWindow = new Window("current Deck", AssetController.getSkin("first"));
        backButton = new TextButton("Back", AssetController.getSkin("first"));
        activateButton = new TextButton("Activate", AssetController.getSkin("first"));
        newDeckButton = new TextButton("New Deck...", AssetController.getSkin("first"));
        deleteDeckButton = new TextButton("delete Deck", AssetController.getSkin("first"));
        stage.addActor(inventoryWindow);
        stage.addActor(cardPreviewWindow);
        stage.addActor(sideDeckWindow);
        stage.addActor(mainDeckWindow);
        stage.addActor(decksListWindow);
        stage.addActor(currentDeckWindow);
        arrangeWidgets();
        addListeners();
    }

    private void arrangeWidgets() {
        arrangeWindows();
        selectedCard.setWidth(cardPreviewWindow.getWidth() * 0.9f);
        selectedCard.setHeight(cardPreviewWindow.getHeight() * 0.9f);
        cardPreviewWindow.add(selectedCard).fill().center();
        ScrollPane inventoryScrollPane = new ScrollPane(inventoryCards.getCardsTable(), AssetController.getSkin("first"));
        inventoryScrollPane.setFillParent(true);
        inventoryScrollPane.setFlickScroll(false);
        inventoryWindow.add(inventoryScrollPane).fill().padRight(10).padLeft(10).center();
        ScrollPane sideDeckScrollPane = new ScrollPane(sideDeckCards.getCardsTable(), AssetController.getSkin("first"), "vertical");
        sideDeckScrollPane.setFillParent(true);
        sideDeckScrollPane.setFlickScroll(false);
        sideDeckScrollPane.setScrollbarsVisible(false);
        sideDeckWindow.add(sideDeckScrollPane).fill().left();
        ScrollPane mainDeckScrollPane = new ScrollPane(mainDeckCards.getCardsTable(), AssetController.getSkin("first"));
        mainDeckScrollPane.setFillParent(true);
        mainDeckScrollPane.setFlickScroll(false);
        mainDeckWindow.add(mainDeckScrollPane).fill().left();
        ScrollPane decksListScrollPane = new ScrollPane(decksListTable, AssetController.getSkin("first"));
        decksListWindow.add(decksListScrollPane).expandX().colspan(4);
        decksListWindow.row();
        decksListWindow.add(backButton).colspan(1).fillX().height(60);
        decksListWindow.add(newDeckButton).colspan(1).fillX().height(60);
        decksListWindow.add(deleteDeckButton).colspan(1).fillX().height(60);
        decksListWindow.add(activateButton).colspan(1).fillX().height(60);
    }

    private void arrangeWindows() {
        float horizontalPad = 20;
        float verticalPad = 20;
        float height = Gdx.graphics.getHeight() - verticalPad * 2 - 80;
        decksListWindow.setBounds(60, 40, 1500, height * 2 / 9f);
        currentDeckWindow.setBounds(decksListWindow.getX() + decksListWindow.getWidth() + horizontalPad,
                40, 240, height * 2 / 9f);
        sideDeckWindow.setBounds(60, decksListWindow.getY() + decksListWindow.getHeight() + verticalPad,
                650, height * 3 / 9f);
        mainDeckWindow.setBounds(sideDeckWindow.getX() + sideDeckWindow.getWidth() + horizontalPad,
                decksListWindow.getY() + decksListWindow.getHeight() + verticalPad,
                1100, height * 3 / 9f);
        inventoryWindow.setBounds(60, sideDeckWindow.getY() + sideDeckWindow.getHeight() + verticalPad,
                1440, height * 4 / 9f);
        cardPreviewWindow.setBounds(inventoryWindow.getX() + inventoryWindow.getWidth() + horizontalPad,
                sideDeckWindow.getY() + sideDeckWindow.getHeight() + verticalPad,
                300, height * 4 / 9f);
    }

    public void selectCard(CardActor cardActor, CardsContainer container) {
        draggingCardContainer = container;
        draggingCard = CardActor.clone(cardActor);
        selectedCard.getCardSprite().setTexture(cardActor.getCardSprite().getTexture());
    }

    private void loadUserInventory() {
        userInventory = Inventory.getInventoryByUserID(DeckMenuController.getInstance().getUser().getId());
        Map<String, Integer> inventoryMonsters = userInventory.getMonsters();
        Map<String, Integer> inventorySpells = userInventory.getSpells();
        Map<String, Integer> inventoryTraps = userInventory.getTraps();
        inventoryCards.setCards(inventoryMonsters, inventorySpells, inventoryTraps);
    }

    private void loadUserDecks() {

        if (userDecks.size() > 0)
            selectDeck(userDecks.get(0));
    }

    private void selectDeck(Deck deck) {
        selectedDeck = deck;
        loadDeck(deck);
    }

    private void newDeck() {
        String deckName = "DeckName ali";
        //getDeckName

    }

    private void deleteDeck() {

    }

    private void activateDeck() {
        DeckController.getInstance().getUser().setMainDeckID(selectedDeck.getId());
        DatabaseManager.updateUsersToDB();
        //
    }

    private void loadDeck(Deck deck) {
        loadMainDeckCards(deck);
        loadSideDeckCards(deck);
    }

    private void loadMainDeckCards(Deck deck) {
        Map<String, Integer> mainDeckMonsters = (Map) deck.getMonsters(false);
        Map<String, Integer> mainDeckSpells = (Map) deck.getSpells(false);
        Map<String, Integer> mainDeckTraps = (Map) deck.getTraps(false);
        mainDeckCards.setCards(mainDeckMonsters, mainDeckSpells, mainDeckTraps);
    }

    private void loadSideDeckCards(Deck deck) {
        Map<String, Integer> sideDeckMonsters = (Map) deck.getMonsters(true);
        Map<String, Integer> sideDeckSpells = (Map) deck.getSpells(true);
        Map<String, Integer> sideDeckTraps = (Map) deck.getTraps(true);
        sideDeckCards.setCards(sideDeckMonsters, sideDeckSpells, sideDeckTraps);
    }

    private void dragCard(float mouseX, float mouseY) {
        //dragged to Inventory
        if (isInsideWindow(inventoryWindow, mouseX, mouseY)) {
            if (draggingCardContainer != inventoryCards)
                draggingCardContainer.removeCard(draggingCard);
        }
        //dragged to sideDeck
        if (isInsideWindow(sideDeckWindow, mouseX, mouseY))
            addCardToSideDeck();
        //dragged to mainDeck
        if (isInsideWindow(mainDeckWindow, mouseX, mouseY))
            addCardToMainDeck();
        draggingCard = null;
        draggingCardContainer = null;
    }

    private void addCardToMainDeck() {
        if (selectedDeck == null)
            return;
        if (draggingCardContainer != mainDeckCards) {
            if (!isCardAddLimited(draggingCard.getCard(), selectedDeck)) {
                draggingCardContainer.removeCard(draggingCard);
                mainDeckCards.addCard(draggingCard);
            } else
                showErrorDialog("You can't add this card to your deck anymore!");
        }
    }

    private void removeCardFromMainDeck(){

    }

    private void

    private void addCardToSideDeck() {
        if (selectedDeck != null) {
            if (draggingCardContainer != sideDeckCards) {
                if (!isCardAddLimited(draggingCard.getCard(), selectedDeck)) {
                    draggingCardContainer.removeCard(draggingCard);
                    sideDeckCards.addCard(draggingCard);
                } else
                    showErrorDialog("You can't add this card to your deck anymore!");
            }
        }
    }

    private void showErrorDialog(String errorMessage) {

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

    static Card getCardByName(String cardName) {
        Card card = DatabaseManager.getCards().getCardByName(cardName);
        return card;
    }

    private boolean isInsideWindow(Window window, float x, float y) {
        if (window.getX() < x && window.getX() + window.getWidth() > x
                && window.getY() < y && window.getY() + window.getHeight() > y)
            return true;
        return false;
    }

    private void addListeners() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE)
                    DeckMenuController.getInstance().back();
                return super.keyDown(event, keycode);
            }
        });
        backButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                DeckMenuController.getInstance().back();
            }
        });
        newDeckButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                newDeck();
            }
        });
        deleteDeckButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                deleteDeck();
            }
        });
        activateButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                activateDeck();
            }
        });
    }

}

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

    public Table getCardsTable() {
        return cardsTable;
    }

    public void setCards(Map<String, Integer> monsterCards, Map<String, Integer> spellCards,
                         Map<String, Integer> trapCards) {
        this.monsterCards = monsterCards;
        this.spellCards = spellCards;
        this.trapCards = trapCards;
        loadCards();
    }

    public void setPad(float pad) {
        this.pad = pad;
    }

    public void addCard(CardActor card) {
        if (card.getCard().getCardType().equals(CardType.MONSTER)) {
            if (monsterCards.containsKey(card.getCard().getName()))
                monsterCards.merge(card.getCard().getName(), 1, Integer::sum);
            else
                monsterCards.put(card.getCard().getName(), 1);
        }
        if (card.getCard().getCardType().equals(CardType.SPELL)) {
            if (spellCards.containsKey(card.getCard().getName()))
                spellCards.merge(card.getCard().getName(), 1, Integer::sum);
            else
                spellCards.put(card.getCard().getName(), 1);
        }
        if (card.getCard().getCardType().equals(CardType.TRAP)) {
            if (trapCards.containsKey(card.getCard().getName()))
                trapCards.merge(card.getCard().getName(), 1, Integer::sum);
            else
                trapCards.put(card.getCard().getName(), 1);
        }
        loadCards();
    }

    public void removeCard(CardActor card) {
        if (card.getCard().getCardType().equals(CardType.MONSTER)) {
            monsterCards.merge(card.getCard().getName(), -1, Integer::sum);
            if (monsterCards.get(card.getCard().getName()) == 0)
                monsterCards.remove(card.getCard().getName(), 0);
        }
        if (card.getCard().getCardType().equals(CardType.SPELL)) {
            spellCards.merge(card.getCard().getName(), -1, Integer::sum);
            if (spellCards.get(card.getCard().getName()) == 0)
                spellCards.remove(card.getCard().getName(), 0);
        }
        if (card.getCard().getCardType().equals(CardType.TRAP)) {
            trapCards.merge(card.getCard().getName(), -1, Integer::sum);
            if (trapCards.get(card.getCard().getName()) == 0)
                trapCards.remove(card.getCard().getName(), 0);
        }
        loadCards();
    }

    public void loadCards() {
        cardsTable.clearChildren();
        ArrayList<CardActor> cardActors = new ArrayList<>();
        if (monsterCards != null) {
            for (String cardName : monsterCards.keySet()) {
                cardActors.add(new CardActor(DeckMenuView.getCardByName(cardName), 200,
                        340, monsterCards.get(cardName)));
            }
        }
        if (spellCards != null) {
            for (String cardName : spellCards.keySet()) {
                cardActors.add(new CardActor(DeckMenuView.getCardByName(cardName), 200,
                        340, spellCards.get(cardName)));
            }
        }
        if (trapCards != null) {
            for (String cardName : trapCards.keySet()) {
                cardActors.add(new CardActor(DeckMenuView.getCardByName(cardName), 200,
                        340, trapCards.get(cardName)));
            }
        }
        System.out.println(cardActors.size());
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