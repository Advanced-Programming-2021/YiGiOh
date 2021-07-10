package edu.sharif.ce.apyugioh.controller.player;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.view.menu.GameMenuView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.GameCard;

public class NormalPlayerController extends PlayerController {

    public NormalPlayerController(Player player) {
        super(player);
    }

    //special Cases

    //Scanner
    public void scanMonsterForScanner(SelectionAction action) {
        super.scanMonsterForScanner();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //TributeMonsterForSummon
    public void tributeMonster(int amount, ArraySelectionAction action) {
        ArrayBlockingQueue<GameCard[]> cards = new ArrayBlockingQueue<>(1);
        super.tributeMonster(amount);

        GameCard[] cardsArray = new GameCard[amount];
        for (int i = 0; i < amount; i++) {
            int finalI = i;
            getGameCard(availableCards, new SelectionAction() {
                @Override
                public GameCard call() throws Exception {
                    if (choice == null) return null;
                    GameCard gameCard = choice.peek();
                    if (gameCard == null) return null;
                    cardsArray[finalI] = gameCard;
                    availableCards.remove(gameCard);
                    return null;
                }
            });
        }

        cards.add(cardsArray);

        getGameController().getExecutor().submit(() -> {
            while (cards.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoices(cards);
        getGameController().getExecutor().submit(action);
    }

    //Man-Eater Bug
    public void directRemove(SelectionAction action) {
        super.directRemove();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //TexChanger
    public void specialCyberseSummon(SelectionAction action) {
        super.specialCyberseSummon();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //HeraldOfCreation
    public void summonFromGraveyard(SelectionAction action) {
        super.summonFromGraveyard();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //Beast King Barbaros & Tricky
    public int chooseHowToSummon(List<String> choices) {
        String result = GameController.getView().promptChoice(choices.toArray(new String[0]), "Choose how to summon");
        if (result == null) return -1;
        for (int i = 0; i < choices.size(); i++) {
            if (choices.get(i).equals(result)) {
                return i;
            }
        }
        return -1;
    }

    //terratiger
    public void selectMonsterToSummon(SelectionAction action) {
        super.selectMonsterToSummon();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //EquipMonster
    public void equipMonster(SelectionAction action) {
        super.equipMonster();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //Select card from graveyard
    public void selectCardFromGraveyard(SelectionAction action) {
        super.selectCardFromGraveyard();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //Select card from monster zone
    public void selectCardFromMonsterZone(SelectionAction action) {
        super.selectCardFromMonsterZone();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //Select card from both graveyards
    public void selectMonsterFromAllGraveyards(SelectionAction action) {
        super.selectMonsterFromAllGraveyards();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //Select card from hand
    public void selectCardFromHand(GameCard exceptCard, SelectionAction action) {
        super.selectCardFromHand(exceptCard);
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    //Select card from deck
    public void selectCardFromDeck(SelectionAction action) {
        super.selectCardFromDeck();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    public void selectFieldSpellFromDeck(SelectionAction action) {
        super.selectFieldSpellFromDeck();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    public void selectRivalMonster(SelectionAction action) {
        super.selectRivalMonster();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    public void selectSpellTrapsFromField(int amount, ArraySelectionAction action) {
        super.selectSpellTrapsFromField(amount);
        ArrayBlockingQueue<GameCard[]> cards = new ArrayBlockingQueue<>(1);

        GameCard[] cardsArray = new GameCard[amount];
        for (int i = 0; i < amount; i++) {
            int finalI = i;
            getGameCard(availableCards, new SelectionAction() {
                @Override
                public GameCard call() throws Exception {
                    if (choice == null) return null;
                    GameCard gameCard = choice.peek();
                    if (gameCard == null) return null;
                    cardsArray[finalI] = gameCard;
                    availableCards.remove(gameCard);
                    return null;
                }
            });
        }

        cards.add(cardsArray);

        getGameController().getExecutor().submit(() -> {
            while (cards.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoices(cards);
        getGameController().getExecutor().submit(action);
    }

    //Select card from graveyard with level less than mostLevel
    public void selectCardFromGraveyard(int mostLevel, SelectionAction action) {
        super.selectCardFromGraveyard(mostLevel);
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    public void selectNormalCardFromHand(int mostLevel, SelectionAction action) {
        super.selectNormalCardFromHand(mostLevel);
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    public void selectForceCardFromHand(SelectionAction action) {
        super.selectCardFromHand(null);
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getForceGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    public void selectRitualMonsterFromHand(SelectionAction action) {
        super.selectRitualMonsterFromHand();
        ArrayBlockingQueue<GameCard> card = new ArrayBlockingQueue<>(1);

        getForceGameCard(availableCards, new SelectionAction() {
            @Override
            public GameCard call() throws Exception {
                if (choice == null) return null;
                GameCard gameCard = choice.peek();
                if (gameCard == null) return null;
                card.add(gameCard);
                return null;
            }
        });

        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }

    public void selectCardsForRitualTribute(int level, ArraySelectionAction action) {
        super.selectCardsForRitualTribute(level);
        ArrayBlockingQueue<GameCard[]> cards = new ArrayBlockingQueue<>(1);

    }

    public Card getACard() {
        String result = GameController.getView().promptChoice(DatabaseManager.getCards().getAllCardNames());
        if (result == null) return null;
        return DatabaseManager.getCards().getCardByName(result);
    }

    public GameCard selectRandomCardFromHand() {
        super.selectRandomCardFromHand();
        return availableCards.get(new Random().nextInt(availableCards.size()));
    }

    @Override
    public void confirm(String message, ConfirmationAction action) {
        ArrayBlockingQueue<Boolean> choice = GameController.getUIView().confirm(message);
        getGameController().getExecutor().submit(() -> {
            while (choice.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(choice);
        getGameController().getExecutor().submit(action);
    }

    private void getGameCard(List<GameCard> availableMonsters, SelectionAction action) {
        ArrayBlockingQueue<GameCard> card = GameController.getUIView().promptChoice(availableMonsters);
        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }


    private void getForceGameCard(List<GameCard> availableMonsters, SelectionAction action) {
        ArrayBlockingQueue<GameCard> card = GameController.getUIView().forcePromptChoice(availableMonsters);
        getGameController().getExecutor().submit(() -> {
            while (card.isEmpty()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        action.setChoice(card);
        getGameController().getExecutor().submit(action);
    }
}
