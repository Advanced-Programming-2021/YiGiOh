package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.view.model.CameraAction;
import edu.sharif.ce.apyugioh.view.model.CardAction;
import edu.sharif.ce.apyugioh.view.model.CardFrontView;
import edu.sharif.ce.apyugioh.view.model.CardModelView;
import edu.sharif.ce.apyugioh.view.model.DeckModelView;
import edu.sharif.ce.apyugioh.view.model.GameActionsManager;
import edu.sharif.ce.apyugioh.view.model.GameDeckModelView;

public class GameMenuView extends Menu {

    private InputProcessor inputProcessor;
    private Stage stage;
    private SpriteBatch batch;
    private Array<CardModelView> cards;
    private DeckModelView deck;
    private GameActionsManager manager;
    private CardFrontView board;
    private PlayerController firstPlayerController, secondPlayerController;
    private GameDeckModelView cardViews;
    private List<Polygon> cardPolygons;

    public GameMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 5, 0, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        moveCamera = false;
        manager = new GameActionsManager();
        stage = new Stage();
        batch = new SpriteBatch();
        cardPolygons = new ArrayList<>();
    }

    @Override
    public void show() {
        super.show();
        board = new CardFrontView(new Sprite(new Texture(Gdx.files.internal("backgrounds/board.png"))));
        board.environment = environment;
        board.worldTransform.setToRotation(0, 1, 0, 270);
        board.worldTransform.scale(8f, 8f, 8f);
        board.worldTransform.setTranslation(75, 0, 0);
        cam.position.set(0, -70, 0);
        cam.lookAt(75, -25, 0);
        cam.update();
        addFirstPlayerMonsterZonePolygons();
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                System.out.println(x + " : " + y + " clicked!");
                for (int i = 0; i < cardPolygons.size(); i++) {
                    if (cardPolygons.get(i).contains(x, y)) {
                        System.out.println("Mioo " + i);
                        break;
                    }
                }
            }
        });
    }

    private void addFirstPlayerMonsterZonePolygons() {
        Polygon polygon = new Polygon(new float[]{32, 153, 136, 153, 115, 0, 0, 0});
        polygon.translate(649, 515);
        cardPolygons.add(polygon);
        polygon = new Polygon(new float[]{19, 153, 123, 153, 115, 0, 0, 0});
        polygon.translate(775, 515);
        cardPolygons.add(polygon);
        polygon = new Polygon(new float[]{6, 153, 110, 153, 115, 0, 0, 0});
        polygon.translate(901, 515);
        cardPolygons.add(polygon);
        polygon = new Polygon(new float[]{0, 153, 104, 153, 123, 0, 8, 0});
        polygon.translate(1017, 515);
        cardPolygons.add(polygon);
        polygon = new Polygon(new float[]{0, 153, 104, 153, 136, 0, 21, 0});
        polygon.translate(1134, 515);
        cardPolygons.add(polygon);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        manager.update(delta);
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.render(board);
        for (CardModelView card : cardViews.getAllCards()) {
            card.render(modelBatch, environment);
        }
        modelBatch.end();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void initializePlayers(PlayerController firstPlayerController, PlayerController secondPlayerController) {
        this.firstPlayerController = firstPlayerController;
        this.secondPlayerController = secondPlayerController;
        cardViews = new GameDeckModelView(firstPlayerController.getPlayer().getField(), secondPlayerController.getPlayer().getField());
        for (CardModelView card : cardViews.getAllCards()) {
            card.scale(0.5f, 0.5f, 0.5f);
        }
        update(true);
    }

    public void registerGameCard(GameCard card) {
        cardViews.addCard(card);
    }

    public void update(boolean isFirstPlayerTurn) {
        updateCamera(isFirstPlayerTurn);
        updateGraveyard();
        updateSpellZone(isFirstPlayerTurn);
        updateMonsterZone(isFirstPlayerTurn);
        updateDeck();
        updateHand();
    }

    public void updateCamera(boolean isFirstPlayerTurn) {
        Vector3 target = cam.position.cpy();
        target.set(0, isFirstPlayerTurn ? -70 : 70, 0);
        CameraAction action = new CameraAction(cam, target, new Vector3(75, isFirstPlayerTurn ? -25 : 25, 0), 1);
        manager.addAction(action);
    }

    private void updateMonsterZone(boolean isFirstPlayerTurn) {
        int counter;
        counter = 0;
        for (GameCard card : firstPlayerController.getPlayer().getField().getMonsterZone()) {
            if (card != null) {
                CardModelView cardView = cardViews.getCard(card.getId());
                Matrix4 target = cardView.getTransform();
                if (isFirstPlayerTurn) {
                    target.setToRotation(0, 1, 0, -90);
                } else {
                    target.setToRotation(0, 1, 0, card.isRevealed() ? -90 : 90);
                }
                target.rotate(0, 0, 1, card.isFaceDown() ? -90 : 0);
                target.setTranslation(74, -18.5f, (counter % 2 == 0 ? -1 : 1) * ((counter + 1) / 2) * 13f);
                CardAction action = new CardAction(cardView, target, 1);
                manager.addAction(action);
            }
            counter++;
        }
        counter = 0;
        for (GameCard card : secondPlayerController.getPlayer().getField().getMonsterZone()) {
            if (card != null) {
                CardModelView cardView = cardViews.getCard(card.getId());
                Matrix4 target = cardView.getTransform();
                if (!isFirstPlayerTurn) {
                    target.setToRotation(0, 1, 0, -90);
                } else {
                    target.setToRotation(0, 1, 0, card.isRevealed() ? -90 : 90);
                }
                target.rotate(0, 0, 1, 180 + (card.isFaceDown() ? -90 : 0));
                target.setTranslation(74, 18.5f, (counter % 2 == 0 ? 1 : -1) * ((counter + 1) / 2) * 13f);
                CardAction action = new CardAction(cardView, target, 1);
                manager.addAction(action);
            }
            counter++;
        }
    }

    private void updateSpellZone(boolean isFirstPlayerTurn) {
        int counter;
        counter = 0;
        for (GameCard card : firstPlayerController.getPlayer().getField().getSpellZone()) {
            if (card != null) {
                CardModelView cardView = cardViews.getCard(card.getId());
                Matrix4 target = cardView.getTransform();
                if (!isFirstPlayerTurn) {
                    target.setToRotation(0, 1, 0, -90);
                } else {
                    target.setToRotation(0, 1, 0, card.isRevealed() ? -90 : 90);
                }
                target.rotate(0, 0, 1, card.isFaceDown() ? -90 : 0);
                target.setTranslation(74, -33.5f, (counter % 2 == 0 ? -1 : 1) * ((counter + 1) / 2) * 13f);
                CardAction action = new CardAction(cardView, target, 1);
                manager.addAction(action);
            }
            counter++;
        }
        counter = 0;
        for (GameCard card : secondPlayerController.getPlayer().getField().getSpellZone()) {
            if (card != null) {
                CardModelView cardView = cardViews.getCard(card.getId());
                Matrix4 target = cardView.getTransform();
                if (!isFirstPlayerTurn) {
                    target.setToRotation(0, 1, 0, -90);
                } else {
                    target.setToRotation(0, 1, 0, card.isRevealed() ? -90 : 90);
                }
                target.rotate(0, 0, 1, 180 + (card.isFaceDown() ? -90 : 0));
                target.setTranslation(74, 33.5f, (counter % 2 == 0 ? 1 : -1) * ((counter + 1) / 2) * 13f);
                CardAction action = new CardAction(cardView, target, 1);
                manager.addAction(action);
            }
            counter++;
        }
    }

    private void updateHand() {
        int counter;
        counter = 0;
        for (GameCard card : firstPlayerController.getPlayer().getField().getHand()) {
            CardModelView cardView = cardViews.getCard(card.getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, -90);
            target.rotate(1, 0, 0, 45);
            target.setTranslation(68, -62f, -45f + (++counter) * 13f);
            CardAction action = new CardAction(cardView, target, 1);
            manager.addAction(action);
        }
        counter = 0;
        for (GameCard card : secondPlayerController.getPlayer().getField().getHand()) {
            CardModelView cardView = cardViews.getCard(card.getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, -90);
            target.rotate(0, 0, 1, 180);
            target.rotate(1, 0, 0, 45);
            target.setTranslation(68, 62f, 45f - (++counter) * 13f);
            CardAction action = new CardAction(cardView, target, 1);
            manager.addAction(action);
        }
    }

    private void updateGraveyard() {
        int counter;
        counter = 0;
        for (GameCard card : firstPlayerController.getPlayer().getField().getGraveyard()) {
            CardModelView cardView = cardViews.getCard(card.getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, -90);
            target.setTranslation(74 - (++counter) * 0.01f, -9.5f, 43f + (counter) * 0.01f);
            CardAction action = new CardAction(cardView, target, 1);
            manager.addAction(action);
        }
        counter = 0;
        for (GameCard card : secondPlayerController.getPlayer().getField().getGraveyard()) {
            CardModelView cardView = cardViews.getCard(card.getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, -90);
            target.rotate(0, 0, 1, 180);
            target.setTranslation(74 - (++counter) * 0.01f, 9.5f, -43f - (counter) * 0.01f);
            CardAction action = new CardAction(cardView, target, 1);
            manager.addAction(action);
        }
    }

    private void updateDeck() {
        int counter;
        counter = 0;
        for (GameCard card : firstPlayerController.getPlayer().getField().getDeck()) {
            CardModelView cardView = cardViews.getCard(card.getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, 90);
            target.setTranslation(74 - (++counter) * 0.01f, -49.5f, 43f + (counter) * 0.01f);
            CardAction action = new CardAction(cardView, target, cardView.getPosition().x < 20 ? 10 : 1);
            manager.addAction(action);
        }
        counter = 0;
        for (GameCard card : secondPlayerController.getPlayer().getField().getDeck()) {
            CardModelView cardView = cardViews.getCard(card.getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, 90);
            target.rotate(0, 0, 1, 180);
            target.setTranslation(74 - (++counter) * 0.01f, 49.5f, -43f - (counter) * 0.01f);
            CardAction action = new CardAction(cardView, target, cardView.getPosition().x < 20 ? 10 : 1);
            manager.addAction(action);
        }
    }

    public boolean isDone() {
        return manager.isDone();
    }
}
