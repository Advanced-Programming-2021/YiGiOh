package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.*;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.view.DuelView;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DuelController {

    @Getter
    private static DuelController instance;
    private static DuelView view;

    static {
        instance = new DuelController();
        view = new DuelView();
    }

    public void startMultiplayerDuel(String firstPlayerUsername, String secondPlayerUsername, int rounds) {
        if (rounds != 3 && rounds != 1) {
            view.showError(DuelView.ERROR_ROUNDS_INVALID, "" + rounds);
            return;
        }
        if (firstPlayerUsername.equals(secondPlayerUsername)) {
            view.showError(DuelView.ERROR_USERNAME_SAME);
            return;
        }
        User firstUser = User.getUserByUsername(firstPlayerUsername);
        if (firstUser == null) {
            view.showError(DuelView.ERROR_USERNAME_INVALID, firstPlayerUsername);
            return;
        }
        User secondUser = User.getUserByUsername(secondPlayerUsername);
        if (secondUser == null) {
            view.showError(DuelView.ERROR_USERNAME_INVALID, secondPlayerUsername);
            return;
        }
        if (firstUser.getMainDeckID() == -1) {
            view.showError(DuelView.ERROR_ACTIVE_DECK_NOT_SET, firstPlayerUsername);
            return;
        }
        if (secondUser.getMainDeckID() == -1) {
            view.showError(DuelView.ERROR_ACTIVE_DECK_NOT_SET, secondPlayerUsername);
            return;
        }
        Deck firstDeck = Deck.getDeckByID(firstUser.getMainDeckID());
        if (!firstDeck.isDeckValid()) {
            view.showError(DuelView.ERROR_DECK_INVALID, firstPlayerUsername);
            return;
        }
        Deck secondDeck = Deck.getDeckByID(secondUser.getMainDeckID());
        if (!secondDeck.isDeckValid()) {
            view.showError(DuelView.ERROR_DECK_INVALID, secondPlayerUsername);
            return;
        }
        Player firstPlayer = initializePlayer(firstUser, firstDeck);
        Player secondPlayer = initializePlayer(secondUser, secondDeck);
        Random random = new Random();
        boolean isFirstPlayerTurn = random.nextBoolean();
        PlayerController firstPlayerController = null;
        PlayerController secondPlayerController = null;
        GameController gameController = new GameController(isFirstPlayerTurn ? firstPlayerController : secondPlayerController,
                isFirstPlayerTurn ? secondPlayerController : firstPlayerController, rounds);
        ProgramController.setGameControllerID(gameController.getId());
        gameController.play();
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
