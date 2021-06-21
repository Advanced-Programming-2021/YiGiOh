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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.HashMap;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.menu.Menu;
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
    private HashMap<String, Window> windows;


    public MainMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 15, 5, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        assets.load("3D/puzzle/puzzle.g3db", Model.class);
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
        createMainWindow();

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (!loaded && assets.update()) {
            Model dragon = assets.get("3D/puzzle/puzzle.g3db", Model.class);
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
        TextButton[] mainMenuButtons = new TextButton[]{new TextButton("Play", AssetController.getSkin("first")),
                new TextButton("Deck", AssetController.getSkin("first")),
                new TextButton("Scoreboard", AssetController.getSkin("first")),
                new TextButton("Shop", AssetController.getSkin("first")),
                new TextButton("Setting", AssetController.getSkin("first")),
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
            window.add(mainMenuButton).width(300).height(80).spaceBottom(10);
            window.row();
        }
        window.padTop(150);
        windows.put("main", window);
        stage.addActor(window);
    }
}
