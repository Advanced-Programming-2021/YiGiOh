package edu.sharif.ce.apyugioh.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectSet;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.UserController;
import edu.sharif.ce.apyugioh.view.model.CardModelView;
import edu.sharif.ce.apyugioh.view.model.DeckModelView;

import java.util.HashMap;

public class UserView extends Menu {

    public static final int SUCCESS_LOGOUT = 3;
    public static final int SUCCESS_USER_CREATE = 1;
    public static final int SUCCESS_USER_LOGIN = 2;

    public static final int ERROR_USER_USERNAME_ALREADY_TAKEN = -1;
    public static final int ERROR_USER_NICKNAME_ALREADY_TAKEN = -2;
    public static final int ERROR_USER_INCORRECT_USERNAME_PASSWORD = -3;

    {
        successMessages.put(SUCCESS_LOGOUT, "user logged out successfully!");
        successMessages.put(SUCCESS_USER_CREATE, "user created successfully!");
        successMessages.put(SUCCESS_USER_LOGIN, "user logged in successfully");

        errorMessages.put(ERROR_USER_INCORRECT_USERNAME_PASSWORD, "username and password doesn't match!");
        errorMessages.put(ERROR_USER_USERNAME_ALREADY_TAKEN, "user with username %s already exists");
        errorMessages.put(ERROR_USER_NICKNAME_ALREADY_TAKEN, "user with nickname %s already exists");
    }


    private boolean loaded;
    private Stage stage;
    private SpriteBatch batch;
    ObjectSet<CardModelView> cards;
    private DeckModelView deck;
    private Texture backgroundTexture;
    private HashMap<String, Window> windows;

