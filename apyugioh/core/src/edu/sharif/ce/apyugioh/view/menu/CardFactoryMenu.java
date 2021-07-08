package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.ObjectSet;
import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.model.ProfilePicture;
import edu.sharif.ce.apyugioh.view.model.CardModelView;

import java.util.HashMap;

public class CardFactoryMenu extends Menu{

    private boolean loaded;
    private Stage stage;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Dialog startGameDialog;
    private ObjectSet<CardModelView> cards;
    private HashMap<String, Window> windows;
    private ProfilePicture profilePicture;

    public CardFactoryMenu(YuGiOh game) {
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
        initialize();

        Gdx.input.setInputProcessor(stage);
    }

    private void initialize(){

    }
}
