package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectSet;
import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.CardFactoryMenuController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.model.ProfilePicture;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.model.CardModelView;

import javax.swing.*;
import java.util.HashMap;

public class CardFactoryMenu extends Menu {

    private final int TRANSITION_SPEED = 3;

    private Stage stage;

    private Window effectsWindow;
    private Label effectsLabel;
    private Table effectsTable;

    private Window cardViewWindow;
    private Sprite cardViewSprite;
    private TextField cardNameField;

    private TextButton backButton;
    private TextButton loadCardViewButton;
    private TextButton addCardButton;
    private TextButton importButton;
    private TextButton exportButton;

    private Window monsterOptionsWindow;
    private Label attackPointsTitleLabel;
    private Slider attackPointsSlider;
    private Label attackPointsLabel;
    private Label defensePointsTitleLabel;
    private Slider defensePointsSlider;
    private Label defensePointsLabel;

    private Window cardEffectsWindow;
    private Label cardEffectsLabel;
    private Table cardEffectTable;

    private Label roleLabel;
    private CheckBox monsterCheckBox;
    private CheckBox spellCheckBox;

    private SpriteBatch batch;
    private Texture backgroundTexture;
    private ObjectSet<CardModelView> cards;
    private ProfilePicture profilePicture;

    public CardFactoryMenu(YuGiOh game) {
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
        cards = new ObjectSet<>();
        AssetController.loadDeck();
        CardModelView card = AssetController.getDeck().getRandom();
        card.setTranslation(35, 0, 0);
        cards.add(card);
        card = AssetController.getDeck().getRandom();
        card.setTranslation(35, 0, 13);
        cards.add(card);
        card = AssetController.getDeck().getRandom();
        card.setTranslation(35, 0, -13);
        cards.add(card);
        initialize();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cardViewSprite.setSize(350, 520);
        cardViewSprite.setPosition(cardViewWindow.getX() + cardViewWindow.getWidth() / 2 - cardViewSprite.getWidth() * 0.5f,
                cardViewWindow.getY() + 100);
        cardViewSprite.draw(batch);
        stage.act(delta);
        stage.draw();
        batch.end();

    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    private void initialize() {
        createProfileDetails();
        initializeEffectsWindow();
        initializeCardViewWindow();
        initializeButtons();
        initializeMonsterOptionsWindow();
        initializeCardEffectsWindow();

        addListeners();
        updateCardRole(monsterCheckBox);
    }

    private void initializeEffectsWindow(){
        effectsWindow = new Window("", AssetController.getSkin("first"), "left");
        effectsLabel = new Label("Effects", AssetController.getSkin("first"), "title");
        effectsLabel.setAlignment(3);
        effectsTable = new Table();

        effectsWindow.addActor(effectsLabel);
        effectsWindow.addActor(effectsTable);
        stage.addActor(effectsWindow);

        effectsWindow.setSize(650, Gdx.graphics.getHeight() * 0.7f - profilePicture.getHeight());
        effectsWindow.setPosition(0, Gdx.graphics.getHeight() / 2 - effectsWindow.getHeight() * 0.75f);
        effectsLabel.setPosition(effectsWindow.getWidth() * 0.6f - effectsLabel.getWidth() * 0.5f,
                effectsWindow.getHeight() - effectsLabel.getHeight() - 80);
        effectsTable.setSize(effectsWindow.getWidth() * 0.6f, effectsWindow.getHeight() * 0.7f);
        effectsTable.setPosition(effectsWindow.getWidth() * 0.6f - effectsTable.getWidth() * 0.5f,
                effectsLabel.getY() - effectsTable.getHeight() - 50);

    }

    private void initializeCardViewWindow(){
        cardViewWindow = new Window("", AssetController.getSkin("first"));
        cardViewSprite = new Sprite(new Texture(Gdx.files.local("assets/cards/monster/Unknown.jpg")));
        cardNameField = new TextField("", AssetController.getSkin("first"));
        cardNameField.setMessageText("no card is loaded yet...");
        cardNameField.setAlignment(3);

        cardViewWindow.addActor(cardNameField);
        stage.addActor(cardViewWindow);

        cardViewWindow.setSize(500, 900);
        cardViewWindow.setPosition(Gdx.graphics.getWidth() / 2 - cardViewWindow.getWidth() / 2,
                Gdx.graphics.getHeight() - cardViewWindow.getHeight());
        cardNameField.setSize(cardViewWindow.getWidth() * 0.8f, 40);
        cardNameField.setPosition(cardViewWindow.getWidth() / 2 - cardNameField.getWidth() / 2, 45);

    }

    private void initializeMonsterOptionsWindow(){
        monsterOptionsWindow = new Window("",AssetController.getSkin("first"));
        monsterOptionsWindow.setKeepWithinStage(false);
        attackPointsTitleLabel = new Label("Attack Pts.",AssetController.getSkin("first"));
        attackPointsSlider = new Slider(0,3000,50,false,AssetController.getSkin("first"));
        attackPointsLabel = new Label("1000",AssetController.getSkin("first"));
        defensePointsTitleLabel = new Label("Defense Pts.",AssetController.getSkin("first"));
        defensePointsSlider = new Slider(0,3000,50,false,AssetController.getSkin("first"));
        defensePointsLabel = new Label("1000",AssetController.getSkin("first"));

        monsterOptionsWindow.addActor(attackPointsTitleLabel);
        monsterOptionsWindow.addActor(attackPointsSlider);
        monsterOptionsWindow.addActor(attackPointsLabel);
        monsterOptionsWindow.addActor(defensePointsTitleLabel);
        monsterOptionsWindow.addActor(defensePointsSlider);
        monsterOptionsWindow.addActor(defensePointsLabel);
        stage.addActor(monsterOptionsWindow);

        monsterOptionsWindow.setSize(400,300);
        monsterOptionsWindow.setPosition(cardViewWindow.getX() + cardViewWindow.getWidth() + 70,
                Gdx.graphics.getHeight() - monsterOptionsWindow.getHeight());
        attackPointsTitleLabel.setPosition(40,monsterOptionsWindow.getHeight()*0.45f);
        attackPointsSlider.setPosition(attackPointsTitleLabel.getX() + attackPointsTitleLabel.getWidth() + 20
        ,attackPointsTitleLabel.getY());
        attackPointsLabel.setPosition(attackPointsSlider.getX() + attackPointsSlider.getWidth() + 20,
                attackPointsSlider.getY());
        defensePointsTitleLabel.setPosition(attackPointsTitleLabel.getX(),
                attackPointsTitleLabel.getY() - defensePointsTitleLabel.getHeight() - 20);
        defensePointsSlider.setPosition(defensePointsTitleLabel.getX() + defensePointsTitleLabel.getWidth() + 20,
                defensePointsTitleLabel.getY());
        defensePointsLabel.setPosition(defensePointsSlider.getX() + defensePointsSlider.getWidth() + 20,
                defensePointsSlider.getY());
    }

    private void initializeCardEffectsWindow(){
        cardEffectsWindow = new Window("", AssetController.getSkin("first"), "right");
        cardEffectsLabel = new Label("Card Effects", AssetController.getSkin("first"), "title");
        cardEffectTable = new Table();
        roleLabel = new Label("Role: ",AssetController.getSkin("first"),"title");
        monsterCheckBox = new CheckBox("Monster",AssetController.getSkin("first"));
        spellCheckBox = new CheckBox("Spell",AssetController.getSkin("first"));

        cardEffectsWindow.addActor(cardEffectsLabel);
        cardEffectsWindow.addActor(cardEffectTable);
        cardEffectsWindow.addActor(roleLabel);
        cardEffectsWindow.addActor(monsterCheckBox);
        cardEffectsWindow.addActor(spellCheckBox);
        stage.addActor(cardEffectsWindow);

        cardEffectsWindow.setSize(650, Gdx.graphics.getHeight() * 0.7f - profilePicture.getHeight());
        cardEffectsWindow.setPosition(Gdx.graphics.getWidth() - cardEffectsWindow.getWidth(),
                Gdx.graphics.getHeight() / 2 - cardEffectsWindow.getHeight() * 0.75f);
        cardEffectsLabel.setPosition(150, cardEffectsWindow.getHeight() - cardEffectsLabel.getHeight() - 100);
        cardEffectTable.setSize(300, effectsWindow.getHeight() * 0.7f);
        cardEffectTable.setPosition(50, cardEffectsLabel.getY() - cardEffectTable.getHeight() - 50);

        roleLabel.setPosition(100,60);
        monsterCheckBox.setPosition(roleLabel.getX() + roleLabel.getWidth() + 20,roleLabel.getY() + 5);
        spellCheckBox.setPosition(monsterCheckBox.getX() + monsterCheckBox.getWidth() + 20,monsterCheckBox.getY());

    }

    private void initializeButtons(){
        backButton = new TextButton("Back", AssetController.getSkin("first"));
        loadCardViewButton = new TextButton("Load Image...", AssetController.getSkin("first"));
        addCardButton = new TextButton("Add to main cards", AssetController.getSkin("first"));
        importButton = new TextButton("Import...", AssetController.getSkin("first"));
        exportButton = new TextButton("Export...", AssetController.getSkin("first"));

        stage.addActor(backButton);
        stage.addActor(loadCardViewButton);
        stage.addActor(addCardButton);
        stage.addActor(importButton);
        stage.addActor(exportButton);

        backButton.setSize(167, 80);
        importButton.setSize(167, 80);
        exportButton.setSize(167, 80);
        addCardButton.setSize(245.5f, 80);
        loadCardViewButton.setSize(245.5f, 80);

        addCardButton.setPosition(cardViewWindow.getX() + 3, cardViewWindow.getY() - 80);
        loadCardViewButton.setPosition(addCardButton.getX() + 248.5f, addCardButton.getY());

        backButton.setPosition(cardViewWindow.getX(), cardViewWindow.getY() - 160);
        importButton.setPosition(backButton.getX() + 167, backButton.getY());
        exportButton.setPosition(importButton.getX() + 167, importButton.getY());
    }

    private void addListeners() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    CardFactoryMenuController.getInstance().back();
                    return true;
                }
                return super.keyDown(event, keycode);
            }
        });
        backButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                CardFactoryMenuController.getInstance().back();
            }
        });
        attackPointsSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                attackPointsLabel.setText((int)attackPointsSlider.getValue());
            }
        });
        defensePointsSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                defensePointsLabel.setText((int)defensePointsSlider.getValue());
            }
        });
        monsterCheckBox.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateCardRole(monsterCheckBox);
            }
        });
        spellCheckBox.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateCardRole(spellCheckBox);
            }
        });
    }

    private void createProfileDetails() {
        if (profilePicture != null)
            profilePicture.remove();
        Image image = new Image(new Texture(Gdx.files.internal("skins/profile_frame.png")));
        image.setPosition(170, Gdx.graphics.getHeight() - 200);
        Table table = new Table(AssetController.getSkin("first"));
        Label usernameLabel = new Label("Username: " + MainMenuController.getInstance().getUser().getUsername(), AssetController.getSkin("first"), "title");
        Label nicknameLabel = new Label("Nickname: " + MainMenuController.getInstance().getUser().getNickname(), AssetController.getSkin("first"));
        table.add(usernameLabel).spaceBottom(5).left();
        table.row();
        table.add(nicknameLabel).left();
        table.setPosition(300 + image.getWidth(), Gdx.graphics.getHeight() - 150);
        profilePicture = new ProfilePicture(Gdx.files.local("assets/db/profiles/" + MainMenuController.getInstance().getUser().getAvatarName()), true);
        stage.addActor(image);
        stage.addActor(table);
        stage.addActor(profilePicture);
    }

    private void showMonsterOptionsWindow(){
        if (monsterOptionsWindow.getY() == Gdx.graphics.getHeight()-monsterOptionsWindow.getHeight())
            return;
        AssetController.playSound("chain");
        monsterOptionsWindow.addAction(Actions.moveTo(monsterOptionsWindow.getX(),
                Gdx.graphics.getHeight() - monsterOptionsWindow.getHeight(),TRANSITION_SPEED));
    }

    private void hideMonsterOptionsWindow(){
        if (monsterOptionsWindow.getY() == Gdx.graphics.getHeight())
            return;
        AssetController.playSound("chain");
        monsterOptionsWindow.addAction(Actions.moveTo(monsterOptionsWindow.getX(),Gdx.graphics.getHeight(),TRANSITION_SPEED));
    }

    private void updateCardRole(CheckBox selectedBox){
        monsterCheckBox.setChecked(false);
        spellCheckBox.setChecked(false);
        selectedBox.setChecked(true);
        if (selectedBox == monsterCheckBox)
            showMonsterOptionsWindow();
        else
            hideMonsterOptionsWindow();
    }
}
