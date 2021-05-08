package edu.sharif.ce.apyugioh.controller.player;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
import edu.sharif.ce.apyugioh.model.card.MonsterType;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NormalPlayerController extends PlayerController {

    public NormalPlayerController(Player player) {
        super(player);
    }

    //special Cases

    //Scanner
    public GameCard scanMonsterForScanner() {
        List<GameCard> availableMonsters = getGameController().getRivalPlayer().getField().getGraveyard();
        return getGameCard(availableMonsters);
    }

    //TributeMonsterForSummon
    public GameCard[] tributeMonster(int amount) {
        GameCard[] cards = new GameCard[amount];
        List<GameCard> availableMonsters = Arrays.stream(player.getField().getMonsterZone()).filter(Objects::nonNull).collect(Collectors.toList());
        for (int i = 0; i < amount; i++) {
            GameCard selectedMonster = getGameCard(availableMonsters);
            cards[i] = selectedMonster;
            availableMonsters.remove(selectedMonster);
        }
        return cards;
    }

    //Man-Eater Bug
    public GameCard directRemove() {
        List<GameCard> availableMonsters = Arrays.stream(getGameController().getRivalPlayer().getField().getMonsterZone()).collect(Collectors.toList());
        return getGameCard(availableMonsters);
    }

    //TexChanger
    public GameCard specialCyberseSummon() {
        List<GameCard> availableMonsters = getGameController().getCurrentPlayer().getField().getGraveyard();
        availableMonsters.addAll(getGameController().getCurrentPlayer().getField().getHand());
        availableMonsters.addAll(getGameController().getCurrentPlayer().getField().getDeck());
        availableMonsters = availableMonsters.stream().filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .filter(e -> ((Monster) e.getCard()).getType().equals(MonsterType.CYBERSE)).collect(Collectors.toList());
        return getGameCard(availableMonsters);
    }

    //HeraldOfCreation
    public GameCard summonFromGraveyard() {
        List<GameCard> availableMonsters = getGameController().getCurrentPlayer().getField().getGraveyard().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .filter(e -> ((Monster) e.getCard()).getLevel() >= 7).collect(Collectors.toList());
        return getGameCard(availableMonsters);
    }

    //Beast King Barbaros & Tricky
    public int chooseHowToSummon() {
        return 0;
    }

    //terratiger
    public GameCard selectMonsterToSummon() {
        List<GameCard> availableMonsters = getGameController().getCurrentPlayer().getField().getHand().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .filter(e -> ((Monster) e.getCard()).getLevel() <= 4).collect(Collectors.toList());
        return getGameCard(availableMonsters);
    }

    //EquipMonster
    public GameCard equipMonster() {
        List<GameCard> availableMonsters = Arrays.stream(player.getField().getMonsterZone()).filter(Objects::nonNull).collect(Collectors.toList());
        return getGameCard(availableMonsters);
    }

    //Select card from graveyard
    public GameCard selectCardFromGraveyard() {
        List<GameCard> availableMonsters = getGameController().getCurrentPlayer().getField().getGraveyard();
        return getGameCard(availableMonsters);
    }

    //Select card from monster zone
    public GameCard selectCardFromMonsterZone() {
        List<GameCard> availableMonsters = Arrays.stream(player.getField().getMonsterZone()).filter(Objects::nonNull).collect(Collectors.toList());
        return getGameCard(availableMonsters);
    }

    //Select card from both graveyards
    public GameCard selectCardFromAllGraveyards() {
        List<GameCard> availableMonsters = getGameController().getCurrentPlayer().getField().getGraveyard();
        availableMonsters.addAll(getGameController().getRivalPlayer().getField().getGraveyard());
        return getGameCard(availableMonsters);
    }

    //Select card from hand
    public GameCard selectCardFromHand() {
        List<GameCard> availableMonsters = getGameController().getCurrentPlayer().getField().getHand();
        return getGameCard(availableMonsters);
    }

    //Select card from deck
    public GameCard selectCardFromDeck() {
        List<GameCard> availableMonsters = getGameController().getCurrentPlayer().getField().getDeck();
        return getGameCard(availableMonsters);
    }

    @Override
    public boolean confirm(String message) {
        return GameController.getView().confirm(message);
    }

    @Nullable
    private GameCard getGameCard(List<GameCard> availableMonsters) {
        String result = GameController.getView().promptChoice(availableMonsters.stream()
                .map(e -> e.getCard().getName()).collect(Collectors.toList()).toArray(String[]::new));
        if (result == null) return null;
        GameCard selectedMonster = availableMonsters.stream().filter(e -> e.getCard().getName().equals(result)).findFirst().orElse(null);
        return selectedMonster;
    }

}
