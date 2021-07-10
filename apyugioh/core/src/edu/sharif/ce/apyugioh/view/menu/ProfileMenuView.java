package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ObjectSet;
import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ProfileController;
import edu.sharif.ce.apyugioh.view.model.ProfilePicture;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.model.CardModelView;
import edu.sharif.ce.apyugioh.view.model.DeckModelView;
import edu.sharif.ce.apyugioh.view.model.DesktopFileChooser;
import net.spookygames.gdx.nativefilechooser.NativeFileChooser;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserCallback;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

public class ProfileMenuView extends Menu {

    private final float TRANSITION_SPEED = 3;

    private boolean loaded;
    private Stage stage;
    private SpriteBatch batch;
    ObjectSet<CardModelView> cards;
    private DeckModelView deck;
    private Texture backgroundTexture;
    private HashMap<String, Window> windows;
    private ProfilePicture profilePicture;
    private InputProcessor inputProcessor;

    public ProfileMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 15, 5, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        batch = new SpriteBatch();
        stage = new Stage();
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/main" + 10 + ".jpg"));
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
        createProfileDetails();
        inputMultiplexer.addProcessor(stage);
        createInputProcessor();
        inputMultiplexer.addProcessor(inputProcessor);
        Gdx.input.setInputProcessor(inputMultiplexer);
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

    private void createWindows() {
        createMainWindow();
        createChangeNicknameWindow();
        createChangePasswordWindow();
    }

