package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.view.model.CardAction;
import edu.sharif.ce.apyugioh.view.model.CardActionsManager;
import edu.sharif.ce.apyugioh.view.model.CardModelView;
import edu.sharif.ce.apyugioh.view.model.DeckModelView;

public class ShopMenuView extends Menu {

    public static final int SUCCESS_BUY_CARD = 1;

    public static final int ERROR_CARD_NAME_INVALID = -1;
    public static final int ERROR_MONEY_NOT_ENOUGH = -2;

    {
        successMessages.put(SUCCESS_BUY_CARD, "bought %s successfully.\nbalance: %s");

        errorMessages.put(ERROR_CARD_NAME_INVALID, "card with name %s doesn't exist");
        errorMessages.put(ERROR_MONEY_NOT_ENOUGH, "not enough money to buy %s.\nyou currently have %s and you need %s more to buy this card");
    }

    private Stage stage;
    private SpriteBatch batch;
    Array<CardModelView> cards;
    private DeckModelView deck;
    private Texture backgroundTexture;
    private CardActionsManager manager;
    private ScrollPane scroll;
    private int animationSpeed = 1;
    private Window buyWindow;
    private Label nameLabel, cardInventoryCountLabel, cardPriceLabel, currentMoney;
    private TextButton buyButton;
    private Inventory userInventory;
    private Card poppedUpCard;

