package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.Effects;
import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.ProfilePicture;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.view.ButtonClickListener;
import edu.sharif.ce.apyugioh.view.command.CheatExecutor;
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
    public Stage stage;
    private SpriteBatch batch;
    private Array<CardModelView> cards;
    private DeckModelView deck;
    private GameActionsManager manager;
    private CardFrontView board;
    private PlayerController firstPlayerController, secondPlayerController;
    private GameDeckModelView cardViews;
    private HashMap<CardLocation, Polygon> cardPolygons;
    private Label phaseLabel, currentPlayerHPLabel, rivalPlayerHPLabel, currentPlayerNameLabel, currentPlayerNicknameLabel, rivalPlayerNameLabel, rivalPlayerNicknameLabel;
    private boolean isDialogShown;
    private ShapeRenderer shapeRenderer;
    private Polygon selectedPolygon;
    private Rectangle attackRect, firstHPBar, secondHPBar;
    private ProfilePicture currentPlayerProfilePicture, rivalPlayerProfilePicture;
    private boolean isGameEnded;
    private Console gameConsole;

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
        currentPlayerHPLabel = new Label("", AssetController.getSkin("first"), "title");
        rivalPlayerHPLabel = new Label("", AssetController.getSkin("first"), "title");
        attackRect = new Rectangle();
        attackRect.set(-1, -1, 0, 0);
        firstHPBar = new Rectangle();
        secondHPBar = new Rectangle();
    }

    @Override
    public void show() {
        super.show();
        shapeRenderer = new ShapeRenderer();
        board = new CardFrontView(new Sprite(new Texture(Gdx.files.internal("backgrounds/board.png"))));
        board.environment = environment;
        board.worldTransform.setToRotation(0, 1, 0, 270);
        board.worldTransform.scale(8f, 8f, 8f);
        board.worldTransform.setTranslation(75, 0, 0);
        cam.position.set(0, -70, 0);
        cam.lookAt(75, -25, 0);
        cam.update();
        addLabelsToStage();
        addButtonsToStage();
        addPolygons();
        initializeUsersDetail(50);
        Gdx.input.setInputProcessor(stage);
        addSelectionListenerToStage();
        AssetController.playMusic("gameplay", 0.5f);
        gameConsole = new GUIConsole();
        gameConsole.setCommandExecutor(new CheatExecutor());
        gameConsole.setDisplayKeyID(Input.Keys.GRAVE);
        gameConsole.setSizePercent(30, 30);
        gameConsole.setPositionPercent(5, 5);
    }

    private void addButtonsToStage() {
        addNextPhaseButton();
        addSummonButton();
        addSetButton();
        addAttackButton();
    }

    private void addPolygons() {
        addFirstPlayerMonsterZonePolygons();
        addFirstPlayerSpellZonePolygons();
        addFirstPlayerHandPolygons();
        addFirstPlayerExtraPolygons();
    }

    private void addSelectionListenerToStage() {
        stage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!manager.isDone() || isDialogShown) return;
                System.out.println(x + " : " + y + " clicked!");
                boolean isSelected = false;
                for (Map.Entry<CardLocation, Polygon> polygon : cardPolygons.entrySet()) {
                    if (polygon.getValue().contains(x, y)) {
                        if (polygon.getKey().isFromMonsterZone() && getGameController().getCurrentPlayer().getField().getMonsterZone()[polygon.getKey().getPosition()] == null)
                            continue;
                        if (polygon.getKey().isFromSpellZone() && getGameController().getCurrentPlayer().getField().getSpellZone()[polygon.getKey().getPosition()] == null)
                            continue;
                        if (polygon.getKey().isInHand() && getGameController().getCurrentPlayer().getField().getHand().size() <= polygon.getKey().getPosition())
                            continue;
                        if (polygon.getKey().isFromFieldZone() && getGameController().getCurrentPlayer().getField().getFieldZone() == null)
                            continue;
                        if (polygon.getKey().isFromGraveyard() && getGameController().getCurrentPlayer().getField().getGraveyard().size() < 1) {
                            continue;
                        } else if (polygon.getKey().isFromGraveyard()) {
                            promptChoice(getGameController().getCurrentPlayer().getField().getGraveyard(), "Graveyard", false);
                            return;
                        }
                        System.out.println("Clicked " + polygon.getKey().toString());
                        getGameController().select(polygon.getKey());
                        selectedPolygon = polygon.getValue();
                        isSelected = true;
                        break;
                    }
                }
                if (!isSelected) {
                    getGameController().deselect();
                    selectedPolygon = null;
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
                    showAttackDialog();
                }
            }
        });
        stage.addActor(attackButton);
    }

    private void showAttackDialog() {
        final int[] selectedTarget = {-1};
        boolean isEmpty = true;
        for (int i = 0; i < getGameController().getRivalPlayer().getField().getMonsterZone().length; i++) {
            if (getGameController().getRivalPlayer().getField().getMonsterZone()[i] != null) {
                isEmpty = false;
                break;
            }
        }
        Dialog dialog = createAttackDialog(selectedTarget, isEmpty);
        if (isEmpty) {
            dialog.button("Attack Direct", true);
        } else {
            addTargetsToAttackDialog(selectedTarget, dialog);
        }
        dialog.button("Cancel", false);
        isDialogShown = true;
        dialog.show(stage);
    }

    private void addTargetsToAttackDialog(int[] selectedTarget, Dialog dialog) {
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
                if (getGameController().getRivalPlayer().getField().getMonsterZone()[i].isFaceDown()) {
                    table.add(image).width(250).height(150).pad(10);
                } else {
                    table.add(image).width(150).height(250).pad(10);
                }
                image.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        selectedTarget[0] = finalI;
                        System.out.println("Selected Target " + (finalI + 1));
                        Vector2 imageCoordinates = image.localToStageCoordinates(new Vector2(0, 0));
                        if (getGameController().getRivalPlayer().getField().getMonsterZone()[finalI].isFaceDown()) {
                            attackRect.set(imageCoordinates.x, imageCoordinates.y, 250, 150);
                        } else {
                            attackRect.set(imageCoordinates.x, imageCoordinates.y, 150, 250);
                        }
                    }
                });
            }
        }
        table.row();
        ScrollPane scroll = new ScrollPane(table, AssetController.getSkin("first"));
        dialog.getContentTable().add(scroll).width(450).height(500);
        dialog.getContentTable().row();
        dialog.button("Attack", true);
    }

    private Dialog createAttackDialog(int[] selectedTarget, boolean finalIsEmpty) {
        Dialog dialog = new Dialog("Attack", AssetController.getSkin("first")) {
            @Override
            protected void result(Object object) {
                super.result(object);
                Boolean result = (Boolean) object;
                if (result) {
                    if (getGameController().getSelectionController() != null) {
                        if (finalIsEmpty) {
                            System.out.println("Direct Attack");
                            getGameController().getCurrentPlayerController().directAttack();
                            AssetController.playSound("gameplay_hit");
                        } else if (selectedTarget[0] != -1) {
                            System.out.println("Attack " + selectedTarget[0] + 1);
                            getGameController().getCurrentPlayerController().attack(selectedTarget[0] + 1);
                            AssetController.playSound("gameplay_attack");
                        }
                        selectedPolygon = null;
                        isDialogShown = false;
                        attackRect.set(-1, -1, 0, 0);
                        hide();
                    } else {
                        System.out.println("Selection is Empty!");
                    }
                } else {
                    isDialogShown = false;
                    attackRect.set(-1, -1, 0, 0);
                    hide();
                }
            }
        };
        return dialog;
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
                    selectedPolygon = null;
                    AssetController.playSound("gameplay_summon");
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
                    selectedPolygon = null;
                    AssetController.playSound("gameplay_summon");
                }
            }
        });
        stage.addActor(summonButton);
    }

    private void addNextPhaseButton() {
        TextButton nextPhaseButton = new TextButton("Next Phase", AssetController.getSkin("first"));
        nextPhaseButton.setPosition(1450, 500);
        nextPhaseButton.setSize(200, 50);
        nextPhaseButton.addListener(new ButtonClickListener() {
            @Override
            public void clickAction() {
                if (manager.isDone()) {
                    getGameController().getCurrentPlayerController().nextPhase();
                    phaseLabel.setText("Phase: " + getGameController().getGameTurnController().getPhase().toString());
                    if (getGameController().getGameTurnController().getPhase().phaseLevel > Phase.MAIN2.phaseLevel) {
                        selectedPolygon = null;
                    }
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
        if (getGameController().getCurrentPlayerController().getPlayer().getField().getFieldZone() != null) {
            if (getGameController().getCurrentPlayerController().getPlayer().getField().getFieldZone().getCard().getCardEffects().contains(Effects.YAMI)) {
                board.material.set(new ColorAttribute(ColorAttribute.Diffuse, Color.DARK_GRAY));
            } else if (getGameController().getCurrentPlayerController().getPlayer().getField().getFieldZone().getCard().getCardEffects().contains(Effects.FOREST)) {
                board.material.set(new ColorAttribute(ColorAttribute.Diffuse, Color.GREEN));
            } else if (getGameController().getCurrentPlayerController().getPlayer().getField().getFieldZone().getCard().getCardEffects().contains(Effects.CLOSED_FOREST)) {
                board.material.set(new ColorAttribute(ColorAttribute.Diffuse, Color.FOREST));
            } else if (getGameController().getCurrentPlayerController().getPlayer().getField().getFieldZone().getCard().getCardEffects().contains(Effects.UMIIRUKA)) {
                board.material.set(new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE));
            }
        } else {
            board.material.set(new ColorAttribute(ColorAttribute.Diffuse, Color.CYAN));
        }
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.render(board);
        for (CardModelView card : cardViews.getAllCards()) {
            card.render(modelBatch, environment);
        }
        modelBatch.end();

        updateUsersDetail();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Math.min(1, 1 - getGameController().getRivalPlayerController().getPlayer().getLifePoints() / 8000f), Math.min(1, firstPlayerController.getPlayer().getLifePoints() / 8000f), 0, 1);
        shapeRenderer.rect(firstHPBar.x, firstHPBar.y, firstHPBar.width, firstHPBar.height);
        shapeRenderer.setColor(Math.min(1, 1 - getGameController().getCurrentPlayerController().getPlayer().getLifePoints() / 8000f), Math.min(1, secondPlayerController.getPlayer().getLifePoints() / 8000f), 0, 1);
        shapeRenderer.rect(secondHPBar.x, secondHPBar.y, secondHPBar.width, secondHPBar.height);
        shapeRenderer.end();

        stage.act(delta);
        stage.draw();
        if (selectedPolygon != null && !isDialogShown) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.CYAN);
            shapeRenderer.polygon(selectedPolygon.getTransformedVertices());
            shapeRenderer.end();
        } else if (isDialogShown && attackRect.getWidth() != 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.PINK);
            shapeRenderer.rect(attackRect.getX(), attackRect.getY(), attackRect.getWidth(), attackRect.getHeight());
            shapeRenderer.end();
        }

        if (isGameEnded) {
            Gdx.input.setInputProcessor(null);
            phaseLabel.setText((firstPlayerController.getPlayer().getLifePoints() > 0 ? firstPlayerController.getPlayer().getUser().getNickname() : secondPlayerController.getPlayer().getUser().getNickname()) + " Won");
        }
        gameConsole.draw();
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
            card.scale(0.45f, 0.45f, 0.45f);
            card.setTranslation(0, 0, 0);
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
        updateFieldZone();
        updateDeck();
        updateHand();
        currentPlayerHPLabel.setText((isFirstPlayerTurn ? firstPlayerController : secondPlayerController).getPlayer().getUser().getUsername() + " : " + (isFirstPlayerTurn ? firstPlayerController : secondPlayerController).getPlayer().getLifePoints());
        rivalPlayerHPLabel.setText((isFirstPlayerTurn ? secondPlayerController : firstPlayerController).getPlayer().getUser().getUsername() + " : " + (isFirstPlayerTurn ? secondPlayerController : firstPlayerController).getPlayer().getLifePoints());
        firstHPBar.setWidth(300 * (isFirstPlayerTurn ? secondPlayerController : firstPlayerController).getPlayer().getLifePoints() / 8000f);
        secondHPBar.setWidth(300 * (isFirstPlayerTurn ? firstPlayerController : secondPlayerController).getPlayer().getLifePoints() / 8000f);
        if (firstPlayerController.getPlayer().getLifePoints() == 0 || secondPlayerController.getPlayer().getLifePoints() == 0) {
            isGameEnded = true;
            AssetController.playSound("gameplay_lose");
        }
    }

    private void updateFieldZone() {
        if (firstPlayerController.getPlayer().getField().getFieldZone() != null) {
            CardModelView cardView = cardViews.getCard(firstPlayerController.getPlayer().getField().getFieldZone().getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, -90);
            target.setTranslation(74, -29f, -38.5f);
            CardAction action = new CardAction(cardView, target, 1);
            manager.addAction(action);
        }
        if (secondPlayerController.getPlayer().getField().getFieldZone() != null) {
            CardModelView cardView = cardViews.getCard(secondPlayerController.getPlayer().getField().getFieldZone().getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, -90);
            target.rotate(0, 0, 1, 180);
            target.setTranslation(74, 29f, 38.5f);
            CardAction action = new CardAction(cardView, target, 1);
            manager.addAction(action);
        }
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
                if (isFirstPlayerTurn) {
                    target.setToRotation(0, 1, 0, -90);
                } else {
                    target.setToRotation(0, 1, 0, card.isRevealed() ? -90 : 90);
                }
                target.rotate(0, 0, 1, card.isFaceDown() ? -90 : 0);
                target.setTranslation(74, -40, (counter % 2 == 0 ? -1 : 1) * ((counter + 1) / 2) * 13f);
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
                target.setTranslation(74, 40, (counter % 2 == 0 ? 1 : -1) * ((counter + 1) / 2) * 13f);
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
            CardAction action = new CardAction(cardView, target, 5);
            manager.addAction(action);
        }
        counter = 0;
        for (GameCard card : secondPlayerController.getPlayer().getField().getDeck()) {
            CardModelView cardView = cardViews.getCard(card.getId());
            Matrix4 target = cardView.getTransform();
            target.setToRotation(0, 1, 0, 90);
            target.rotate(0, 0, 1, 180);
            target.setTranslation(74 - (++counter) * 0.01f, 49.5f, -43f - (counter) * 0.01f);
            CardAction action = new CardAction(cardView, target, 5);
            manager.addAction(action);
        }
    }

    private void initializeUsersDetail(int x) {
        firstHPBar.set(x + 50, 150, 300, 30);
        secondHPBar.set(x + 50, Gdx.graphics.getHeight() - 205, 300, 30);
        Image currentImage = new Image(new Texture(Gdx.files.internal("skins/profile_frame.png")));
        currentImage.setPosition(x, Gdx.graphics.getHeight() - 200);
        Image rivalImage = new Image(new Texture(Gdx.files.internal("skins/profile_frame.png")));
        rivalImage.setPosition(x, 150);
        Table currentTable = new Table(AssetController.getSkin("first"));
        Table rivalTable = new Table(AssetController.getSkin("first"));
        currentPlayerNameLabel = new Label("" + getGameController().getCurrentPlayer().getUser().getUsername(), AssetController.getSkin("first"), "title");
        currentPlayerNicknameLabel = new Label("" + getGameController().getCurrentPlayer().getUser().getNickname(), AssetController.getSkin("first"));
        currentTable.add(currentPlayerNameLabel).spaceBottom(5).left();
        currentTable.row();
        currentTable.add(currentPlayerNicknameLabel).left().spaceBottom(15);
        currentTable.row();
        currentTable.setPosition(x + 75 + currentImage.getWidth(), 200);
        rivalPlayerNameLabel = new Label("" + getGameController().getRivalPlayer().getUser().getUsername(), AssetController.getSkin("first"), "title");
        rivalPlayerNicknameLabel = new Label("" + getGameController().getRivalPlayer().getUser().getNickname(), AssetController.getSkin("first"));
        rivalTable.add(rivalPlayerNameLabel).spaceBottom(5).left();
        rivalTable.row();
        rivalTable.add(rivalPlayerNicknameLabel).left().spaceBottom(15);
        rivalTable.row();
        rivalTable.setPosition(x + 75 + currentImage.getWidth(), Gdx.graphics.getHeight() - 150);
        currentPlayerProfilePicture = new ProfilePicture(Gdx.files.local("assets/db/profiles/" + getGameController().getCurrentPlayer().getUser().getAvatarName()), true);
        rivalPlayerProfilePicture = new ProfilePicture(Gdx.files.local("assets/db/profiles/" + getGameController().getRivalPlayer().getUser().getAvatarName()), true);
        currentPlayerProfilePicture.setYPosition(164);
        currentPlayerProfilePicture.setXPosition(x + 49);
        rivalPlayerProfilePicture.setXPosition(x + 49);

        currentPlayerHPLabel.setText("LP: " + getGameController().getCurrentPlayer().getLifePoints());
        currentTable.add(currentPlayerHPLabel).padLeft(-25);
        rivalPlayerHPLabel.setText("LP: " + getGameController().getRivalPlayer().getLifePoints());
        rivalTable.add(rivalPlayerHPLabel).padLeft(-25);
        stage.addActor(currentImage);
        stage.addActor(rivalImage);
        stage.addActor(currentTable);
        stage.addActor(rivalTable);
        stage.addActor(currentPlayerProfilePicture);
        stage.addActor(rivalPlayerProfilePicture);
    }

    private void addLabelsToStage() {
        phaseLabel.setPosition(1450, 780);
        stage.addActor(phaseLabel);
    }

    private void updateUsersDetail() {
        currentPlayerProfilePicture.setProfilePicture(Gdx.files.local("assets/db/profiles/" + getGameController().getCurrentPlayer().getUser().getAvatarName()), true);
        currentPlayerNameLabel.setText("" + getGameController().getCurrentPlayer().getUser().getUsername());
        currentPlayerNicknameLabel.setText("" + getGameController().getCurrentPlayer().getUser().getNickname());
        rivalPlayerProfilePicture.setProfilePicture(Gdx.files.local("assets/db/profiles/" + getGameController().getRivalPlayer().getUser().getAvatarName()), true);
        rivalPlayerNameLabel.setText("" + getGameController().getRivalPlayer().getUser().getUsername());
        rivalPlayerNicknameLabel.setText("" + getGameController().getRivalPlayer().getUser().getNickname());
        currentPlayerHPLabel.setText("LP: " + getGameController().getCurrentPlayer().getLifePoints());
        rivalPlayerHPLabel.setText("LP: " + getGameController().getRivalPlayer().getLifePoints());
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }

    public ArrayBlockingQueue<GameCard> forcePromptChoice(List<GameCard> cards) {
        return promptChoice(cards, "Select Card", true);
    }

    public ArrayBlockingQueue<GameCard> promptChoice(List<GameCard> cards) {
        return promptChoice(cards, "Select Card", false);
    }

    public ArrayBlockingQueue<GameCard> promptChoice(List<GameCard> cards, String message, boolean isForce) {
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
                    attackRect.set(-1, -1, 0, 0);
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
                        Vector2 imageCoordinates = image.localToStageCoordinates(new Vector2(0, 0));
                        if (card.isFaceDown()) {
                            attackRect.set(imageCoordinates.x, imageCoordinates.y, 250, 150);
                        } else {
                            attackRect.set(imageCoordinates.x, imageCoordinates.y, 150, 250);
                        }
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
            if (!isForce) dialog.button("Cancel", false);
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

    @Override
    public void pause() {
        super.pause();
        AssetController.pauseMusic();
    }

    @Override
    public void resume() {
        super.resume();
        AssetController.resumeMusic();
    }
}