    private void createMainWindow() {
        TextButton[] buttons = new TextButton[] {
                new TextButton("Change Avatar", AssetController.getSkin("first")),
                new TextButton("Change Nickname", AssetController.getSkin("first")),
                new TextButton("Change Password", AssetController.getSkin("first")),
                new TextButton("Delete Account", AssetController.getSkin("first")),
                new TextButton("Back", AssetController.getSkin("first"))
        };
        Window window = new Window("", AssetController.getSkin("first"));
        window.setKeepWithinStage(false);
        window.setWidth(542);
        window.setHeight(940);
        Table table = new Table(AssetController.getSkin("first"));
        window.setPosition((Gdx.graphics.getWidth() - window.getWidth()) / 2, Gdx.graphics.getHeight() - window.getHeight());
        for (TextButton button : buttons) {
            if (button.getText().toString().equals("Change Nickname")) {
                button.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        window.addAction(Actions.moveTo(window.getX(), Gdx.graphics.getHeight(), TRANSITION_SPEED));
                        AssetController.playSound("chain");
                        windows.get("nickname").addAction(Actions.moveTo(0, Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
                    }
                });
            } else if (button.getText().toString().equals("Change Password")) {
                button.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        window.addAction(Actions.moveTo(window.getX(), Gdx.graphics.getHeight(), TRANSITION_SPEED));
                        AssetController.playSound("chain");
                        windows.get("password").addAction(Actions.moveTo(Gdx.graphics.getWidth() - 940, Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
                    }
                });
            } else if (button.getText().toString().equals("Change Avatar")) {
                button.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        NativeFileChooser fileChooser = new DesktopFileChooser();
                        // Configure
                        createFileChooser(fileChooser);
                    }
                });
            } else if (button.getText().toString().equals("Back")) {
                button.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        ProfileController.getInstance().back();
                    }
                });
            }

            table.add(button).width(350).height(100).spaceBottom(20);
            table.row();
        }

        window.padTop(200);
        windows.put("main", window);
        window.add(table);
        stage.addActor(window);
    }

    private void createChangeNicknameWindow() {
        TextButton[] buttons = new TextButton[] {
                new TextButton("Save", AssetController.getSkin("first")),
                new TextButton("Back", AssetController.getSkin("first"))
        };
        Window window = new Window("", AssetController.getSkin("first"), "left");
        window.setKeepWithinStage(false);
        window.setWidth(940);
        window.setHeight(542);

        Table table = new Table(AssetController.getSkin("first"));
        table.padLeft(170).padTop(-50);

        TextField nicknameField = new TextField("", AssetController.getSkin("first"));
        Label nicknameLabel = new Label("new Nickname : ", AssetController.getSkin("first"));
        table.add(nicknameLabel).width(150).height(50).spaceBottom(20);
        table.add(nicknameField).width(150).height(50).spaceBottom(20);
        table.row();

        for (TextButton button : buttons) {
            if (button.getText().toString().equals("Back")) {
                button.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        nicknameField.setText("");
                        window.addAction(Actions.moveTo(-window.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
                        AssetController.playSound("chain");
                        windows.get("main").addAction(Actions.moveTo(Gdx.graphics.getWidth() / 2 - 271, Gdx.graphics.getHeight() - 940, TRANSITION_SPEED));
                    }
                });
            } else if (button.getText().toString().equals("Save")) {
                button.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        ProfileController.getInstance().changeNickname(nicknameField.getText());
                    }
                });
            }

            table.add(button).width(350).height(100).colspan(2).spaceBottom(10);
            table.row();
        }

        window.setPosition(-window.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2);
        windows.put("nickname", window);
        window.add(table);
        stage.addActor(window);
    }

    private void createChangePasswordWindow() {
        TextButton[] buttons = new TextButton[] {
                new TextButton("Save", AssetController.getSkin("first")),
                new TextButton("Back", AssetController.getSkin("first"))
        };
        Window window = new Window("", AssetController.getSkin("first"), "right");
        window.setKeepWithinStage(false);
        window.setWidth(940);
        window.setHeight(542);

        Table table = new Table(AssetController.getSkin("first"));
        table.padRight(170).padTop(-50);

        TextField passwordField = new TextField("", AssetController.getSkin("first"));
        Label passwordLabel = new Label("Password : ", AssetController.getSkin("first"));
        table.add(passwordLabel).width(150).height(50).spaceBottom(20);
        table.add(passwordField).width(150).height(50).spaceBottom(20);
        table.row();

        TextField newPasswordField = new TextField("", AssetController.getSkin("first"));
        Label confirmPasswordLabel = new Label("new Password : ", AssetController.getSkin("first"));
        table.add(confirmPasswordLabel).width(150).height(50).spaceBottom(20);
        table.add(newPasswordField).width(150).height(50).spaceBottom(20);
        table.row();

        for (TextButton button : buttons) {
            if (button.getText().toString().equals("Back")) {
                button.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        passwordField.setText("");
                        newPasswordField.setText("");
                        window.addAction(Actions.moveTo(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2, TRANSITION_SPEED));
                        AssetController.playSound("chain");
                        windows.get("main").addAction(Actions.moveTo(Gdx.graphics.getWidth() / 2 - 271, Gdx.graphics.getHeight() - 940, TRANSITION_SPEED));
                    }
                });
            } else if (button.getText().toString().equals("Save")) {
                button.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        ProfileController.getInstance().changePassword(
                                passwordField.getText(),
                                newPasswordField.getText()
                        );
                    }
                });
            }

            table.add(button).width(350).height(100).colspan(2).spaceBottom(10);
            table.row();
        }

        window.setPosition(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - 542 / 2);
        windows.put("password", window);
        window.add(table);
        stage.addActor(window);
    }

    private void createFileChooser(NativeFileChooser fileChooser) {
        NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();
        conf.directory = Gdx.files.absolute(System.getProperty("user.home"));
        conf.mimeFilter = "audio/*";
        conf.nameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("ogg");
            }
        };
        conf.title = "Choose profile photo";
        fileChooser.chooseFile(conf, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                // Do stuff with file, yay!
                profilePicture.setProfilePicture(file, false);
            }

            @Override
            public void onCancellation() {
                System.out.println("Cancelled!");
            }

            @Override
            public void onError(Exception exception) {
                System.out.println("Not an audio");
            }
        });
    }

    private void createProfileDetails() {
        Image image = new Image(new Texture(Gdx.files.internal("skins/profile_frame.png")));
        image.setPosition(170, Gdx.graphics.getHeight() - 200);
        Table table = new Table(AssetController.getSkin("first"));
        Label usernameLabel = new Label("" + ProfileController.getInstance().getUser().getUsername(), AssetController.getSkin("first"), "title");
        Label nicknameLabel = new Label("" + ProfileController.getInstance().getUser().getNickname(), AssetController.getSkin("first"));
        table.add(usernameLabel).spaceBottom(5).left();
        table.row();
        table.add(nicknameLabel).left();
        table.setPosition(245 + (MainMenuController.getInstance().getUser().getUsername().length() - 5) * 10 + image.getWidth(), Gdx.graphics.getHeight() - 150);
        profilePicture = new ProfilePicture(Gdx.files.local("assets/db/profiles/" + ProfileController.getInstance().getUser().getAvatarName()), true);
        stage.addActor(image);
        stage.addActor(table);
        stage.addActor(profilePicture);
    }

    private void createInputProcessor() {
        inputProcessor = new InputProcessor() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode != Input.Keys.ESCAPE && keycode != Input.Keys.BACK) {
                    return false;
                }
                AssetController.stopSound();
                ProfileController.getInstance().back();
                return true;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                return false;
            }
        };
    }
}
