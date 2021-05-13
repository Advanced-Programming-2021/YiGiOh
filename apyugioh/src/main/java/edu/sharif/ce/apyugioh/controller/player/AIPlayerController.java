package edu.sharif.ce.apyugioh.controller.player;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.*;

import java.util.*;
import java.util.stream.Collectors;

public class AIPlayerController extends PlayerController {

    public AIPlayerController(Player player) {
        super(player);
    }

    public void startRoundAction() {
        getGameController().nextPhaseAI();
        getGameController().nextPhaseAI();
        int roundCount = getGameController().getRoundResults().size();
        if (setOrSummon(roundCount)) return;
        getGameController().nextPhaseAI();
        if (getGameController().getPassedTurns() > 1) {
            if (attackEachCard(roundCount)) return;
        }
        getGameController().nextPhaseAI();
        getGameController().nextPhaseAI();
        getGameController().nextPhase();
        getGameController().showCurrentPlayerBoard();
    }

    private boolean attackEachCard(int roundCount) {
        List<GameCard> toAttackMonsters = Arrays.stream(player.getField().getMonsterZone())
                .filter(Objects::nonNull)
                .filter(e -> !e.isFaceDown())
                .collect(Collectors.toList());
        while (toAttackMonsters.size() > 0) {
            CardLocation location = selectHighestAttackMonsterFromMonsterZone(toAttackMonsters);
            if (location != null) {
                select(location);
                toAttackMonsters.remove(getSelectionController().getCard());
                int position = selectLowestAttackMonster(getGameController().getRivalPlayer().getField().getMonsterZone());
                if (position == -1) {
                    directAttack();
                } else {
                    GameCard monsterToGetAttacked = getGameController().getRivalPlayer().getField().getMonsterZone()[position];
                    if (monsterToGetAttacked.isFaceDown()) {
                        if (getSelectionController().getCard().getCurrentAttack() >= monsterToGetAttacked.getCurrentDefense()) {
                            attack(position + 1);
                        }
                    } else {
                        if (getSelectionController().getCard().getCurrentAttack() >= monsterToGetAttacked.getCurrentAttack()) {
                            attack(position + 1);
                        }
                    }
                }
                if (isRoundEnded(roundCount)) return true;
            } else {
                break;
            }
        }
        return false;
    }

    private boolean setOrSummon(int roundCount) {
        CardLocation location = selectMonsterFromHand();
        if (location != null) select(location);
        if (getSelectionController() != null && !player.getField().isMonsterZoneFull()) {
            if (getSelectionController().getCard().getCurrentAttack() > 700 &&
                    getSelectionController().getCard().getCurrentAttack() > getSelectionController().getCard().getCurrentDefense()) {
                summon();
            } else {
                set();
            }
            if (isRoundEnded(roundCount)) return true;
        }
        return false;
    }

    private boolean isRoundEnded(int roundCount) {
        if (ProgramController.getGameControllerID() == -1) {
            return true;
        }
        if (getGameController().getRoundResults().size() != roundCount) {
            return true;
        }
        return false;
    }

    public void nextPhaseAction() {
        nextPhase();
    }

    private CardLocation selectMonsterFromHand() {
        CardLocation location = new CardLocation();
        location.setInHand(true);
        GameCard attackMonster = getHighestAttackMonster(player.getField().getHand());
        attackMonster = checkForTributePossibility(attackMonster, Comparator.comparingInt(GameCard::getCurrentAttack));
        GameCard defenseMonster = getHighestDefenseMonster(player.getField().getHand());
        defenseMonster = checkForTributePossibility(defenseMonster, Comparator.comparingInt(GameCard::getCurrentDefense));
        GameCard selected;
        if (attackMonster == null && defenseMonster == null) {
            return null;
        } else if (attackMonster == null) {
            selected = defenseMonster;
        } else if (defenseMonster == null) {
            selected = attackMonster;
        } else {
            if (attackMonster.getCurrentAttack() > defenseMonster.getCurrentDefense()) {
                selected = attackMonster;
            } else {
                selected = defenseMonster;
            }
        }
        location.setPosition(player.getField().getHand().indexOf(selected));
        return location;
    }

