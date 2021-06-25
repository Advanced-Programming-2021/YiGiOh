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

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.DeckMenuController;
import edu.sharif.ce.apyugioh.model.Deck;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;

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
    private Card selectedCard;
    private Deck selectedDeck;
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
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    private void initialize(){
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
        ScrollPane scrollPane = new ScrollPane(inventoryCardTable,AssetController.getSkin("first"));
        scrollPane.setFillParent(true);
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

    private void selectCard(Card card){

    }

    private void loadUserDecks(){
        for(int i = 1;i<=10;++i) {
            for (int j = 1; j <= 10; ++j) {
                TextButton button = new TextButton("Razi" + i + j, AssetController.getSkin("first"));
                inventoryCardTable.add(button).height(200).width(150).padRight(50);
            }
            inventoryCardTable.row();
        }
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

    private void addListeners(){
        stage.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE)
                    DeckMenuController.getInstance().back();
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
