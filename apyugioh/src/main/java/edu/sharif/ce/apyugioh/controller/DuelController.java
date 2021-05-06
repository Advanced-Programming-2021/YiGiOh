package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.player.NormalPlayerController;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.*;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.GameDeck;
import edu.sharif.ce.apyugioh.view.DuelView;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DuelController {

    @Getter
    private static DuelController instance;
    private static DuelView view;
    private static Logger logger;

    static {
        instance = new DuelController();
        view = new DuelView();
        logger = LogManager.getLogger(DuelController.class);
    }

    public void startMultiplayerDuel(String firstPlayerUsername, String secondPlayerUsername, int rounds) {
        if (isRoundCountValid(rounds)) return;
        if (isUsernameDifferent(firstPlayerUsername, secondPlayerUsername)) return;
        User firstUser = User.getUserByUsername(firstPlayerUsername);
        User secondUser = User.getUserByUsername(secondPlayerUsername);
        if (isUserDecksActive(firstPlayerUsername, secondPlayerUsername, firstUser, secondUser)) return;
        Deck firstDeck = Deck.getDeckByID(firstUser.getMainDeckID());
        Deck secondDeck = Deck.getDeckByID(secondUser.getMainDeckID());
        if (isUserDecksValid(firstPlayerUsername, secondPlayerUsername, firstDeck, secondDeck)) return;
        Player firstPlayer = initializePlayer(firstUser, firstDeck);
        Player secondPlayer = initializePlayer(secondUser, secondDeck);
        Random random = new Random();
        boolean isFirstPlayerTurn = random.nextBoolean();
        PlayerController firstPlayerController = new NormalPlayerController(isFirstPlayerTurn ? firstPlayer : secondPlayer);
        PlayerController secondPlayerController = new NormalPlayerController(isFirstPlayerTurn ? secondPlayer : firstPlayer);
        GameController gameController = new GameController(firstPlayerController, secondPlayerController, rounds);
        ProgramController.setGameControllerID(gameController.getId());
        logger.info("duel with id {} started between {} and {}", gameController.getId(), firstUser.getNickname(),
                secondUser.getNickname());
        gameController.play();
    }

    private boolean isUserDecksValid(String firstPlayerUsername, String secondPlayerUsername, Deck firstDeck, Deck secondDeck) {
        if (!firstDeck.isDeckValid()) {
            view.showError(DuelView.ERROR_DECK_INVALID, firstPlayerUsername);
            return true;
        }
        if (!secondDeck.isDeckValid()) {
            view.showError(DuelView.ERROR_DECK_INVALID, secondPlayerUsername);
            return true;
        }
        return false;
    }

    private boolean isUserDecksActive(String firstPlayerUsername, String secondPlayerUsername, User firstUser, User secondUser) {
        if (firstUser == null) {
            view.showError(DuelView.ERROR_USERNAME_INVALID, firstPlayerUsername);
            return true;
        }
        if (secondUser == null) {
            view.showError(DuelView.ERROR_USERNAME_INVALID, secondPlayerUsername);
            return true;
        }
        if (firstUser.getMainDeckID() == -1) {
            view.showError(DuelView.ERROR_ACTIVE_DECK_NOT_SET, firstPlayerUsername);
            return true;
        }
        if (secondUser.getMainDeckID() == -1) {
            view.showError(DuelView.ERROR_ACTIVE_DECK_NOT_SET, secondPlayerUsername);
            return true;
        }
        return false;
    }

    private boolean isUsernameDifferent(String firstPlayerUsername, String secondPlayerUsername) {
        if (firstPlayerUsername.equals(secondPlayerUsername)) {
            view.showError(DuelView.ERROR_USERNAME_SAME);
            return true;
        }
        return false;
    }

    private boolean isRoundCountValid(int rounds) {
        if (rounds != 3 && rounds != 1) {
            view.showError(DuelView.ERROR_ROUNDS_INVALID, "" + rounds);
            return true;
        }
        return false;
    }

    private Player initializePlayer(User user, Deck deck) {
        Player player = new Player();
        player.setUser(user);
        GameDeck gameDeck = initializeGameDeck(deck);
        player.setDeck(gameDeck);
        player.setLifePoints(8000);
        player.resetField();
        return player;
    }

    @NotNull
    private GameDeck initializeGameDeck(Deck deck) {
        GameDeck gameDeck = new GameDeck();
        List<Card> mainDeck = initializeDeck(deck.getMainDeck());
        Collections.shuffle(mainDeck);
        gameDeck.setMainDeck(mainDeck);
        List<Card> sideDeck = initializeDeck(deck.getSideDeck());
        Collections.shuffle(sideDeck);
        gameDeck.setSideDeck(sideDeck);
        return gameDeck;
    }

    @NotNull
    private List<Card> initializeDeck(Map<String, Integer> deck) {
        List<Card> cards = new ArrayList<>();
        for (Map.Entry<String, Integer> cardCount : deck.entrySet()) {
            Card card = DatabaseManager.getCards().getCardByName(cardCount.getKey());
            for (int i = 0; i < cardCount.getValue(); i++) {
                cards.add(card);
            }
        }
        return cards;
    }

}