    private GameCard checkForTributePossibility(GameCard monster, Comparator<GameCard> gameCardComparator) {
        if (((Monster) monster.getCard()).getLevel() > 6 && player.getField().getAvailableMonstersInZoneCount() < 2) {
            monster = player.getField().getHand().stream()
                    .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                    .filter(e -> ((Monster) e.getCard()).getLevel() <= 6)
                    .max(gameCardComparator)
                    .orElse(null);
        }
        if (((Monster) monster.getCard()).getLevel() > 4 && player.getField().getAvailableMonstersInZoneCount() < 1) {
            monster = player.getField().getHand().stream()
                    .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                    .filter(e -> ((Monster) e.getCard()).getLevel() <= 4)
                    .max(gameCardComparator)
                    .orElse(null);
        }
        return monster;
    }


    private CardLocation selectHighestAttackMonsterFromMonsterZone(List<GameCard> monsters) {
        CardLocation location = new CardLocation();
        location.setFromMonsterZone(true);
        GameCard monster = getHighestAttackMonster(monsters.stream()
                .filter(Objects::nonNull)
                .filter(e -> !e.isFaceDown())
                .collect(Collectors.toList()));
        if (monster == null) {
            return null;
        }
        for (int i = 0; i < 5; i++) {
            if (player.getField().getMonsterZone()[i] != null && player.getField().getMonsterZone()[i].getId() == monster.getId()) {
                location.setPosition(i);
                return location;
            }
        }
        return null;
    }

    private int selectLowestAttackMonster(GameCard[] cards) {
        GameCard attackMonster = getLowestAttackMonster(Arrays.stream(cards)
                .filter(Objects::nonNull)
                .filter(e -> !e.isFaceDown())
                .collect(Collectors.toList()));
        GameCard defenseMonster = getLowestDefenseMonster(Arrays.stream(cards)
                .filter(Objects::nonNull)
                .filter(e -> e.isFaceDown())
                .collect(Collectors.toList()));
        GameCard selected;
        if (attackMonster == null && defenseMonster == null) {
            return -1;
        } else if (attackMonster == null) {
            selected = defenseMonster;
        } else if (defenseMonster == null) {
            selected = attackMonster;
        } else {
            if (attackMonster.getCurrentAttack() < 700) {
                selected = attackMonster;
            } else if (attackMonster.getCurrentAttack() < defenseMonster.getCurrentDefense()) {
                selected = attackMonster;
            } else {
                selected = defenseMonster;
            }
        }
        for (int i = 0; i < 5; i++) {
            if (getGameController().getRivalPlayer().getField().getMonsterZone()[i] != null &&
                    getGameController().getRivalPlayer().getField().getMonsterZone()[i].getId() == selected.getId()) {
                return i;
            }
        }
        return -1;
    }

    private GameCard getHighestAttackMonster(List<GameCard> cards) {
        return cards.stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .max(Comparator.comparingInt(GameCard::getCurrentAttack))
                .orElse(null);
    }

    private GameCard getHighestDefenseMonster(List<GameCard> cards) {
        return cards.stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .max(Comparator.comparingInt(GameCard::getCurrentDefense))
                .orElse(null);
    }

    private GameCard getLowestAttackMonster(List<GameCard> cards) {
        return cards.stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .min(Comparator.comparingInt(GameCard::getCurrentAttack))
                .orElse(null);
    }

    private GameCard getLowestDefenseMonster(List<GameCard> cards) {
        return cards.stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .min(Comparator.comparingInt(GameCard::getCurrentDefense))
                .orElse(null);
    }

