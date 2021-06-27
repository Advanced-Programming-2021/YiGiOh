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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.view.model.CardAction;
import edu.sharif.ce.apyugioh.view.model.CardActionsManager;
import edu.sharif.ce.apyugioh.view.model.CardFrontView;
import edu.sharif.ce.apyugioh.view.model.CardModelView;
import edu.sharif.ce.apyugioh.view.model.DeckModelView;
import edu.sharif.ce.apyugioh.view.model.GameDeckModelView;

public class GameMenuView extends Menu {

    private InputProcessor inputProcessor;
    private Stage stage;
    private SpriteBatch batch;
    private Array<CardModelView> cards;
    private DeckModelView deck;
    private CardActionsManager manager;
    private CardFrontView board;
    private PlayerController firstPlayerController, secondPlayerController;
    private GameDeckModelView cardViews;

    public GameMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 5, 0, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        moveCamera = false;
        manager = new CardActionsManager();
        stage = new Stage();
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
        super.show();
        board = new CardFrontView(new Sprite(new Texture(Gdx.files.internal("backgrounds/board.png"))));
        board.environment = environment;
        board.worldTransform.setToRotation(0, 1, 0, 270);
        board.worldTransform.scale(8f, 8f, 8f);
        board.worldTransform.setTranslation(75, 0, 0);
        cam.position.lerp(new Vector3(0, -70, 0), 1);
        cam.lookAt(75, -25, 0);
        Gdx.input.setInputProcessor(stage);
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
            card.scale(0.65f, 0.65f, 0.65f);
        }
        update();
    }

    public void update() {
        updateGraveyard();
        updateMonsterZone();
        updateDeck();
        updateHand();
    }

    private void updateMonsterZone() {
        int counter;
        counter = 0;
        for (GameCard card : firstPlayerController.getPlayer().getField().getMonsterZone()) {
            if (card != null) {
                CardModelView cardView = cardViews.getCard(card.getId());
                Matrix4 target = cardView.getTransform();
                cardView.setToRotation(0, 1, 0, card.isFaceDown() ? 90 : -90);
                target.setTranslation(74, -18, -25f + (counter) * 13f);
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
                cardView.setToRotation(0, 1, 0, card.isFaceDown() ? 90 : -90);
                target.rotate(0, 0, 1, 180);
                target.setTranslation(74, 18, 25f - (counter) * 13f);
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
