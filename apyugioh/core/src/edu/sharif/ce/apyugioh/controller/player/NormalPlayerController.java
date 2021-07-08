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
    public GameCard scanMonsterForScanner() {
        super.scanMonsterForScanner();
        return getGameCard(availableCards);
    }

    //TributeMonsterForSummon
    public GameCard[] tributeMonster(int amount) {
        super.tributeMonster(amount);
        GameCard[] cards = new GameCard[amount];
        for (int i = 0; i < amount; i++) {
            GameCard selectedMonster = getGameCard(availableCards);
            cards[i] = selectedMonster;
            availableCards.remove(selectedMonster);
        }
        return cards;
    }

    //Man-Eater Bug
    public GameCard directRemove() {
        super.directRemove();
        return getGameCard(availableCards);
    }

    //TexChanger
    public GameCard specialCyberseSummon() {
        super.specialCyberseSummon();
        return getGameCard(availableCards);
    }

    //HeraldOfCreation
    public GameCard summonFromGraveyard() {
        super.summonFromGraveyard();
        return getGameCard(availableCards);
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
    public GameCard selectMonsterToSummon() {
        super.selectMonsterToSummon();
        return getGameCard(availableCards);
    }

    //EquipMonster
    public GameCard equipMonster() {
        super.equipMonster();
        return getGameCard(availableCards);
    }

    //Select card from graveyard
    public GameCard selectCardFromGraveyard() {
        super.selectCardFromGraveyard();
        return getGameCard(availableCards);
    }

    //Select card from monster zone
    public GameCard selectCardFromMonsterZone() {
        super.selectCardFromMonsterZone();
        return getGameCard(availableCards);
    }

    //Select card from both graveyards
    public GameCard selectMonsterFromAllGraveyards() {
        super.selectMonsterFromAllGraveyards();
        return getGameCard(availableCards);
    }

    //Select card from hand
    public GameCard selectCardFromHand(GameCard exceptCard) {
        super.selectCardFromHand(exceptCard);
        return getGameCard(availableCards);
    }

    //Select card from deck
    public GameCard selectCardFromDeck() {
        super.selectCardFromDeck();
        return getGameCard(availableCards);
    }

    public GameCard selectFieldSpellFromDeck() {
        super.selectFieldSpellFromDeck();
        return getGameCard(availableCards);
    }

    public GameCard selectRivalMonster() {
        super.selectRivalMonster();
        return getGameCard(availableCards);
    }

    public GameCard[] selectSpellTrapsFromField(int amount) {
        super.selectSpellTrapsFromField(amount);
        GameCard[] cards = new GameCard[amount];
        for (int i = 0; i < amount; i++) {
            GameCard selectedSpell = getGameCard(availableCards);
            cards[i] = selectedSpell;
            availableCards.remove(selectedSpell);
        }
        return cards;
    }

    //Select card from graveyard with level less than mostLevel
    public GameCard selectCardFromGraveyard(int mostLevel) {
        super.selectCardFromGraveyard(mostLevel);
        return getGameCard(availableCards);
    }

    public GameCard selectNormalCardFromHand(int mostLevel) {
        super.selectNormalCardFromHand(mostLevel);
        return getGameCard(availableCards);
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

    @Nullable
    private GameCard getGameCard(List<GameCard> availableMonsters) {
        ArrayBlockingQueue<GameCard> choice = GameController.getUIView().promptChoice(availableMonsters);
        if (choice == null || choice.size() == 0) {
            return null;
        }
        return choice.poll();
    }

}
