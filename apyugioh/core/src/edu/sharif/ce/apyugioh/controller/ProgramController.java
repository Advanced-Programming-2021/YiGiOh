package edu.sharif.ce.apyugioh.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.MenuState;
import lombok.Getter;
import lombok.Setter;

public class ProgramController {

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

    public void initialize() {
        logger.info("initialization started");
        DatabaseManager.init();
    }
}
