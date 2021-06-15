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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ObjectSet;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.UserController;
import edu.sharif.ce.apyugioh.view.model.CardModelView;
import edu.sharif.ce.apyugioh.view.model.DeckModelView;

public class MainMenuView extends Menu {

    public static final int SUCCESS_LOGOUT = 1;

    {
        successMessages.put(SUCCESS_LOGOUT, "user logged out successfully!");
    }

    private boolean loaded;
    private Stage stage;
    private SpriteBatch batch;
    ObjectSet<CardModelView> cards;
    private DeckModelView deck;
    private Texture backgroundTexture;

    public MainMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 15, 5, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        assets.load("3D/yugi/yugi.g3db", Model.class);
        batch = new SpriteBatch();
        stage = new Stage();
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/main" + MathUtils.random(1, 10) + ".jpg"));
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
        createMainWindow();
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

    private Dialog createLoginDialog() {
        TextField usernameField = new TextField("", AssetController.getSkin("first"));
        TextField passwordField = new TextField("", AssetController.getSkin("first"));
        Dialog dialog = new Dialog("", AssetController.getSkin("first")) {
            @Override
            protected void result(Object object) {
                if ((Boolean) object) {
                    UserController.getInstance().loginUser(usernameField.getText(), passwordField.getText());
                }
            }
        };
//        dialog.setPosition(Gdx.graphics.getHeight() - dialog.getHeight(), Gdx.graphics.getWidth()/2 - dialog.getWidth()/2);
        dialog.getContentTable().add(new Label("Username : ", AssetController.getSkin("first")));
        dialog.getContentTable().add(usernameField).width(220).height(40);
        dialog.getContentTable().row();
        dialog.getContentTable().add(new Label("Password : ", AssetController.getSkin("first")));
        dialog.getContentTable().add(passwordField).width(220).height(40);
        dialog.getTitleTable().pad(5);
        dialog.getContentTable().pad(5);
        dialog.getButtonTable().pad(5);
        dialog.button("OK", true).button("NO", false).key(Input.Keys.ENTER, true);
        return dialog;
    }

    private void createMainWindow() {
        TextButton[] mainMenuButtons = new TextButton[]{new TextButton("Signup", AssetController.getSkin("first")),
                new TextButton("Login", AssetController.getSkin("first")),
                new TextButton("Shop", AssetController.getSkin("first"))};
        Window window = new Window("", AssetController.getSkin("first"));
        for (TextButton mainMenuButton : mainMenuButtons) {
            if (mainMenuButton.getText().toString().equals("Login")) {
                mainMenuButton.addListener(new ButtonClickListener() {
                    @Override
                    public void clickAction() {
                        createLoginDialog().show(stage);
                    }
                });
            }
            window.add(mainMenuButton).width(350).height(100).spaceBottom(20);
            window.row();
        }
        window.setWidth(542);
        window.setHeight(940);
        window.setPosition(Gdx.graphics.getWidth()/2 - 271, Gdx.graphics.getHeight() - 940);
        window.background(new TextureRegionDrawable(new Texture(Gdx.files.internal("skins/table.png"))));
        stage.addActor(window);
    }
}
