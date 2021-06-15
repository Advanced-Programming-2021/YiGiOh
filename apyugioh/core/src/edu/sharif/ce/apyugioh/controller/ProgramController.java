package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.MenuState;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class ProgramController {

    @Getter
    private static YuGiOh game;
    @Getter
    private static ProgramController instance;
    @Getter
    @Setter
    private static MenuState state;
    @Getter
    @Setter
    private static int gameControllerID;

    static {
        instance = new ProgramController();
        state = MenuState.LOGIN;
        gameControllerID = -1;
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
        loadCursor();
        MainMenuController.getInstance().showMainMenu();
    }

    public void loadCursor() {
        Pixmap pm = new Pixmap(Gdx.files.internal("cursor1.png"));
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(pm, 0, 0));
        pm.dispose();
    }
}