    private GameCard getBestMonster(List<GameCard> cards) {
        GameCard attackMonster = getHighestAttackMonster(cards.stream()
                .filter(Objects::nonNull)
                .filter(e -> !e.isFaceDown())
                .collect(Collectors.toList()));
        GameCard defenseMonster = getHighestDefenseMonster(cards.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        if (attackMonster == null && defenseMonster == null) {
            if (cards.size() > 0) {
                return selectRandom(cards);
            }
        } else if (attackMonster == null) {
            return defenseMonster;
        } else if (defenseMonster == null) {
            return attackMonster;
        } else {
            if (attackMonster.getCurrentAttack() > 1500) {
                return attackMonster;
            } else if (attackMonster.getCurrentAttack() > defenseMonster.getCurrentDefense()) {
                return attackMonster;
            } else {
                return defenseMonster;
            }
        }
        return null;
    }

    //special Cases

    //TributeMonsterForSummon
    public GameCard[] tributeMonster(int amount) {
        super.tributeMonster(amount);
        GameCard[] cards = new GameCard[amount];
        for (int i = 0; i < amount; i++) {
            int selection = selectLowestAttackMonster(availableCards.toArray(new GameCard[0]));
            if (selection == -1) {
                return null;
            }
            cards[i] = availableCards.get(selection);
            availableCards.remove(cards[i]);
        }
        return cards;
    }

    //Scanner
    public GameCard scanMonsterForScanner() {
        super.scanMonsterForScanner();
        return getBestMonster(availableCards);
    }

    //Man-Eater Bug
    public GameCard directRemove() {
        super.directRemove();
        return getBestMonster(availableCards);
    }

    //TexChanger
    public GameCard specialCyberseSummon() {
        super.specialCyberseSummon();
        return getBestMonster(availableCards);
    }

    //HeraldOfCreation
    public GameCard summonFromGraveyard() {
        super.summonFromGraveyard();
        return getBestMonster(availableCards);
    }

    //Beast King Barbaros & Tricky
    public int chooseHowToSummon(List<String> choices) {
        return new Random().nextInt(choices.size());
    }

    //terratiger
    public GameCard selectMonsterToSummon() {
        super.selectMonsterToSummon();
        return getBestMonster(availableCards);
    }

    //EquipMonster
    public GameCard equipMonster() {
        super.equipMonster();
        return getBestMonster(availableCards);
    }

    //Select card from graveyard
    public GameCard selectCardFromGraveyard() {
        super.selectCardFromGraveyard();
        return getBestMonster(availableCards);
    }

    //Select card from monster zone
    public GameCard selectCardFromMonsterZone() {
        super.selectCardFromMonsterZone();
        return getBestMonster(availableCards);
    }

    //Select card from both graveyards
    public GameCard selectCardFromAllGraveyards() {
        super.selectCardFromAllGraveyards();
        return getBestMonster(availableCards);
    }

    //Select card from hand
    public GameCard selectCardFromHand() {
        super.selectCardFromHand();
        return getBestMonster(availableCards);
    }

    //Select card from deck
    public GameCard selectCardFromDeck() {
        super.selectCardFromDeck();
        return getBestMonster(availableCards);
    }

    @Override
    public GameCard selectFieldSpellFromDeck() {
        super.selectFieldSpellFromDeck();
        return getBestMonster(availableCards);
    }

    @Override
    public GameCard selectRivalMonster() {
        super.selectRivalMonster();
        return getBestMonster(availableCards);
    }

    @Override
    public GameCard[] selectSpellTrapsFromField(int amount) {
        super.selectSpellTrapsFromField(amount);
        GameCard[] cards = new GameCard[amount];
        for (int i = 0; i < amount; i++) {
            cards[i] = selectRandom(availableCards);
            availableCards.remove(cards[i]);
        }
        return cards;
    }

    @Override
    public GameCard selectCardFromGraveyard(int mostLevel) {
        super.selectCardFromGraveyard(mostLevel);
        return getBestMonster(availableCards);
    }

    @Override
    public GameCard selectNormalCardFromHand(int mostLevel) {
        super.selectNormalCardFromHand(mostLevel);
        return getBestMonster(availableCards);
    }

    @Override
    public Card getACard() {
        return DatabaseManager.getCards().getAllCards()
                .get(new Random().nextInt(DatabaseManager.getCards().getAllCards().size()));
    }

    public GameCard selectRandomCardFromHand() {
        return null;
    }

    @Override
    public boolean confirm(String message) {
        return true;
    }

    private GameCard selectRandom(List<GameCard> cards) {
        return cards.get(new Random().nextInt(cards.size()));
    }

}
