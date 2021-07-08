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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.view.model.CameraAction;
import edu.sharif.ce.apyugioh.view.model.CardAction;
import edu.sharif.ce.apyugioh.view.model.CardFrontView;
import edu.sharif.ce.apyugioh.view.model.CardModelView;
import edu.sharif.ce.apyugioh.view.model.DeckModelView;
import edu.sharif.ce.apyugioh.view.model.GameActionsManager;
import edu.sharif.ce.apyugioh.view.model.GameDeckModelView;
import lombok.Getter;
import lombok.Setter;

public class GameMenuView extends Menu {

    @Setter
    @Getter
    private int gameControllerID;
    private InputProcessor inputProcessor;
    private Stage stage;
    private SpriteBatch batch;
    private Array<CardModelView> cards;
    private DeckModelView deck;
    private GameActionsManager manager;
    private CardFrontView board;
    private PlayerController firstPlayerController, secondPlayerController;
    private GameDeckModelView cardViews;
    private HashMap<CardLocation, Polygon> cardPolygons;
    private Label phaseLabel, firstPlayerHPLabel, secondPlayerHPLabel;
    private boolean isDialogShown;

    public GameMenuView(YuGiOh game) {
        super(game);
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new PointLight().set(0.8f, 0.8f, 0.8f, 5, 0, 0, 150));
        environment.add(new DirectionalLight().set(0.35f, 0.35f, 0.35f, 0.1f, -0.03f, -0.1f));
        moveCamera = false;
        manager = new GameActionsManager();
        stage = new Stage();
        batch = new SpriteBatch();
        cardPolygons = new HashMap<>();
        phaseLabel = new Label("Phase: Draw", AssetController.getSkin("first"), "title");
        firstPlayerHPLabel = new Label("", AssetController.getSkin("first"), "title");
        secondPlayerHPLabel = new Label("", AssetController.getSkin("first"), "title");
    }

    @Override
    public void show() {
        super.show();
        gameControllerID = ProgramController.getGameControllerID();
        board = new CardFrontView(new Sprite(new Texture(Gdx.files.internal("backgrounds/board.png"))));
        board.environment = environment;
        board.worldTransform.setToRotation(0, 1, 0, 270);
        board.worldTransform.scale(8f, 8f, 8f);
        board.worldTransform.setTranslation(75, 0, 0);
        cam.position.set(0, -70, 0);
        cam.lookAt(75, -25, 0);
        cam.update();
        phaseLabel.setPosition(1450, 780);
        stage.addActor(phaseLabel);
        firstPlayerController = getGameController().getFirstPlayer();
        secondPlayerController = getGameController().getSecondPlayer();
        firstPlayerHPLabel.setText(firstPlayerController.getPlayer().getUser().getUsername() + " : " + firstPlayerController.getPlayer().getLifePoints());
        firstPlayerHPLabel.setPosition(1450, 1000);
        stage.addActor(firstPlayerHPLabel);
        secondPlayerHPLabel.setText(secondPlayerController.getPlayer().getUser().getUsername() + " : " + secondPlayerController.getPlayer().getLifePoints());
        secondPlayerHPLabel.setPosition(1450, 950);
        stage.addActor(secondPlayerHPLabel);
        addNextPhaseButton();
        addSummonButton();
        addSetButton();
        addAttackButton();
        addFirstPlayerMonsterZonePolygons();
        addFirstPlayerSpellZonePolygons();
        addFirstPlayerHandPolygons();
        addFirstPlayerExtraPolygons();
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!manager.isDone() || isDialogShown) return;
                System.out.println(x + " : " + y + " clicked!");
                for (Map.Entry<CardLocation, Polygon> polygon : cardPolygons.entrySet()) {
                    if (polygon.getValue().contains(x, y)) {
                        System.out.println("Clicked " + polygon.getKey().toString());
                        if (polygon.getKey().isFromMonsterZone() && getGameController().getCurrentPlayer().getField().getMonsterZone()[polygon.getKey().getPosition()] == null)
                            continue;
                        if (polygon.getKey().isFromSpellZone() && getGameController().getCurrentPlayer().getField().getSpellZone()[polygon.getKey().getPosition()] == null)
                            continue;
                        if (polygon.getKey().isInHand() && getGameController().getCurrentPlayer().getField().getHand().size() < polygon.getKey().getPosition())
                            continue;
                        if (polygon.getKey().isFromFieldZone() && getGameController().getCurrentPlayer().getField().getFieldZone() == null)
                            continue;
                        if (polygon.getKey().isFromGraveyard() && getGameController().getCurrentPlayer().getField().getGraveyard().size() < 1)
                            continue;
                        getGameController().select(polygon.getKey());
                        break;
                    }
                }
            }
        });
    }

    private void addAttackButton() {
        TextButton attackButton = new TextButton("Attack", AssetController.getSkin("first"));
        attackButton.setPosition(1450, 680);
        attackButton.setSize(200, 50);
        attackButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (getGameController().getSelectionController() != null && getGameController().getSelectionController().getLocation().isFromMonsterZone() && manager.isDone()) {
                    final int[] selectedTarget = {-1};
                    boolean isEmpty = true;
                    for (int i = 0; i < getGameController().getRivalPlayer().getField().getMonsterZone().length; i++) {
                        if (getGameController().getRivalPlayer().getField().getMonsterZone()[i] != null) {
                            isEmpty = false;
                            break;
                        }
                    }
                    boolean finalIsEmpty = isEmpty;
                    Dialog dialog = new Dialog("Attack", AssetController.getSkin("first")) {
                        @Override
                        protected void result(Object object) {
                            super.result(object);
                            Boolean result = (Boolean) object;
                            if (result && selectedTarget[0] != -1) {
                                if (finalIsEmpty) {
                                    System.out.println("Direct Attack");
                                    getGameController().getCurrentPlayerController().directAttack();
                                } else {
                                    System.out.println("Attack " + selectedTarget[0] + 1);
                                    getGameController().getCurrentPlayerController().attack(selectedTarget[0] + 1);
                                }
                            }
                            isDialogShown = false;
                            hide();
                        }
                    };
                    if (isEmpty) {
                        dialog.button("Attack Direct", true);
                    } else {
                        Table table = new Table(AssetController.getSkin("first"));
                        for (int i = 0; i < getGameController().getRivalPlayer().getField().getMonsterZone().length; i++) {
                            if (getGameController().getRivalPlayer().getField().getMonsterZone()[i] != null) {
                                Image image;
                                if (!getGameController().getRivalPlayer().getField().getMonsterZone()[i].isRevealed() && getGameController().getRivalPlayer().getField().getMonsterZone()[i].isFaceDown()) {
                                    image = new Image(AssetController.getDeck().getAtlas().findRegion("Unknown"));
                                } else {
                                    image = new Image(AssetController.getDeck().getAtlas().findRegion(Utils.firstUpperOnly(getGameController().getRivalPlayer().getField().getMonsterZone()[i].getCard().getName()).replaceAll("\\s+", "")));
                                }
                                int finalI = i;
                                image.addListener(new ClickListener() {
                                    @Override
                                    public void clicked(InputEvent event, float x, float y) {
                                        super.clicked(event, x, y);
                                        selectedTarget[0] = finalI;
                                        System.out.println("Selected Target " + finalI + 1);
                                    }
                                });
                                if (getGameController().getRivalPlayer().getField().getMonsterZone()[i].isFaceDown()) {
                                    table.add(image).width(250).height(150).pad(10);
                                } else {
                                    table.add(image).width(150).height(250).pad(10);
                                }
                            }
                        }
                        table.row();
                        ScrollPane scroll = new ScrollPane(table, AssetController.getSkin("first"));
                        dialog.getContentTable().add(scroll).width(500).height(500);
                        dialog.getContentTable().row();
                        dialog.button("Attack", true);
                    }
                    dialog.button("Cancel", false);
                    isDialogShown = true;
                    dialog.show(stage);
                }
            }
        });
        stage.addActor(attackButton);
    }

    private void addSetButton() {
        TextButton setButton = new TextButton("Set", AssetController.getSkin("first"));
        setButton.setPosition(1450, 620);
        setButton.setSize(200, 50);
        setButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (getGameController().getSelectionController() != null && getGameController().getSelectionController().getLocation().isInHand() && manager.isDone()) {
                    getGameController().getCurrentPlayerController().set();
                }
            }
        });
        stage.addActor(setButton);
    }

    private void addSummonButton() {
        TextButton summonButton = new TextButton("Summon", AssetController.getSkin("first"));
        summonButton.setPosition(1450, 560);
        summonButton.setSize(200, 50);
        summonButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (getGameController().getSelectionController() != null && getGameController().getSelectionController().getLocation().isInHand() && manager.isDone()) {
                    getGameController().getCurrentPlayerController().summon();
                }
            }
        });
        stage.addActor(summonButton);
    }

    private void addNextPhaseButton() {
        TextButton nextPhaseButton = new TextButton("Next Phase", AssetController.getSkin("first"));
        nextPhaseButton.setPosition(1450, 500);
        nextPhaseButton.setSize(200, 50);
        nextPhaseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (manager.isDone()) {
                    getGameController().getCurrentPlayerController().nextPhase();
                    phaseLabel.setText("Phase: " + getGameController().getGameTurnController().getPhase().toString());
                }
            }
        });
        stage.addActor(nextPhaseButton);
    }

    private void addFirstPlayerMonsterZonePolygons() {
        addPolygon(CardLocation.getPositionInMonsterZone(4), new float[]{32, 153, 136, 153, 115, 0, 0, 0}, 649, 515);
        addPolygon(CardLocation.getPositionInMonsterZone(2), new float[]{19, 153, 123, 153, 115, 0, 0, 0}, 775, 515);
        addPolygon(CardLocation.getPositionInMonsterZone(0), new float[]{6, 153, 110, 153, 115, 0, 0, 0}, 901, 515);
        addPolygon(CardLocation.getPositionInMonsterZone(1), new float[]{0, 153, 104, 153, 123, 0, 8, 0}, 1017, 515);
        addPolygon(CardLocation.getPositionInMonsterZone(3), new float[]{0, 153, 104, 153, 136, 0, 21, 0}, 1134, 515);
    }

    private void addFirstPlayerSpellZonePolygons() {
        addPolygon(CardLocation.getPositionInSpellZone(4), new float[]{44, 198, 162, 198, 133, 0, 0, 0}, 600, 302);
        addPolygon(CardLocation.getPositionInSpellZone(2), new float[]{26, 198, 144, 198, 133, 0, 0, 0}, 746, 302);
        addPolygon(CardLocation.getPositionInSpellZone(0), new float[]{8, 198, 127, 198, 133, 0, 0, 0}, 892, 302);
        addPolygon(CardLocation.getPositionInSpellZone(1), new float[]{0, 198, 118, 198, 144, 0, 11, 0}, 1028, 302);
        addPolygon(CardLocation.getPositionInSpellZone(3), new float[]{0, 198, 118, 198, 162, 0, 29, 0}, 1156, 302);
    }

    private void addFirstPlayerHandPolygons() {
        addPolygon(CardLocation.getPositionInHand(0), new float[]{0, 180, 162, 180, 180, 0, 28, 0}, 450, 85);
        addPolygon(CardLocation.getPositionInHand(1), new float[]{0, 180, 162, 180, 172, 0, 20, 0}, 623, 85);
        addPolygon(CardLocation.getPositionInHand(2), new float[]{0, 180, 162, 180, 162, 0, 10, 0}, 798, 85);
        addPolygon(CardLocation.getPositionInHand(3), new float[]{0, 180, 162, 180, 152, 0, 0, 0}, 973, 85);
        addPolygon(CardLocation.getPositionInHand(4), new float[]{12, 180, 174, 180, 152, 0, 0, 0}, 1137, 85);
        addPolygon(CardLocation.getPositionInHand(5), new float[]{22, 180, 184, 180, 152, 0, 0, 0}, 1302, 85);
    }

    private void addFirstPlayerExtraPolygons() {
        addPolygon(CardLocation.getPositionInFieldZone(), new float[]{46, 152, 127, 152, 90, 0, 0, 0}, 518, 428);
        addPolygon(CardLocation.getPositionInGraveyard(0), new float[]{0, 122, 75, 122, 114, 0, 32, 0}, 1272, 587);
        //addPolygon(CardLocation.(5),new float[]{0, 202, 92, 202, 156, 0, 51, 0}, 1356, 200);
    }

    private void addPolygon(CardLocation location, float[] vertices, float translateX, float translateY) {
        Polygon polygon = new Polygon(vertices);
        polygon.translate(translateX, translateY);
        cardPolygons.put(location, polygon);
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
        stage.act(delta);
        stage.draw();
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
        firstPlayerHPLabel.setText(firstPlayerController.getPlayer().getUser().getUsername() + " : " + firstPlayerController.getPlayer().getLifePoints());
        secondPlayerHPLabel.setText(secondPlayerController.getPlayer().getUser().getUsername() + " : " + secondPlayerController.getPlayer().getLifePoints());
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

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }

    public ArrayBlockingQueue<GameCard> promptChoice(List<GameCard> cards) {
        return promptChoice(cards, "Select Card");
    }

    public ArrayBlockingQueue<GameCard> promptChoice(List<GameCard> cards, String message) {
        ArrayBlockingQueue<GameCard> choice = new ArrayBlockingQueue<>(1);
        System.out.println("Choice Called");
        if (cards.size() > 0) {
            final int[] selectedTarget = {-1};
            Dialog dialog = new Dialog(message, AssetController.getSkin("first")) {
                @Override
                protected void result(Object object) {
                    super.result(object);
                    Boolean result = (Boolean) object;
                    synchronized (choice) {
                        if (result && selectedTarget[0] != -1) {
                            choice.add(cards.get(selectedTarget[0]));
                        }
                    }
                    isDialogShown = false;
                    hide();
                }
            };
            Table table = new Table(AssetController.getSkin("first"));
            int counter = 0;
            for (GameCard card : cards) {
                Image image;
                if (!card.isRevealed() && card.isFaceDown()) {
                    image = new Image(AssetController.getDeck().getAtlas().findRegion("Unknown"));
                } else {
                    image = new Image(AssetController.getDeck().getAtlas().findRegion(Utils.firstUpperOnly(card.getCard().getName()).replaceAll("\\s+", "")));
                }
                int finalCounter = counter;
                image.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        selectedTarget[0] = finalCounter;
                        System.out.println("Selected Choice " + finalCounter);
                    }
                });
                if (card.isFaceDown()) {
                    table.add(image).width(250).height(150).pad(10);
                } else {
                    table.add(image).width(150).height(250).pad(10);
                }
                counter++;
            }
            table.row();
            ScrollPane scroll = new ScrollPane(table, AssetController.getSkin("first"));
            dialog.getContentTable().add(scroll).width(500).height(500);
            dialog.getContentTable().row();
            dialog.button("Select", true);
            dialog.button("Cancel", false);
            isDialogShown = true;
            dialog.show(stage);
        }
        return choice;
    }

    public ArrayBlockingQueue<Boolean> confirm(String message) {
        ArrayBlockingQueue<Boolean> choice = new ArrayBlockingQueue<>(1);
        System.out.println("Confirm Called");
        Dialog dialog = new Dialog("Confirmation", AssetController.getSkin("first")) {
            @Override
            protected void result(Object object) {
                super.result(object);
                Boolean result = (Boolean) object;
                if (result) {
                    choice.add(Boolean.TRUE);
                } else {
                    choice.add(Boolean.FALSE);
                }
                isDialogShown = false;
                hide();
            }
        };
        dialog.text(message);
        dialog.button("Confirm", true);
        dialog.button("Cancel", false);
        isDialogShown = true;
        dialog.show(stage);
        return choice;
    }
}
