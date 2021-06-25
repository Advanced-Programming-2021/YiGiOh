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

import org.apache.commons.text.TextStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.DeckMenuController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.model.CardActor;

public class DeckMenuView extends Menu {

    private Stage stage;
    private Texture backgroundTexture;

    private Window inventoryWindow;
    private Table inventoryCardTable;

    private Window cardPreviewWindow;

    private Window sideDeckWindow;
    private Table sideDeckCardsTable;

    private Window mainDeckWindow;
    private Table mainDeckCardsTable;

    private Window decksListWindow;
    private Table decksListTable;
    private TextButton backButton;
    private TextButton selectAsMainDeckButton;
    private TextButton deleteDeckButton;
    private TextButton newDeckButton;

    private Window currentDeckWindow;

    private SpriteBatch batch;
    private CardActor selectedCard;
    private CardActor draggingCard;
    private Deck selectedDeck;
    private Inventory userInventory;
    private ArrayList<CardActor> inventoryCards;
    private ArrayList<Deck> userDecks;



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
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT) && draggingCard != null) {
            cardDragged();
            draggingCard = null;
        }
        if (draggingCard != null){
            draggingCard.setPosition(Gdx.input.getX(),Gdx.graphics.getHeight()-Gdx.input.getY());
            stage.getBatch().begin();
            draggingCard.draw(stage.getBatch(),1);
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

    private void initialize(){
        selectedCard = new CardActor();
        inventoryCards = new ArrayList<>();
        userDecks = new ArrayList<>();
        inventoryWindow = new Window("Inventory", AssetController.getSkin("first"));
        inventoryCardTable = new Table();
        cardPreviewWindow = new Window("Preview", AssetController.getSkin("first"));
        sideDeckWindow = new Window("Side Deck", AssetController.getSkin("first"));
        sideDeckCardsTable = new Table();
        mainDeckWindow = new Window("Main Deck", AssetController.getSkin("first"));
        mainDeckCardsTable = new Table();
        decksListWindow = new Window("Your Decks", AssetController.getSkin("first"));
        decksListTable = new Table();
        currentDeckWindow = new Window("current Deck", AssetController.getSkin("first"));
        backButton =  new TextButton("Back", AssetController.getSkin("first"));
        selectAsMainDeckButton =  new TextButton("Select As Main", AssetController.getSkin("first"));
        newDeckButton = new TextButton("New Deck...",AssetController.getSkin("first"));
        deleteDeckButton = new TextButton("delete Deck", AssetController.getSkin("first"));
        stage.addActor(inventoryWindow);
        stage.addActor(cardPreviewWindow);
        stage.addActor(sideDeckWindow);
        stage.addActor(mainDeckWindow);
        stage.addActor(decksListWindow);
        stage.addActor(currentDeckWindow);
        arrangeWindows();
        addListeners();
    }

    private void arrangeWindows(){
        float horizontalPad = 20;
        float verticalPad = 20;
        float height = Gdx.graphics.getHeight()-verticalPad*2-80;
        decksListWindow.setBounds(60,40,1500,height*2/9f);
        currentDeckWindow.setBounds(decksListWindow.getX()+decksListWindow.getWidth()+horizontalPad,
                40,240,height*2/9f);
        sideDeckWindow.setBounds(60,decksListWindow.getY()+decksListWindow.getHeight()+verticalPad,
                600,height*3/9f);
        mainDeckWindow.setBounds(sideDeckWindow.getX()+sideDeckWindow.getWidth()+horizontalPad,
                decksListWindow.getY()+decksListWindow.getHeight()+verticalPad,
                1140,height*3/9f);
        inventoryWindow.setBounds(60,sideDeckWindow.getY()+sideDeckWindow.getHeight()+verticalPad,
                1440,height*4/9f);
        cardPreviewWindow.setBounds(inventoryWindow.getX() + inventoryWindow.getWidth() + horizontalPad,
                sideDeckWindow.getY()+sideDeckWindow.getHeight()+verticalPad,
                300,height*4/9f);
        selectedCard.setWidth(cardPreviewWindow.getWidth()*0.9f);
        selectedCard.setHeight(cardPreviewWindow.getHeight()*0.9f);
        cardPreviewWindow.add(selectedCard).fill().center();
        ScrollPane scrollPane = new ScrollPane(inventoryCardTable,AssetController.getSkin("first"));
        scrollPane.setFillParent(true);
        scrollPane.setFlickScroll(false);
        inventoryWindow.add(scrollPane).fill().padRight(10).padLeft(10).center();
        sideDeckWindow.add(new ScrollPane(sideDeckCardsTable,AssetController.getSkin("first"))).fill();
        mainDeckWindow.add(new ScrollPane(mainDeckCardsTable,AssetController.getSkin("first"))).fill();
        decksListWindow.add(new ScrollPane(decksListTable,AssetController.getSkin("first"))).expandX().colspan(4);
        decksListWindow.row();
        decksListWindow.add(backButton).colspan(1).fillX().height(60);
        decksListWindow.add(newDeckButton).colspan(1).fillX().height(60);
        decksListWindow.add(deleteDeckButton).colspan(1).fillX().height(60);
        decksListWindow.add(selectAsMainDeckButton).colspan(1).fillX().height(60);
    }

    private void selectCard(CardActor cardActor){
        selectedCard.getCardSprite().setTexture(cardActor.getCardSprite().getTexture());
    }

    private void loadUserInventory(){
        userInventory = Inventory.getInventoryByUserID(DeckMenuController.getInstance().getUser().getId());
        Map<String,Integer> inventoryMonsters = userInventory.getMonsters();
        for(String cardName:inventoryMonsters.keySet()) {
            inventoryCards.add(new CardActor(cardName, CardType.MONSTER, 200,
                    340,inventoryMonsters.get(cardName)));
        }
        Map<String,Integer> inventorySpells = userInventory.getSpells();
        for(String cardName:inventorySpells.keySet()) {
            inventoryCards.add(new CardActor(cardName, CardType.SPELL, 200,
                    340,inventorySpells.get(cardName)));
        }
        Map<String,Integer> inventoryTraps = userInventory.getTraps();
        for(String cardName:inventoryTraps.keySet()) {
            inventoryCards.add(new CardActor(cardName, CardType.TRAP, 200,
                    340,inventoryTraps.get(cardName)));
        }
        for(int i = 0;i<inventoryCards.size();++i){
            CardActor cardActor = inventoryCards.get(i);
            cardActor.addListener(new ClickListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    selectCard(cardActor);
                    draggingCard = CardActor.clone(cardActor);
                    return super.touchDown(event,x,y,pointer,button);
                }
            });
            if (i%5==0 && i>0)
                inventoryCardTable.row();
            if (i%5==0)
                inventoryCardTable.add(cardActor).padLeft(50).padRight(50).expandX().padTop(20);
            else if ((i+1)%5==0)
                inventoryCardTable.add(cardActor).padRight(50).expandX().padTop(20);
            else
                inventoryCardTable.add(cardActor).padRight(50).expandX().padTop(20);
        }
    }

    private void loadUserDecks(){

        if (userDecks.size()>0)
            selectDeck(userDecks.get(0));
    }

    private void selectDeck(Deck deck){
        selectedDeck = deck;
        updateSelectedDeck(deck);
        loadDeck(deck);
    }

    private void loadDeck(Deck deck){
        loadMainDeckCards(deck);
        loadSideDeckCards(deck);
    }

    private void loadMainDeckCards(Deck deck){

    }

    private void loadSideDeckCards(Deck deck){

    }

    private void updateSelectedDeck(Deck deck){

    }

    private void cardDragged(){

    }

    private void addListeners(){
        stage.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE)
                    DeckMenuController.getInstance().back();
                if (keycode == Input.Keys.ENTER)
                    draggingCard = null;
                return super.keyDown(event,keycode);
            }
        });
        backButton.addListener(new ButtonClickListener(){
            @Override
            public void clickAction() {
                DeckMenuController.getInstance().back();
            }
        });
    }

}
