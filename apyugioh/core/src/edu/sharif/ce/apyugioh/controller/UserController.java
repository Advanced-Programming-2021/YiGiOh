package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.Gdx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.networking.request.LoginRequest;
import edu.sharif.ce.apyugioh.model.networking.request.RegisterRequest;
import edu.sharif.ce.apyugioh.model.networking.response.LoginResponse;
import edu.sharif.ce.apyugioh.model.networking.response.RegisterResponse;
import edu.sharif.ce.apyugioh.view.menu.UserMenuView;
import lombok.Getter;

public class UserController {

    @Getter
    private static UserController instance;
    private static UserMenuView view;
    private static Logger logger;

    static {
        instance = new UserController();
        view = new UserMenuView(ProgramController.getGame());
        logger = LogManager.getLogger(UserController.class);
    }

    private UserController() {
    }

    public void registerUser(String username, String password, String nickname) {
        ProgramController.getClient().sendTCP(new RegisterRequest(username, password, nickname));
        logger.info("{} registered in with {} : {}", nickname, username, password);
    }

    public void registerUser(RegisterResponse response) {
        Gdx.app.postRunnable(() -> {
            view.showMessage(response.getMessage());
        });
    }

    public void loginUser(String username, String password) {
        ProgramController.getClient().sendTCP(new LoginRequest(username, password));
    }

    public void loginUser(LoginResponse response) {
        if (response.isSuccessful()) {
            Gdx.app.postRunnable(() -> {
                MainMenuController.getInstance().setUser(response.getUser());
                MainMenuController.getInstance().showMainMenu();
                view.dispose();
            });
            logger.info("{} logged in with {} : {}", response.getUser().getNickname(), response.getUser().getUsername(), response.getUser().getPassword());
        } else {
            view.showMessage(response.getMessage());
        }
    }

    public void logoutUser() {
        User user = MainMenuController.getInstance().getUser();
        logger.info("{} logged in with {} : {}", user.getNickname(),
                user.getUsername(), user.getPassword());
        showMenu();
    }

    public void showMenu() {
        ProgramController.setState(MenuState.LOGIN);
        ProgramController.getGame().setScreen(view);
    }
}
