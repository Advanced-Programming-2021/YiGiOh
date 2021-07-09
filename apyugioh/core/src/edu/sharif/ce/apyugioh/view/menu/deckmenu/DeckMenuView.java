package edu.sharif.ce.apyugioh.view.menu.deckmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import org.graalvm.compiler.debug.CSVUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.DeckMenuController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.menu.Menu;
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

    private Window activeDeckWindow;
    private DeckListElement activeDeckPreview;

    private SpriteBatch batch;
    private CardActor selectedCard;
    private CardActor draggingCard;
    private CardsContainer draggingCardContainer;

    public DeckMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 15, 5, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        batch = new SpriteBatch();
        stage = new Stage();
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/main" + MathUtils.random(1, 10) + ".jpg"));
    }

    @Override
    public void show() {
        super.show();
        initialize();
        updateCardContainers();
        updateDeckList();
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
            draggingCard.setPosition(Gdx.input.getX() - draggingCard.getWidth()/2f,
                    Gdx.graphics.getHeight() - Gdx.input.getY() - draggingCard.getHeight()/2f);
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
        inventoryCards = new CardsContainer(5, 230, 340, 30);
        sideDeckCards = new CardsContainer(3, 170, 170 * 1.6f, 20);
        mainDeckCards = new CardsContainer(5, 170, 170 * 1.6f, 20);
        selectedCard = new CardActor();
        inventoryWindow = new Window("", AssetController.getSkin("first"));
        cardPreviewWindow = new Window("", AssetController.getSkin("first"));
        sideDeckWindow = new Window("", AssetController.getSkin("first"));
        mainDeckWindow = new Window("", AssetController.getSkin("first"));
        decksListWindow = new Window("", AssetController.getSkin("first"));
        decksListTable = new Table();
        activeDeckWindow = new Window("", AssetController.getSkin("first"));
        backButton = new TextButton("Back", AssetController.getSkin("first"));
        activateButton = new TextButton("Activate", AssetController.getSkin("first"));
        newDeckButton = new TextButton("New Deck...", AssetController.getSkin("first"));
        deleteDeckButton = new TextButton("delete Deck", AssetController.getSkin("first"));
        stage.addActor(inventoryWindow);
        stage.addActor(cardPreviewWindow);
        stage.addActor(sideDeckWindow);
        stage.addActor(mainDeckWindow);
        stage.addActor(decksListWindow);
        stage.addActor(activeDeckWindow);
        arrangeWidgets();
        addListeners();
    }

    private void arrangeWindows() {
        float horizontalPad = 20;
        float verticalPad = 20;
        float height = Gdx.graphics.getHeight() - verticalPad * 2 - 80;
        decksListWindow.setBounds(60, 40, 1500, height * 2 / 9f);
        activeDeckWindow.setBounds(decksListWindow.getX() + decksListWindow.getWidth() + horizontalPad,
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

    private void arrangeWidgets() {
        arrangeWindows();
        selectedCard.setWidth(cardPreviewWindow.getWidth() * 0.8f);
        selectedCard.setHeight(cardPreviewWindow.getHeight() * 0.8f);
        cardPreviewWindow.add(selectedCard).fill().center();
        ScrollPane inventoryScrollPane = new ScrollPane(inventoryCards.getCardsTable(), AssetController.getSkin("first"));
        inventoryScrollPane.setFillParent(true);
        inventoryScrollPane.setFlickScroll(false);
        inventoryScrollPane.setSmoothScrolling(true);
        inventoryWindow.add(inventoryScrollPane).fill().padRight(10).padLeft(10).center();
        ScrollPane sideDeckScrollPane = new ScrollPane(sideDeckCards.getCardsTable(), AssetController.getSkin("first"), "vertical");
        sideDeckScrollPane.setFillParent(true);
        sideDeckScrollPane.setFlickScroll(false);
        sideDeckScrollPane.setSmoothScrolling(true);
        sideDeckWindow.add(sideDeckScrollPane).fill().left();
        ScrollPane mainDeckScrollPane = new ScrollPane(mainDeckCards.getCardsTable(), AssetController.getSkin("first"));
        mainDeckScrollPane.setFillParent(true);
        mainDeckScrollPane.setFlickScroll(false);
        mainDeckScrollPane.setSmoothScrolling(true);
        mainDeckWindow.add(mainDeckScrollPane).fill().left();
        arrangeDeckListWidgets();
    }

    private void arrangeDeckListWidgets(){
        ScrollPane deckScrollPane = new ScrollPane(decksListTable,AssetController.getSkin("first"),"horizontal");
        deckScrollPane.setSize(decksListWindow.getWidth()*0.9f,decksListWindow.getHeight()*0.8f);
        deckScrollPane.setPosition(decksListWindow.getX() + 20,decksListWindow.getY()+60);
        deckScrollPane.setSmoothScrolling(true);
        stage.addActor(deckScrollPane);
        backButton.setSize(decksListWindow.getWidth()*0.2f,60);
        newDeckButton.setSize(decksListWindow.getWidth()*0.2f,60);
        deleteDeckButton.setSize(decksListWindow.getWidth()*0.2f,60);
        activateButton.setSize(decksListWindow.getWidth()*0.2f,60);

        backButton.setPosition(decksListWindow.getX()+110,decksListWindow.getY()+10);
        newDeckButton.setPosition(backButton.getX() +backButton.getWidth() +20,decksListWindow.getY()+10);
        deleteDeckButton.setPosition(newDeckButton.getX() + newDeckButton.getWidth() +20,decksListWindow.getY()+10);
        activateButton.setPosition(deleteDeckButton.getX()+ deleteDeckButton.getWidth() + 20,decksListWindow.getY()+10);
        decksListWindow.setTouchable(Touchable.disabled);
        stage.addActor(backButton);
        stage.addActor(newDeckButton);
        stage.addActor(deleteDeckButton);
        stage.addActor(activateButton);
        activeDeckPreview = new DeckListElement("----",DeckMenuController.getInstance().getUser().getId(),
                activeDeckWindow.getWidth()*0.5f,activeDeckWindow.getHeight()*0.85f,10);
        activeDeckWindow.add(activeDeckPreview).expand().fill();
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
                showDeckNameAskDialog();
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

    public void selectCard(CardActor cardActor, CardsContainer container) {
        draggingCardContainer = container;
        draggingCard = CardActor.clone(cardActor);
        selectedCard.getCardSprite().setTexture(cardActor.getCardSprite().getTexture());
    }

    public void updateCardContainers(){
        loadUserInventory();
        loadSideDeckCards(DeckMenuController.getInstance().getSelectedDeck());
        loadMainDeckCards(DeckMenuController.getInstance().getSelectedDeck());
    }

    public void updateDeckList(){
        DeckMenuController.getInstance().loadUserDecks();
        activeDeckPreview.setIsSelectable(false);
        activeDeckPreview.setDeck(DeckMenuController.getInstance().getUserActiveDeck());
        List<DeckListElement> deckElements = new ArrayList<>();
        for(Deck deck:DeckMenuController.getInstance().getUserDecks()){
            deckElements.add(new DeckListElement(deck.getName(),DeckMenuController.getInstance().getUser().getId(),
                    100,160,9));
        }
        decksListTable.clearChildren();
        for(DeckListElement deckListElement: deckElements){
            decksListTable.add(deckListElement).padRight(20).padLeft(20);
            deckListElement.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectDeck(deckListElement);
                }
            });
        }
    }

    public void loadUserInventory() {
        DeckMenuController.getInstance().loadUserInventory();
        Inventory userInventory = DeckMenuController.getInstance().getUserInventory();
        Map<String, Integer> inventoryMonsters = userInventory.getMonsters();
        Map<String, Integer> inventorySpells = userInventory.getSpells();
        Map<String, Integer> inventoryTraps = userInventory.getTraps();
        inventoryCards.updateCards(inventoryMonsters, inventorySpells, inventoryTraps);
    }

    private void newDeck(String deckName) {
        DeckMenuController.getInstance().createDeck(deckName);
        updateDeckList();
        updateCardContainers();
    }

    private void deleteDeck() {
        DeckMenuController.getInstance().deleteDeck();
        updateDeckList();
        updateCardContainers();
    }

    private void activateDeck() {
        DeckMenuController.getInstance().activateDeck();
        updateDeckList();
    }

    public void selectDeck(DeckListElement selectedDeck) {
        DeckMenuController.getInstance().selectDeck(selectedDeck.getDeck());
        loadDeck(selectedDeck.getDeck());
        updateDeckList();
    }

    private void loadDeck(Deck deck) {
        loadMainDeckCards(deck);
        loadSideDeckCards(deck);
    }

    private void loadMainDeckCards(Deck deck) {
        if (deck == null) {
            mainDeckCards.updateCards(null, null, null);
            return;
        }
        Map<String, Integer> mainDeckMonsters = deck.getMonsters(false);
        Map<String, Integer> mainDeckSpells = deck.getSpells(false);
        Map<String, Integer> mainDeckTraps = deck.getTraps(false);
        mainDeckCards.updateCards(mainDeckMonsters, mainDeckSpells, mainDeckTraps);
    }

    private void loadSideDeckCards(Deck deck) {
        if (deck == null) {
            sideDeckCards.updateCards(null, null, null);
            return;
        }
        Map<String, Integer> sideDeckMonsters = deck.getMonsters(true);
        Map<String, Integer> sideDeckSpells = deck.getSpells(true);
        Map<String, Integer> sideDeckTraps = deck.getTraps(true);
        sideDeckCards.updateCards(sideDeckMonsters, sideDeckSpells, sideDeckTraps);
    }

    private void dragCard(float mouseX, float mouseY) {
        //dragged to Inventory
        if (isInsideWindow(inventoryWindow, mouseX, mouseY)) {
            if (draggingCardContainer != inventoryCards) {
                AssetController.playSound("flip");
                if (draggingCardContainer == mainDeckCards)
                    removeCardFromMainDeck(draggingCard.getCard());
                if (draggingCardContainer == sideDeckCards)
                    removeCardFromSideDeck(draggingCard.getCard());
            }
        }
        //dragged to sideDeck
        if (isInsideWindow(sideDeckWindow, mouseX, mouseY)) {
            if (draggingCardContainer != sideDeckCards)
                AssetController.playSound("flip");
            addCardToSideDeck(draggingCard.getCard());
        }
        //dragged to mainDeck
        if (isInsideWindow(mainDeckWindow, mouseX, mouseY)) {
            if (draggingCardContainer != mainDeckCards)
                AssetController.playSound("flip");
            addCardToMainDeck(draggingCard.getCard());
        }
        draggingCard = null;
        draggingCardContainer = null;
        updateCardContainers();
    }

    private void addCardToMainDeck(Card card) {
        if (draggingCardContainer == mainDeckCards)
            return;
        if (draggingCardContainer == sideDeckCards)
            removeCardFromSideDeck(card);
        DeckMenuController.getInstance().addCardToMainDeck(card);
    }

    private void addCardToSideDeck(Card card) {
        if (draggingCardContainer == sideDeckCards)
            return;
        if (draggingCardContainer == mainDeckCards)
            removeCardFromMainDeck(card);
        DeckMenuController.getInstance().addCardToSideDeck(card);
    }

    private void removeCardFromMainDeck(Card card){
        DeckMenuController.getInstance().removeCardFromMainDeck(card);
    }

    private void removeCardFromSideDeck(Card card){
        DeckMenuController.getInstance().removeCardFromSideDeck(card);
    }

    private void showDeckNameAskDialog(){
        Dialog dialog = new Dialog("Enter New Deck's Name:",AssetController.getSkin("first"));
        TextButton okButton = new TextButton("Ok",AssetController.getSkin("first"));
        TextButton cancelButton = new TextButton("Cancel",AssetController.getSkin("first"));
        TextField deckNameField = new TextField("",AssetController.getSkin("first"));
        deckNameField.setMessageText("New Deck's Name...");
        deckNameField.setAlignment(3);
        dialog.setSize(300,200);
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);
        Runnable closeAction = () -> {
            dialog.hide();
            dialog.cancel();
            dialog.remove();
        };
        okButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                newDeck(deckNameField.getText());
                closeAction.run();
            }
        });
        cancelButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                closeAction.run();
            }
        });
        dialog.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    newDeck(deckNameField.getText());
                    closeAction.run();
                }
                if (keycode == Input.Keys.ESCAPE)
                    closeAction.run();
                return super.keyDown(event,keycode);
            }
        });
        dialog.getContentTable().add(deckNameField).fill().expandX().padLeft(50).padRight(50).height(70).colspan(2);
        dialog.getButtonTable().add(cancelButton).fill().expand().colspan(1).height(110);
        dialog.getButtonTable().add(okButton).fill().expand().colspan(1).height(110);
        dialog.show(stage);
    }

    public void showErrorDialog(String errorMessage) {
        Dialog dialog = new Dialog("",AssetController.getSkin("first"));
        TextButton okButton = new TextButton("Ok",AssetController.getSkin("first"));
        Label errorMessageLabel = new Label(errorMessage,AssetController.getSkin("first"),"title");
        errorMessageLabel.getStyle().fontColor = Color.WHITE;
        dialog.setModal(true);
        dialog.setMovable(false);
        dialog.setResizable(false);
        Runnable okAction = () -> {
            dialog.hide();
            dialog.cancel();
            dialog.remove();
        };
        okButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                okAction.run();
            }
        });
        dialog.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.ENTER)
                    okAction.run();
                return super.keyDown(event,keycode);
            }
        });
        dialog.getContentTable().add(errorMessageLabel).fill().expandX().padLeft(10).padRight(10);
        dialog.getButtonTable().add(okButton).fill().expand().height(110);
        dialog.show(stage);
    }

    private boolean isInsideWindow(Window window, float x, float y) {
        if (window.getX() < x && window.getX() + window.getWidth() > x
                && window.getY() < y && window.getY() + window.getHeight() > y)
            return true;
        return false;
    }

}

