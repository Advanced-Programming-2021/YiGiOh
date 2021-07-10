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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.HashMap;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.DeckMenuController;
import edu.sharif.ce.apyugioh.controller.DuelController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ProfileController;
import edu.sharif.ce.apyugioh.controller.ScoreboardController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.controller.UserController;
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

    private boolean loaded;
    private Stage stage;
    private SpriteBatch batch;
    ObjectSet<CardModelView> cards;
    private Texture backgroundTexture;
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
                        //DuelController.getInstance().startNoPlayerDuel(AILevel.HARD, AILevel.MEDIOCRE, 1);
                        DuelController.getInstance().startSinglePlayerDuel(MainMenuController.getInstance().getUser().getUsername(), AILevel.MEDIOCRE, 1);
                        //DuelController.getInstance().startMultiplayerDuel(MainMenuController.getInstance().getUser().getUsername(), "Ali", 1);
                        GameController.showGame();
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
            if (mainMenuButton.getText().toString().equals("Scoreboard")) {
                mainMenuButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        ScoreboardController.getInstance().setUser(MainMenuController.getInstance().getUser());
                        ScoreboardController.getInstance().showScoreboard();
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
}