    public UserView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 15, 5, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        assets.load("3D/yugi/yugi.g3db", Model.class);
        batch = new SpriteBatch();
        stage = new Stage();
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/main" + MathUtils.random(1, 10) + ".jpg"));
        windows = new HashMap<>();
    }

    @Override
    public void show() {
        super.show();
        cards = new ObjectSet<>();
        deck = new DeckModelView();
        CardModelView card = deck.getRandom();
        card.setTranslation(35, 0, 0);
        cards.add(card);
        card = deck.getRandom();
        card.setTranslation(35, 0, 13);
        cards.add(card);
        card = deck.getRandom();
        card.setTranslation(35, 0, -13);
        cards.add(card);
        createWindows();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (!loaded && assets.update()) {
            Model dragon = assets.get("3D/yugi/yugi.g3db", Model.class);
            ModelInstance instance = new ModelInstance(dragon);
            instance.transform.scale(0.1f, 0.1f, 0.1f);
            instance.transform.setTranslation(18, -8, -14);
            instance.transform.rotate(0, -1, 0, 90);
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

    private void createDialog(int dialogID, String message) {
        Dialog dialog = new Dialog("", AssetController.getSkin("first")) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
//                    hide();
                }
            }
        };
        dialog.setKeepWithinStage(false);
        dialog.setWidth(542);
        dialog.setHeight(940);
        dialog.setPosition(Gdx.graphics.getWidth()/2 - 271, Gdx.graphics.getHeight());
        Label messageLabel = new Label(message, AssetController.getSkin("first"));
        dialog.add(messageLabel).padBottom(-100);
        dialog.button("OK", true).key(Input.Keys.ENTER, true);
        stage.addActor(dialog);
    }

    private void createWindows() {
        createMainWindow();
        createSignupWindow();
        createLoginWindow();
    }

    private void createMainWindow() {
        TextButton[] mainWindowButtons = new TextButton[] {
                new TextButton("Signup", AssetController.getSkin("first")),
                new TextButton("Login", AssetController.getSkin("first")),
                new TextButton("Exit", AssetController.getSkin("first"))
        };
        Window window = new Window("", AssetController.getSkin("first"));
        window.setKeepWithinStage(false);
        window.setWidth(542);
        window.setHeight(940);
        Table table = new Table(AssetController.getSkin("first"));
        window.setPosition(Gdx.graphics.getWidth()/2 - 271, Gdx.graphics.getHeight() - 940);
        for (TextButton mainWindowButton : mainWindowButtons) {
            if (mainWindowButton.getText().toString().equals("Signup")) {
                mainWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        window.addAction(Actions.moveTo(window.getX(), Gdx.graphics.getHeight(), 3));
                        AssetController.getSound("chain").play();
                        windows.get("signup").addAction(Actions.moveTo(Gdx.graphics.getWidth() - 940, Gdx.graphics.getHeight()/2 - 542/2, 3));
                    }
                });
            } else if (mainWindowButton.getText().toString().equals("Login")) {
                mainWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        window.addAction(Actions.moveTo(window.getX(), Gdx.graphics.getHeight(), 3));
                        AssetController.getSound("chain").play();
                        windows.get("login").addAction(Actions.moveTo(0, Gdx.graphics.getHeight()/2 - 542/2, 3));
                    }
                });
            } else if (mainWindowButton.getText().toString().equals("Exit")) {
                mainWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        Gdx.app.exit();
                    }
                });
            }
            table.add(mainWindowButton).width(350).height(100).spaceBottom(20);
            table.row();
        }
        windows.put("main", window);
        window.add(table);
        stage.addActor(window);
    }

    private void createSignupWindow() {
        TextButton[] signupWindowButtons = new TextButton[] {
                new TextButton("Signup", AssetController.getSkin("first")),
                new TextButton("Back", AssetController.getSkin("first"))
        };
        Window window = new Window("", AssetController.getSkin("first"), "right");
        window.setKeepWithinStage(false);
        window.setWidth(940);
        window.setHeight(542);

        Table table = new Table(AssetController.getSkin("first"));
        table.padRight(170).padTop(-20);

        TextField usernameField = new TextField("", AssetController.getSkin("first"));
        Label usernameLabel = new Label("Username : ", AssetController.getSkin("first"), "title");
        table.add(usernameLabel).width(150).height(50).spaceBottom(20);
        table.add(usernameField).width(150).height(50).spaceBottom(20);
        table.row();

        TextField passwordField = new TextField("", AssetController.getSkin("first"));
        Label passwordLabel = new Label("Password : ", AssetController.getSkin("first"), "title");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        table.add(passwordLabel).width(150).height(50).spaceBottom(20);
        table.add(passwordField).width(150).height(50).spaceBottom(20);
        table.row();

        TextField confirmPasswordField = new TextField("", AssetController.getSkin("first"));
        Label confirmPasswordLabel = new Label("Confirm : ", AssetController.getSkin("first"), "title");
        confirmPasswordField.setPasswordMode(true);
        confirmPasswordField.setPasswordCharacter('*');
        table.add(confirmPasswordLabel).width(150).height(50).spaceBottom(20);
        table.add(confirmPasswordField).width(150).height(50).spaceBottom(20);
        table.row();

        for (TextButton signupWindowButton : signupWindowButtons) {
            if (signupWindowButton.getText().toString().equals("Back")) {
                signupWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        usernameField.setText("");
                        passwordField.setText("");
                        confirmPasswordField.setText("");
                        window.addAction(Actions.moveTo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2 - 542/2, 3));
                        AssetController.getSound("chain").play();
                        windows.get("main").addAction(Actions.moveTo(Gdx.graphics.getWidth()/2 - 271, Gdx.graphics.getHeight() - 940, 3));
                    }
                });
            } else if (signupWindowButton.getText().toString().equals("signup")) {
                signupWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        UserController.getInstance().registerUser(usernameField.getText(), passwordField.getText(), usernameField.getText());
                    }
                });
            }
            table.add(signupWindowButton).width(350).height(100).spaceBottom(10).colspan(2);
            table.row();
        }
        window.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()/2 - 542/2);
        windows.put("signup", window);
        window.add(table);
        stage.addActor(window);
    }

    private void createLoginWindow() {
        TextButton[] loginWindowButtons = new TextButton[] {
                new TextButton("Login", AssetController.getSkin("first")),
                new TextButton("Back", AssetController.getSkin("first"))
        };
        Window window = new Window("", AssetController.getSkin("first"), "left");
        window.setKeepWithinStage(false);
        window.setWidth(940);
        window.setHeight(542);

        Table table = new Table(AssetController.getSkin("first"));
        table.padLeft(170).padTop(-50);

        TextField usernameField = new TextField("", AssetController.getSkin("first"));
        Label usernameLabel = new Label("Username : ", AssetController.getSkin("first"), "title");
        table.add(usernameLabel).width(150).height(50).spaceBottom(20);
        table.add(usernameField).width(150).height(50).spaceBottom(20);
        table.row();

        TextField passwordField = new TextField("", AssetController.getSkin("first"));
        Label passwordLabel = new Label("Password : ", AssetController.getSkin("first"), "title");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        table.add(passwordLabel).width(150).height(50).spaceBottom(20);
        table.add(passwordField).width(150).height(50).spaceBottom(20);
        table.row();

        for (TextButton loginWindowButton : loginWindowButtons) {
            if (loginWindowButton.getText().toString().equals("Back")) {
                loginWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        usernameField.setText("");
                        passwordField.setText("");
                        window.addAction(Actions.moveTo(-window.getWidth(), Gdx.graphics.getHeight()/2 - 542/2, 3));
                        AssetController.getSound("chain").play();
                        windows.get("main").addAction(Actions.moveTo(Gdx.graphics.getWidth()/2 - 271, Gdx.graphics.getHeight() - 940, 3));
                    }
                });
            } else if (loginWindowButton.getText().toString().equals("Login")) {
                loginWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        System.out.println(usernameField.getText() + " " + passwordField.getText());
                        UserController.getInstance().loginUser(usernameField.getText(), passwordField.getText());
                    }
                });
            }
            table.add(loginWindowButton).width(350).height(100).colspan(2).spaceBottom(10);
            table.row();
        }
        window.setPosition(-window.getWidth(), Gdx.graphics.getHeight()/2 - 542/2);
        windows.put("login", window);
        window.add(table);
        stage.addActor(window);
    }
}