    public ShopMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 5, 0, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        batch = new SpriteBatch();
        stage = new Stage();
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/main" + MathUtils.random(1, 10) + ".jpg"));
        moveCamera = false;
        manager = new CardActionsManager();
    }

    @Override
    public void show() {
        super.show();
        initializeCards();
        scroll = new ScrollPane(null, AssetController.getSkin("first"));
        scroll.setSize(1448, 1080);
        scroll.setPosition(0, 0);
        scroll.addListener(new CustomListener());
        stage.addActor(scroll);
        userInventory = Inventory.getInventoryByUserID(ShopController.getInstance().getUser().getId());
        currentMoney = new Label("Money: " + userInventory.getMoney(), AssetController.getSkin("first"), "title");
        currentMoney.setPosition(1525, 1000);
        stage.addActor(currentMoney);
        initializeWindow();
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (poppedUpCard != null) {
                    ShopController.getInstance().buyCard(poppedUpCard.getName());
                    currentMoney.setText("Money: " + userInventory.getMoney());
                    cardInventoryCountLabel.setText(userInventory.getCardStock().getOrDefault(Utils.firstUpperOnly(poppedUpCard.getName()), 0));
                    if (DatabaseManager.getCards().getCardPrice(Utils.firstUpperOnly(poppedUpCard.getName())) > userInventory.getMoney()) {
                        buyButton.setTouchable(Touchable.disabled);
                    }
                }
            }
        });
        Gdx.input.setInputProcessor(stage);
    }

    private void initializeWindow() {
        buyWindow = new Window("", AssetController.getSkin("first"));
        buyWindow.setKeepWithinStage(false);
        buyWindow.setWidth(542);
        buyWindow.setHeight(940);
        Table table = new Table(AssetController.getSkin("first"));
        buyWindow.setPosition(Gdx.graphics.getWidth() - buyWindow.getWidth() - 15, Gdx.graphics.getHeight());
        Table content = new Table(AssetController.getSkin("first"));
        nameLabel = new Label("", AssetController.getSkin("first"), "title");
        content.add(nameLabel);
        content.row();
        Label priceLabel = new Label("Price: ", AssetController.getSkin("first"), "title");
        cardPriceLabel = new Label("", AssetController.getSkin("first"), "title");
        cardPriceLabel.setAlignment(Align.right);
        content.add(priceLabel);
        content.add(cardPriceLabel).expandX();
        content.row();
        Label alreadyBoughtLabel = new Label("Already Bought: ", AssetController.getSkin("first"), "title");
        cardInventoryCountLabel = new Label("", AssetController.getSkin("first"), "title");
        cardInventoryCountLabel.setAlignment(Align.right);
        content.add(alreadyBoughtLabel);
        content.add(cardInventoryCountLabel).expandX();
        content.row();
        buyButton = new TextButton("Buy", AssetController.getSkin("first"));
        content.add(buyButton);
        buyWindow.add(content);
        stage.addActor(buyWindow);
    }

    private void initializeCards() {
        cards = new Array<>();
        deck = new DeckModelView();
        int rowCounter = 0, columnCounter = 0;
        for (String cardName : DatabaseManager.getCards().getAllCardNames()) {
            CardModelView card = deck.getCard(cardName);
            if (card != null) {
                card.setTranslation(85 + (rowCounter * 11 + columnCounter) * 0.01f, -36, 60 - (rowCounter * 11 + columnCounter) * 0.01f);
                card.rotate(0, 1, 0, 90);
                CardAction action = new CardAction(card, new Vector3(45, 0, 0), 180, 2) {
                    public void onDone() {
                        AssetController.getSound("deal").play();
                    }
                };
                manager.addAction(action);
                CardAction action2 = new CardAction(card, new Vector3(75, 40 - 18 * rowCounter, -80 + 13 * columnCounter), 180, 5);
                manager.addAction(action2);
                cards.add(card);
                columnCounter++;
                if (columnCounter == 10) {
                    columnCounter = 0;
                    rowCounter++;
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        manager.update(animationSpeed * delta);
        modelBatch.begin(cam);
        for (CardModelView card : cards) {
            card.render(modelBatch, environment);
        }
        modelBatch.end();
        stage.act(delta);
        stage.draw();
    }

    class CustomListener extends ClickListener {

        private float lastX = 0, lastY = 0, startingX = 0, startingY = 0;
        private boolean cardShown;
        private int selectedCardIndex = -1;

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            lastX = x;
            lastY = y;
            startingX = x;
            startingY = y;
            return super.touchDown(event, x, y, pointer, button);
        }

        @Override
        public void touchDragged(InputEvent event, float x, float y, int pointer) {
            super.touchDragged(event, x, y, pointer);
            if ((y < lastY && cards.first().getPosition().y < 40) || (y > lastY && cards.get(cards.size - 1).getPosition().y > -40) || !manager.isDone() || selectedCardIndex != -1) {
                return;
            }
            float translation = (y - lastY) / 10f;
            if (translation < 0) {
                translation = Math.max(translation, 40 - cards.first().getPosition().y);
            }
            if (translation > 0) {
                translation = Math.min(translation, -40 - cards.get(cards.size - 1).getPosition().y);
            }
            for (CardModelView card : cards) {
                card.translate(0, translation, 0);
            }
            lastX = x;
            lastY = y;
        }

        @Override
        public void clicked(InputEvent event, float x, float y) {
            super.clicked(event, x, y);
            animationSpeed = 10;
            lastX = 0;
            lastY = 0;
            if (Math.abs(x - startingX) < 10 && Math.abs(y - startingY) < 10 && manager.isDone()) {
                if (x > 7 && x < 1447 && !cardShown) {
                    float shiftUp = cards.first().getPosition().y - 40;
                    int firstRow = (int) shiftUp / 18;
                    float firstRowAppeared = 18 - shiftUp % 18;
                    float baseUp = (firstRowAppeared / 18f) * 196;
                    y = 1080 - y;
                    int selectedRow = firstRow;
                    if (y > baseUp) {
                        selectedRow += (int) ((y - baseUp) / 196) + 1;
                    }
                    int selectedColumn = (int) ((x - 7) / 144);
                    selectedCardIndex = calculateIndex(selectedRow, selectedColumn);
                    if (selectedCardIndex >= cards.size) {
                        selectedCardIndex = -1;
                        return;
                    }
                    CardModelView selectedCard = cards.get(selectedCardIndex);
                    CardAction action = new CardAction(selectedCard, new Vector3(45, 0, 0), 180, 0.012f) {
                        public void onStart() {
                            AssetController.getSound("flip").play();
                        }
                    };
                    manager.addAction(action);
                    CardAction action2 = new CardAction(selectedCard, new Vector3(15, 0, 0), 0, 0.012f);
                    manager.addAction(action2);
                    buyWindow.addAction(Actions.moveBy(0, -buyWindow.getHeight(), 3));
                    AssetController.getSound("chain").play();
                    poppedUpCard = selectedCard.getCard();
                    nameLabel.setText(poppedUpCard.getName());
                    int price = DatabaseManager.getCards().getCardPrice(poppedUpCard.getName());
                    cardPriceLabel.setText(price);
                    cardInventoryCountLabel.setText(userInventory.getCardStock().getOrDefault(Utils.firstUpperOnly(poppedUpCard.getName()), 0));
                    if (price > userInventory.getMoney()) {
                        buyButton.setTouchable(Touchable.disabled);
                    } else {
                        buyButton.setTouchable(Touchable.enabled);
                    }
                    cardShown = true;
                } else {
                    if (selectedCardIndex != -1) {
                        CardModelView card = cards.get(selectedCardIndex);
                        CardAction action = new CardAction(card, new Vector3(45, 0, 0), 180, 0.012f) {
                            public void onStart() {
                                AssetController.getSound("flip").play();
                            }

                            public void onDone() {
                                cardShown = false;
                                selectedCardIndex = -1;
                                poppedUpCard = null;
                            }
                        };
                        manager.addAction(action);
                        int rowCounter = selectedCardIndex / 10;
                        int columnCounter = selectedCardIndex % 10;
                        CardAction action2 = new CardAction(card, new Vector3(75, cards.first().getPosition().y - 18 * rowCounter, cards.first().getPosition().z + 13 * columnCounter), 0, 0.012f);
                        manager.addAction(action2);
                        buyWindow.addAction(Actions.moveBy(0, buyWindow.getHeight(), 3));
                        AssetController.getSound("chain").play();
                    }
                }
            }
        }

        private int calculateIndex(int row, int column) {
            return row * 10 + column;
        }
    }
}
