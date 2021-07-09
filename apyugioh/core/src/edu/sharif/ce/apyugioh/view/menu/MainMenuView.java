package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.HashMap;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.*;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.AILevel;
import edu.sharif.ce.apyugioh.model.ProfilePicture;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.model.CardModelView;

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
    private StartGameDialog startGameWindow;
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
        createStartGameWindow();
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
        if (windows.containsKey("main"))
            return;
        TextButton[] mainMenuButtons = new TextButton[]{
                new TextButton("Play", AssetController.getSkin("first")),
                new TextButton("Deck", AssetController.getSkin("first")),
                new TextButton("Card Factory",AssetController.getSkin("first")),
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
                        AssetController.playSound("chain");
                        startGameWindow.addAction(Actions.moveTo(Gdx.graphics.getWidth()- startGameWindow.getWidth(), startGameWindow.getY(),TRANSITION_SPEED));
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
            if (mainMenuButton.getText().toString().equals("Card Factory")) {
                mainMenuButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        CardFactoryMenuController.getInstance().setUser(MainMenuController.getInstance().getUser());
                        CardFactoryMenuController.getInstance().showCardFactoryMenu();
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
    }

    private void createStartGameWindow() {
        if (windows.containsKey("start"))
            return;
        startGameWindow = new StartGameDialog("", AssetController.getSkin("first"));
        startGameWindow.setKeepWithinStage(false);
        System.out.println(startGameWindow.getWidth());
        startGameWindow.setPosition(Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()/2- startGameWindow.getHeight()/2);
        windows.put("start", startGameWindow);
        stage.addActor(startGameWindow);
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

    public void showMessage(String message){
        Dialog dialog = new Dialog("",AssetController.getSkin("first"));
        TextButton okButton = new TextButton("Ok",AssetController.getSkin("first"));
        Label errorMessageLabel = new Label(message,AssetController.getSkin("first"),"title");
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
        dialog.getContentTable().add(errorMessageLabel).fill().expandX().padLeft(10).padRight(10).center();
        dialog.getButtonTable().add(okButton).fill().expand().height(110);
        dialog.show(stage);
    }

    @Override
    public void showError(int errorID, String... values) {
        Dialog dialog = new Dialog("",AssetController.getSkin("first"));
        TextButton okButton = new TextButton("Ok",AssetController.getSkin("first"));
        Label errorMessageLabel = new Label(String.format(errorMessages.get(errorID),values),AssetController.getSkin("first"),"title");
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
        dialog.getContentTable().add(errorMessageLabel).fill().expandX().padLeft(10).padRight(10).center();
        dialog.getButtonTable().add(okButton).fill().expand().height(110);
        dialog.show(stage);
    }

    @Override
    public void showSuccess(int successID, String... values) {
        Dialog dialog = new Dialog("",AssetController.getSkin("first"));
        TextButton okButton = new TextButton("Ok",AssetController.getSkin("first"));
        Label errorMessageLabel = new Label(String.format(successMessages.get(successID), values),AssetController.getSkin("first"),"title");
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
        dialog.getContentTable().add(errorMessageLabel).fill().expandX().padLeft(10).padRight(10).center();
        dialog.getButtonTable().add(okButton).fill().expand().height(110);
        dialog.show(stage);
    }

    public void cancelStartGame(){
        Window mainWindow = windows.get("main");
        Window startWindow = windows.get("start");
        AssetController.playSound("chain");
        mainWindow.addAction(Actions.moveTo(mainWindow.getX(),Gdx.graphics.getHeight()-mainWindow.getHeight(),TRANSITION_SPEED));
        startWindow.addAction(Actions.sequence(Actions.moveTo(Gdx.graphics.getWidth(),startWindow.getY(),TRANSITION_SPEED),new RunnableAction(){
            @Override
            public void run() {
                ((StartGameDialog)startWindow).resetValues();
            }
        }));
    }

    public void startGame(String firstPlayerUsername, String secondPlayerUsername, int rounds, AILevel aiLevel) {
        boolean isDuelValid;
        if (secondPlayerUsername.equals(""))
            isDuelValid = DuelController.getInstance().startSinglePlayerDuel(firstPlayerUsername,aiLevel,rounds);
        else
            isDuelValid = DuelController.getInstance().startMultiplayerDuel(firstPlayerUsername,secondPlayerUsername,rounds);
        System.out.println(isDuelValid);
        if (isDuelValid)
            GameController.showGame();
    }
}

class StartGameDialog extends Window {

    private String rivalUsername = "";
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
        setSize(1200,600);
        initializeWidgets();
        arrangeWidgets();
        addListeners();
        rivalUsername = "";
        aiLevel = AILevel.EASY;
        rounds = 1;
        updateMode();
        updateAILevel(easyCheckBox);
        updateRoundsNumber();
    }

    private void initializeWidgets() {
        roundsTitleLabel = new Label("Rounds: ", AssetController.getSkin("first"), "title");
        roundsSlider = new Slider(1, 3, 3, false, AssetController.getSkin("first"));
        roundsLabel = new Label("1", AssetController.getSkin("first"),"title");
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
        roundsTitleLabel.setPosition(200, getHeight() - 150);
        roundsSlider.setPosition(roundsTitleLabel.getX() + 150, roundsTitleLabel.getY());
        roundsLabel.setPosition(roundsSlider.getX() + 200,roundsSlider.getY());
        modeLabel.setPosition(200,getHeight() - 300);
        modeCheckBox.setPosition(modeLabel.getX()+100, modeLabel.getY() + 10);

        modeCheckBox.getStyle().fontColor = Color.WHITE;

        AiLevelLabel.setPosition(modeLabel.getX(),modeCheckBox.getY()-100);
        easyCheckBox.setPosition(AiLevelLabel.getX() + 100, AiLevelLabel.getY());
        mediocreCheckBox.setPosition(easyCheckBox.getX() + 200, easyCheckBox.getY());
        hardCheckBox.setPosition(mediocreCheckBox.getX() + 200,mediocreCheckBox.getY());

        rivalUsernameLabel.setPosition(modeLabel.getX(),modeCheckBox.getY()-100);
        rivalUsernameField.setPosition(rivalUsernameLabel.getX() + 150, rivalUsernameLabel.getY());
        rivalUsernameField.setSize(200,50);

        startButton.setBounds(50,100,350,100);
        cancelButton.setBounds(500,100,350,100);
    }

    private void updateMode(){
        if (modeCheckBox.isChecked()){
            AiLevelLabel.setVisible(false);
            easyCheckBox.setVisible(false);
            mediocreCheckBox.setVisible(false);
            hardCheckBox.setVisible(false);
            rivalUsernameField.setVisible(true);
            rivalUsernameLabel.setVisible(true);
        } else {
            AiLevelLabel.setVisible(true);
            easyCheckBox.setVisible(true);
            mediocreCheckBox.setVisible(true);
            hardCheckBox.setVisible(true);
            rivalUsernameField.setVisible(false);
            rivalUsernameLabel.setVisible(false);
        }
    }

    private void updateRoundsNumber(){
        rounds = (int)roundsSlider.getValue();
        roundsLabel.setText(String.valueOf((int)roundsSlider.getValue()));
    }

    private void updateAILevel(CheckBox checkedCheckBox){
        easyCheckBox.setChecked(false);
        mediocreCheckBox.setChecked(false);
        hardCheckBox.setChecked(false);
        checkedCheckBox.setChecked(true);
        if (checkedCheckBox == easyCheckBox)
            aiLevel = AILevel.EASY;
        if (checkedCheckBox == mediocreCheckBox)
            aiLevel = AILevel.MEDIOCRE;
        if (checkedCheckBox == hardCheckBox)
            aiLevel = AILevel.HARD;
    }

    public void resetValues(){
        roundsSlider.setValue(1.0f);
        updateRoundsNumber();
        updateAILevel(easyCheckBox);
        modeCheckBox.setChecked(false);
        updateMode();
    }

    private void addListeners() {
        startButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                MainMenuController.getView().startGame(MainMenuController.getInstance().getUser().getUsername(),
                        rivalUsername,rounds,aiLevel);
            }
        });
        cancelButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                MainMenuController.getView().cancelStartGame();
            }
        });
        modeCheckBox.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateMode();
            }
        });
        roundsSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updateRoundsNumber();
            }
        });
        easyCheckBox.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateAILevel(easyCheckBox);
            }
        });
        mediocreCheckBox.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateAILevel(mediocreCheckBox);
            }
        });
        hardCheckBox.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                updateAILevel(hardCheckBox);
            }
        });
        rivalUsernameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                rivalUsername = rivalUsernameField.getText();
                System.out.println(rivalUsername);
            }
        });
    }

}
