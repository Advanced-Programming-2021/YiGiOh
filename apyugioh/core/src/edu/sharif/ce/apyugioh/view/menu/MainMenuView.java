package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.HashMap;

import com.sun.tools.javac.comp.Check;
import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.DeckMenuController;
import edu.sharif.ce.apyugioh.controller.DuelController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ProfileController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.controller.UserController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.AILevel;
import edu.sharif.ce.apyugioh.model.ProfilePicture;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.model.CardModelView;
import org.w3c.dom.Text;

public class MainMenuView extends Menu {


    public static final int SUCCESS_LOGOUT = 1;

    {
        successMessages.put(SUCCESS_LOGOUT, "user logged out successfully!");
    }

    private float TRANSITION_SPEED = 3;
    private boolean loaded;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private StartGameDialog startGameDialog;
    private ObjectSet<CardModelView> cards;
    private HashMap<String, Window> windows;
    private ProfilePicture profilePicture;


    public MainMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 15, 5, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        batch = new SpriteBatch();
        stage = new Stage();
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/main" + MathUtils.random(1, 10) + ".jpg"));
        windows = new HashMap<>();
    }

    @Override
    public void show() {
        super.show();
        cards = new ObjectSet<>();
        CardModelView card = AssetController.getDeck().getRandom();
        card.setTranslation(35, 0, 0);
        cards.add(card);
        card = AssetController.getDeck().getRandom();
        card.setTranslation(35, 0, 13);
        cards.add(card);
        card = AssetController.getDeck().getRandom();
        card.setTranslation(35, 0, -13);
        cards.add(card);
        createMainWindow();
        createProfileDetails();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (!loaded && AssetController.getAssets().update()) {
            Model dragon = AssetController.getAssets().get("3D/puzzle/puzzle.g3db", Model.class);
            ModelInstance instance = new ModelInstance(dragon);
            instance.transform.scale(0.4f, 0.4f, 0.4f);
            instance.transform.setTranslation(18, -8, -14);
            instance.transform.rotate(0, -1, 0, 90);
            instance.materials.get(0).set(new ColorAttribute(ColorAttribute.Diffuse, Color.GOLD));
            instances.add(instance);
            instances.add(instance.copy());
            instances.get(1).transform.setTranslation(18, -8, 14);
            loaded = true;
        } else if (loaded) {
            instances.get(0).transform.rotate(0, -1, 0, 100 * Gdx.graphics.getDeltaTime());
            instances.get(1).transform.rotate(0, 1, 0, 100 * Gdx.graphics.getDeltaTime());
        }
        super.render(delta);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        for (CardModelView card : cards) {
            card.rotate(0, 1, 0, 200 * Gdx.graphics.getDeltaTime());
            card.render(modelBatch, environment);
        }
        modelBatch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }

    private void createMainWindow() {
        TextButton[] mainMenuButtons = new TextButton[]{
                new TextButton("Play", AssetController.getSkin("first")),
                new TextButton("Deck", AssetController.getSkin("first")),
                new TextButton("Scoreboard", AssetController.getSkin("first")),
                new TextButton("Shop", AssetController.getSkin("first")),
                new TextButton("Profile", AssetController.getSkin("first")),
                new TextButton("Logout", AssetController.getSkin("first"))};
        Window window = new Window("", AssetController.getSkin("first"));
        window.setKeepWithinStage(false);
        window.setWidth(542);
        window.setHeight(940);
        window.setPosition(Gdx.graphics.getWidth() / 2 - 271, Gdx.graphics.getHeight() - 940);
        for (TextButton mainMenuButton : mainMenuButtons) {
            if (mainMenuButton.getText().toString().equals("Shop")) {
                mainMenuButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        ShopController.getInstance().setUser(MainMenuController.getInstance().getUser());
                        ShopController.getInstance().showShop();
                    }
                });
            }
            if (mainMenuButton.getText().toString().equals("Play")) {
                mainMenuButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        //DuelController.getInstance().startSinglePlayerDuel(MainMenuController.getInstance().getUser().getUsername(), AILevel.MEDIOCRE, 1);
                        //GameController.showGame();
                        AssetController.playSound("chain");
                        startGameDialog.addAction(Actions.moveTo(Gdx.graphics.getWidth()-startGameDialog.getWidth(),startGameDialog.getY(),TRANSITION_SPEED));
                        window.addAction(Actions.moveTo(window.getX(),Gdx.graphics.getHeight(),TRANSITION_SPEED));
                    }
                });
            }
            if (mainMenuButton.getText().toString().equals("Deck")) {
                mainMenuButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        DeckMenuController.getInstance().setUser(MainMenuController.getInstance().getUser());
                        DeckMenuController.getInstance().showDeckMenu();
                    }
                });
            }
            if (mainMenuButton.getText().toString().equals("Logout")) {
                mainMenuButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        UserController.getInstance().logoutUser();
                    }
                });
            }
            if (mainMenuButton.getText().toString().equals("Profile")) {
                mainMenuButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        ProfileController.getInstance().setUser(MainMenuController.getInstance().getUser());
                        ProfileController.getInstance().showProfile();
                    }
                });
            }
            window.add(mainMenuButton).width(300).height(80).spaceBottom(10);
            window.row();
        }

        window.padTop(150);
        windows.put("main", window);
        stage.addActor(window);
        createStartGameDialog();
    }

    private void createStartGameDialog() {
        startGameDialog = new StartGameDialog("", AssetController.getSkin("first"));
        startGameDialog.setSize(1000,900);
        startGameDialog.setKeepWithinStage(false);
        System.out.println(startGameDialog.getWidth());
        startGameDialog.setPosition(Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()/2-startGameDialog.getHeight()/2);
        windows.put("start",startGameDialog);
        stage.addActor(startGameDialog);
    }

    private void createProfileDetails() {
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

    public void cancelStartGame(){
        Window mainWindow = windows.get("main");
        Window startWindow = windows.get("start");
        AssetController.playSound("chain");
        mainWindow.addAction(Actions.moveTo(mainWindow.getX(),Gdx.graphics.getHeight()-mainWindow.getHeight(),TRANSITION_SPEED));
        startWindow.addAction(Actions.moveTo(Gdx.graphics.getWidth(),startWindow.getY(),TRANSITION_SPEED));
    }

    private void startGame(String firstPlayerUsername, String secondPlayerUsername, int rounds, AILevel aiLevel) {

    }
}

