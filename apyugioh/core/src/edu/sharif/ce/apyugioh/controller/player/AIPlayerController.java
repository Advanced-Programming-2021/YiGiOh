package edu.sharif.ce.apyugioh.controller.player;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Effects;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.model.card.CardType;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;

public class AIPlayerController extends PlayerController {

    public AIPlayerController(Player player) {
        super(player);
    }

    public void startRoundAction() {
        GameController.getUIView().update();
        getGameController().nextPhaseAI();
        GameController.getUIView().update();
        getGameController().nextPhaseAI();
        GameController.getUIView().update();
        int roundCount = getGameController().getRoundResults().size();
        if (setOrSummon(roundCount)) return;
        GameController.getUIView().update();
        getGameController().nextPhaseAI();
        GameController.getUIView().update();
        if (getGameController().getPassedTurns() > 1) {
            if (attackEachCard(roundCount)) return;
        }
        GameController.getUIView().update();
        if (!isRoundEnded(roundCount)) {
            getGameController().nextPhaseAI();
            GameController.getUIView().update();
            getGameController().nextPhaseAI();
            GameController.getUIView().update();
            getGameController().nextPhase();
            GameController.getUIView().update();
        }

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
                int position = selectLowestAttackMonster(getRivalPlayer().getField().getMonsterZone());
                if (position == -1) {
                    directAttack();
                    GameController.getUIView().update();
                } else {
                    GameCard monsterToGetAttacked = getRivalPlayer().getField().getMonsterZone()[position];
                    if (monsterToGetAttacked.isFaceDown()) {
                        if (getSelectionController().getCard().getCurrentAttack() >= monsterToGetAttacked.getCurrentDefense()) {
                            attack(position + 1);
                            GameController.getUIView().update();
                        }
                    } else {
                        if (getSelectionController().getCard().getCurrentAttack() >= monsterToGetAttacked.getCurrentAttack()) {
                            attack(position + 1);
                            GameController.getUIView().update();
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
        if (getSelectionController() != null) {
            if ((((Monster) getSelectionController().getCard().getCard()).getLevel() <= 4 && !player.getField().isMonsterZoneFull())
                    || (((Monster) getSelectionController().getCard().getCard()).getLevel() > 4)) {
                if (getSelectionController().getCard().getCurrentAttack() > 700 &&
                        getSelectionController().getCard().getCurrentAttack() > getSelectionController().getCard().getCurrentDefense()) {
                    summon();
                } else {
                    set();
                }
            }
            if (isRoundEnded(roundCount)) return true;
        }
        location = selectSpellFromHand();
        if (location != null) select(location);
        if (getSelectionController() != null) {
            if (!player.getField().isSpellZoneFull()) {
                set();
            }
            return isRoundEnded(roundCount);
        }
        return false;
    }

    private CardLocation selectSpellFromHand() {
        CardLocation location = new CardLocation();
        location.setInHand(true);
        GameCard selected = selectRandom(player.getField().getHand().stream().
                filter(e -> !e.getCard().getCardType().equals(CardType.MONSTER)).collect(Collectors.toList()));
        if (selected == null) return null;
        location.setPosition(player.getField().getHand().indexOf(selected));
        return location;
    }

    private boolean isRoundEnded(int roundCount) {
        if (ProgramController.getGameControllerID() == -1) {
            return true;
        }
        return getGameController().getRoundResults().size() != roundCount;
    }

    public void nextPhaseAction() {
        if (!isRoundEnded(getGameController().getRoundResults().size())) {
            nextPhase();
        }
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
        if (monster == null) return null;
        if (((Monster) monster.getCard()).getLevel() > 6 && player.getField().getAvailableMonstersInZoneCount() < 2) {
            monster = player.getField().getHand().stream()
                    .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                    .filter(e -> ((Monster) e.getCard()).getLevel() <= 6)
                    .max(gameCardComparator)
                    .orElse(null);
        }
        if (monster == null) return null;
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
                .filter(GameCard::isFaceDown)
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
            if (getRivalPlayer().getField().getMonsterZone()[i] != null &&
                    getRivalPlayer().getField().getMonsterZone()[i].getId() == selected.getId()) {
                return i;
            }
        }
        for (int i = 0; i < 5; i++) {
            if (player.getField().getMonsterZone()[i] != null &&
                    player.getField().getMonsterZone()[i].getId() == selected.getId()) {
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
            return selectRandom(cards);
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
    }

    //special Cases

    //TributeMonsterForSummon
    @Override
    public GameCard[] tributeMonster(int amount) {
        super.tributeMonster(amount);
        GameCard[] cards = new GameCard[amount];
        for (int i = 0; i < amount; i++) {
            int selection = selectLowestAttackMonster(availableCards.toArray(GameCard[]::new));
            if (selection == -1) {
                return null;
            }
            cards[i] = player.getField().getMonsterZone()[selection];
            availableCards.remove(cards[i]);
        }
        return cards;
    }

    //Scanner
    @Override
    public GameCard scanMonsterForScanner() {
        super.scanMonsterForScanner();
        return getBestMonster(availableCards);
    }

    //Man-Eater Bug
    @Override
    public GameCard directRemove() {
        super.directRemove();
        return getBestMonster(availableCards);
    }

    //TexChanger
    @Override
    public GameCard specialCyberseSummon() {
        super.specialCyberseSummon();
        return getBestMonster(availableCards);
    }

    //HeraldOfCreation
    @Override
    public GameCard summonFromGraveyard() {
        super.summonFromGraveyard();
        return getBestMonster(availableCards);
    }

    //Beast King Barbaros & Tricky
    @Override
    public int chooseHowToSummon(List<String> choices) {
        if (getSelectionController().getCard().getCard().getCardEffects().contains(Effects.BEAST_KING_BARBAROS)) {
            if (player.getField().getAvailableMonstersInZoneCount() < 2) {
                return 1;
            } else if (player.getField().getAvailableMonstersInZoneCount() < 3) {
                return 0;
            } else {
                GameCard currentHighest = getHighestAttackMonster(Arrays.stream(player.getField().getMonsterZone())
                        .filter(Objects::nonNull).collect(Collectors.toList()));
                GameCard rivalHighest = getHighestAttackMonster(Arrays.stream(getRivalPlayer().getField().getMonsterZone())
                        .filter(Objects::nonNull).collect(Collectors.toList()));
                if (rivalHighest == null) {
                    return 0;
                }
                if (currentHighest.getCurrentAttack() < rivalHighest.getCurrentAttack()) {
                    return 2;
                }
                if (player.getField().getAvailableMonstersInZoneCount() > 3) {
                    return 2;
                }
                return 0;
            }
        } else if (getSelectionController().getCard().getCard().getCardEffects().contains(Effects.SPECIAL_SUMMON_BY_REMOVE_CARD_FROM_HAND)) {
            if (player.getField().getAvailableMonstersInZoneCount() == 0) {
                return 1;
            } else {
                GameCard lowestInField = getLowestAttackMonster(Arrays.stream(player.getField().getMonsterZone())
                        .filter(Objects::nonNull).collect(Collectors.toList()));
                GameCard lowestInHand = getLowestAttackMonster(player.getField().getHand());
                if (lowestInField.getCurrentAttack() < lowestInHand.getCurrentAttack()) {
                    return 0;
                }
                return 1;
            }
        }
        return new Random().nextInt(choices.size());
    }

    //terratiger
    @Override
    public GameCard selectMonsterToSummon() {
        super.selectMonsterToSummon();
        return getBestMonster(availableCards);
    }

    //EquipMonster
    @Override
    public GameCard equipMonster() {
        super.equipMonster();
        return getBestMonster(availableCards);
    }

    //Select card from graveyard
    @Override
    public GameCard selectCardFromGraveyard() {
        super.selectCardFromGraveyard();
        return getBestMonster(availableCards);
    }

    //Select card from monster zone
    @Override
    public GameCard selectCardFromMonsterZone() {
        super.selectCardFromMonsterZone();
        return getBestMonster(availableCards);
    }

    //Select card from both graveyards
    @Override
    public GameCard selectCardFromAllGraveyards() {
        super.selectCardFromAllGraveyards();
        return getBestMonster(availableCards);
    }

    //Select card from hand
    @Override
    public GameCard selectCardFromHand(GameCard exceptCard) {
        super.selectCardFromHand(exceptCard);
        return getBestMonster(availableCards);
    }

    //Select card from deck
    @Override
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
        if (availableCards.size() < amount) return null;
        for (int i = 0; i < amount; i++) {
            cards[i] = selectRandom(availableCards);
            if (cards[i] == null) return null;
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
        super.selectRandomCardFromHand();
        return selectRandom(availableCards);
    }

    @Override
    public boolean confirm(String message) {
        return true;
    }

    private GameCard selectRandom(List<GameCard> cards) {
        if (cards != null && !cards.isEmpty()) {
            int random = new Random().nextInt(cards.size());
            return cards.get(random);
        } else {
            return null;
        }
    }

}
