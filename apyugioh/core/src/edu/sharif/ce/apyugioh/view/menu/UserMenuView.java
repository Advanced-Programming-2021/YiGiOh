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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.HashMap;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.UserController;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.model.CardModelView;

public class UserMenuView extends Menu {

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

    private final float TRANSITION_SPEED = 3;

    private boolean loaded;
    private Stage stage;
    private SpriteBatch batch;
    ObjectSet<CardModelView> cards;
    private Texture backgroundTexture;
    private HashMap<String, Window> windows;

    public UserMenuView(YuGiOh game) {
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
        createWindows();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (!loaded && AssetController.getAssets().update()) {
            Model dragon = AssetController.getAssets().get("3D/yugi/yugi.g3db", Model.class);
            ModelInstance instance = new ModelInstance(dragon);
            instance.transform.scale(0.1f, 0.1f, 0.1f);
            instance.transform.setTranslation(18, -8, -14);
            instance.transform.rotate(0, -1, 0, 90);
            instances.add(instance);
            instances.add(instance.copy());
            instances.get(1).transform.setTranslation(18, -8, 14);
            instances.get(0).materials.get(0).set(new ColorAttribute(ColorAttribute.Diffuse, Color.RED));
            //instances.get(1).materials.get(0).set(new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE));
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
        dialog.getContentTable().add(errorMessageLabel).fill().expandX().padLeft(10).padRight(10);
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
        dialog.getContentTable().add(errorMessageLabel).fill().expandX().padLeft(10).padRight(10);
        dialog.getButtonTable().add(okButton).fill().expand().height(110);
        dialog.show(stage);
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
        dialog.setPosition(Gdx.graphics.getWidth() / 2 - 271, Gdx.graphics.getHeight());
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
        TextButton[] mainWindowButtons = new TextButton[]{
                new TextButton("Signup", AssetController.getSkin("first")),
                new TextButton("Login", AssetController.getSkin("first")),
                new TextButton("Exit", AssetController.getSkin("first"))
        };
        Window window = new Window("", AssetController.getSkin("first"));
        window.setKeepWithinStage(false);
        window.setWidth(542);
        window.setHeight(940);
        Table table = new Table(AssetController.getSkin("first"));
        window.setPosition((Gdx.graphics.getWidth() - window.getWidth()) / 2, Gdx.graphics.getHeight() - window.getHeight());
        for (TextButton mainWindowButton : mainWindowButtons) {
            if (mainWindowButton.getText().toString().equals("Signup")) {
                mainWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        window.addAction(Actions.moveTo(window.getX(), Gdx.graphics.getHeight(), TRANSITION_SPEED));
                        AssetController.playSound("chain");
                        windows.get("signup").addAction(Actions.moveTo(Gdx.graphics.getWidth() - 940, Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
                    }
                });
            } else if (mainWindowButton.getText().toString().equals("Login")) {
                mainWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        window.addAction(Actions.moveTo(window.getX(), Gdx.graphics.getHeight(), TRANSITION_SPEED));
                        AssetController.playSound("chain");
                        windows.get("login").addAction(Actions.moveTo(0, Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
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
        TextButton[] signupWindowButtons = new TextButton[]{
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
                        window.addAction(Actions.moveTo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
                        AssetController.playSound("chain");
                        windows.get("main").addAction(Actions.moveTo(Gdx.graphics.getWidth() / 2 - 271, Gdx.graphics.getHeight() - 940, TRANSITION_SPEED));
                    }
                });
            } else if (signupWindowButton.getText().toString().equals("Signup")) {
                signupWindowButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        UserController.getInstance().registerUser(usernameField.getText(), passwordField.getText(), usernameField.getText());
                        //Back to login view + show a success dialog
                    }
                });
            }
            table.add(signupWindowButton).width(350).height(100).spaceBottom(10).colspan(2);
            table.row();
        }
        window.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2);
        windows.put("signup", window);
        window.add(table);
        stage.addActor(window);
    }

    private void createLoginWindow() {
        TextButton[] loginWindowButtons = new TextButton[]{
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
                        window.addAction(Actions.moveTo(-window.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
                        AssetController.playSound("chain");
                        windows.get("main").addAction(Actions.moveTo(Gdx.graphics.getWidth() / 2 - 271, Gdx.graphics.getHeight() - 940, TRANSITION_SPEED));
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
        window.addListener(new InputListener(){
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    UserController.getInstance().loginUser(usernameField.getText(), passwordField.getText());
                    return true;
                } else if (keycode == Input.Keys.ESCAPE){
                    usernameField.setText("");
                    passwordField.setText("");
                    window.addAction(Actions.moveTo(-window.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
                    AssetController.playSound("chain");
                    windows.get("main").addAction(Actions.moveTo(Gdx.graphics.getWidth() / 2 - 271, Gdx.graphics.getHeight() - 940, TRANSITION_SPEED));
                    return true;
                }
                return super.keyDown(event,keycode);
            }
        });
        window.setPosition(-window.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2);
        windows.put("login", window);
        window.add(table);
        stage.addActor(window);
    }
}