class StartGameDialog extends Window {

    private String rivalUsername;
    private AILevel aiLevel;
    private int rounds;

    //widgets
    private Label roundsTitleLabel;
    private Slider roundsSlider;
    private Label roundsLabel;
    private Label modeLabel;
    private CheckBox modeCheckBox;
    private Label AiLevelLabel;
    private CheckBox easyCheckBox;
    private CheckBox mediocreCheckBox;
    private CheckBox hardCheckBox;
    private Label rivalUsernameLabel;
    private TextField rivalUsernameField;
    private TextButton startButton;
    private TextButton cancelButton;

    public StartGameDialog(String title, Skin skin) {
        super(title, skin,"right");
        initializeWidgets();
        arrangeWidgets();
        addListeners();
        rivalUsername = "";
        aiLevel = AILevel.EASY;
        rounds = 1;
    }

    private void initializeWidgets() {
        roundsTitleLabel = new Label("Rounds: ", AssetController.getSkin("first"), "title");
        roundsSlider = new Slider(1, 3, 1, false, AssetController.getSkin("first"));
        roundsLabel = new Label("1", AssetController.getSkin("first"));
        modeLabel = new Label("Mode: ", AssetController.getSkin("first"), "title");
        modeCheckBox = new CheckBox("Multiplayer", AssetController.getSkin("first"));
        AiLevelLabel = new Label("Level: ", AssetController.getSkin("first"), "title");
        easyCheckBox = new CheckBox("Easy", AssetController.getSkin("first"));
        mediocreCheckBox = new CheckBox("Mediocre", AssetController.getSkin("first"));
        hardCheckBox = new CheckBox("Hard", AssetController.getSkin("first"));
        rivalUsernameLabel = new Label("Rival: ", AssetController.getSkin("first"), "title");
        rivalUsernameField = new TextField("", AssetController.getSkin("first"));
        startButton = new TextButton("Start", AssetController.getSkin("first"));
        cancelButton = new TextButton("Cancel", AssetController.getSkin("first"));

        addActor(roundsTitleLabel);
        addActor(roundsSlider);
        addActor(roundsLabel);
        addActor(modeLabel);
        addActor(modeCheckBox);
        addActor(AiLevelLabel);
        addActor(easyCheckBox);
        addActor(mediocreCheckBox);
        addActor(hardCheckBox);
        addActor(rivalUsernameLabel);
        addActor(rivalUsernameField);
        addActor(startButton);
        addActor(cancelButton);
    }

    private void arrangeWidgets() {

        setSize(1000,getHeight());
        roundsTitleLabel.setPosition(100, getY() + getHeight() - 100);
        roundsSlider.setPosition(roundsTitleLabel.getX() + 150, roundsTitleLabel.getY());
        roundsLabel.setPosition(roundsSlider.getX() + 300,roundsSlider.getY());
        modeLabel.setPosition(100,getY() + getHeight() - 200);
        modeCheckBox.setPosition(modeLabel.getX()+50, modeLabel.getY());

        AiLevelLabel.setPosition(modeCheckBox.getX() + 100,modeCheckBox.getY());
        easyCheckBox.setPosition(AiLevelLabel.getX() + 100, AiLevelLabel.getY());
        mediocreCheckBox.setPosition(easyCheckBox.getX() + 200, easyCheckBox.getY());
        hardCheckBox.setPosition(mediocreCheckBox.getX() + 200,mediocreCheckBox.getY());

        rivalUsernameLabel.setPosition(modeCheckBox.getX() + 100,modeCheckBox.getY());
        rivalUsernameField.setPosition(rivalUsernameLabel.getX() + 200, rivalUsernameLabel.getY());

        startButton.setBounds(300,0,400,100);
        cancelButton.setBounds(1000,0,400,100);
    }

    private void addListeners() {
        cancelButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                MainMenuController.getView().cancelStartGame();
            }
        });
    }

}
