package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.handler.Handler;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.networking.request.LoginRequest;
import edu.sharif.ce.apyugioh.model.networking.request.RegisterRequest;
import edu.sharif.ce.apyugioh.model.networking.response.LoginResponse;
import edu.sharif.ce.apyugioh.model.networking.response.RegisterResponse;
import edu.sharif.ce.apyugioh.view.menu.Menu;
import lombok.Getter;
import lombok.Setter;

public class ProgramController {

    @Getter
    private static YuGiOh game;
    @Getter
    private static ProgramController instance;
    @Getter
    @Setter
    private static MenuState state;
    @Getter
    private static Menu currentMenu;
    @Getter
    @Setter
    private static int gameControllerID;
    @Getter
    @Setter
    private static Client client;
    @Getter
    private static List<Handler> handlers;

    static {
        instance = new ProgramController();
        state = MenuState.LOGIN;
        gameControllerID = -1;
        handlers = new ArrayList<>();
    }

    private ProgramController() {
    }

    private static Logger logger = LogManager.getLogger(ProgramController.class);

    public static PlayerController getCurrentPlayerController() {
        if (gameControllerID != -1) {
            return GameController.getGameControllerById(gameControllerID).getCurrentPlayerController();
        }
        return null;
    }

    public static PlayerController getRivalPlayerController() {
        if (gameControllerID != -1) {
            return GameController.getGameControllerById(gameControllerID).getRivalPlayerController();
        }
        return null;
    }

    public void initialize(YuGiOh game) {
        ProgramController.game = game;
        logger.info("initialization started");
        DatabaseManager.init();
        AssetController.loadAssets();
        AssetController.load3DAssets();
        loadCursor();
        try {
            initializeClient();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        addHandlers();
        UserController.getInstance().showMenu();
        MainMenuController.getView();
    }

    private void addHandlers() {
        Handler registerHandler = new Handler() {
            @Override
            public boolean handleAction() {
                if (handleObject instanceof RegisterResponse) {
                    RegisterResponse response = (RegisterResponse) handleObject;
                    UserController.getInstance().registerUser(response);
                    return true;
                }
                return false;
            }
        };
        handlers.add(registerHandler);
        Handler loginHandler = new Handler() {
            @Override
            public boolean handleAction() {
                if (handleObject instanceof LoginResponse) {
                    LoginResponse response = (LoginResponse) handleObject;
                    UserController.getInstance().loginUser(response);
                    return true;
                }
                return false;
            }
        };
        handlers.add(loginHandler);
        connectHandlers();
    }

    public void connectHandlers() {
        for (int i = 0; i < handlers.size(); i++) {
            if (i != handlers.size() - 1) {
                handlers.get(i).setNextHandler(handlers.get(i + 1));
            }
        }
    }

    private void initializeClient() throws IOException {
        Log.set(Log.LEVEL_TRACE);
        client = new Client();
        client.start();
        registerKryoClasses();
        client.connect(5000, "localhost", 55698, 55687);
        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                System.out.println(connection.toString() + " Connected");
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
            }

            @Override
            public void received(Connection connection, Object o) {
                super.received(connection, o);
                if (!handlers.isEmpty()) {
                    handlers.get(0).setHandleObject(o);
                    handlers.get(0).setConnection(connection);
                    handlers.get(0).handle();
                }
            }
        });
    }

    private void registerKryoClasses() {
        Kryo kryo = client.getKryo();
        kryo.register(RegisterRequest.class);
        kryo.register(LoginRequest.class);
        kryo.register(RegisterResponse.class);
        kryo.register(LoginResponse.class);
        kryo.register(User.class);
    }

    public void loadCursor() {
        Pixmap pm = new Pixmap(Gdx.files.internal("cursor1.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();
    }

    public static void setCurrentMenu(Menu menu) {
        currentMenu = menu;
        game.setScreen(currentMenu);
    }
}
