package edu.sharif.ce.apyugioh.controller.player;

import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.*;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

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
    public GameCard selectCardFromAllGraveyards() {
        super.selectCardFromAllGraveyards();
        return getGameCard(availableCards);
    }

    //Select card from hand
    public GameCard selectCardFromHand() {
        super.selectCardFromHand();
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

    public GameCard selectRandomCardFromHand(){
        return null;
    }

    @Override
    public boolean confirm(String message) {
        return GameController.getView().confirm(message);
    }

    @Nullable
    private GameCard getGameCard(List<GameCard> availableMonsters) {
        String result = GameController.getView().promptChoice(availableMonsters.stream()
                .map(e -> e.getId() + " " + e.getCard().getName()).collect(Collectors.toList()).toArray(String[]::new));
        if (result == null) return null;
        return availableMonsters.stream()
                .filter(e -> e.getId() == Integer.parseInt(result.split(" ")[0]))
                .findFirst().orElse(null);
    }

}
