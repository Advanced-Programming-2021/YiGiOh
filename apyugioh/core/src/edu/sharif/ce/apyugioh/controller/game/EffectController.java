package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.player.PlayerController;
import edu.sharif.ce.apyugioh.model.*;
import edu.sharif.ce.apyugioh.model.card.*;
import edu.sharif.ce.apyugioh.view.GameView;
import edu.sharif.ce.apyugioh.view.View;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class EffectController {
    @Setter
    private List<GameCard> cardsAffected;
    private GameCard effectCard;
    private int remainsTurn;
    private boolean isUsedThisTurn;
    private boolean isUsedBefore;
    private int gameControllerID;

    public EffectController(int gameControllerID, GameCard effectCard) {
        this.gameControllerID = gameControllerID;
        cardsAffected = new ArrayList<>();
        isUsedThisTurn = false;
        isUsedBefore = false;
        this.effectCard = effectCard;
    }

    public EffectController(int gameControllerID, GameCard effectCard, int remainsTurn) {
        this.gameControllerID = gameControllerID;
        cardsAffected = new ArrayList<>();
        isUsedThisTurn = false;
        isUsedBefore = false;
        this.effectCard = effectCard;
        this.remainsTurn = remainsTurn;
    }

    public void specialSummonFromGraveyard() {
        GameCard cardFromGraveyard = Objects.requireNonNull(getPlayerControllerByCard(effectCard)).selectMonsterFromAllGraveyards();
        if (cardFromGraveyard == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        } else if (!(getCurrentPlayerField().isInGraveyard(cardFromGraveyard) ||
                getRivalPlayerField().isInGraveyard(cardFromGraveyard))) {
            getGameControllerView().showError(GameView.ERROR_NOT_FROM_PLACE, "graveyard");
        } else {
            new SummonController(gameControllerID, cardFromGraveyard, Objects.requireNonNull(getPlayerControllerByCard(effectCard)).getPlayer())
                    .specialSummon();
        }
    }

    public void addFieldSpellToHand() {
        //change view to get just field spell from deck
        GameCard cardFromDeck = getCurrentPlayerController().selectFieldSpellFromDeck();
        if (cardFromDeck == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        } else if (!getCurrentPlayerField().isInDeck(cardFromDeck)) {
            getGameControllerView().showError(GameView.ERROR_NOT_FROM_PLACE, "your deck");
        } else {
            getCurrentPlayerField().removeFromDeck(cardFromDeck);
            getCurrentPlayerField().putInHand(cardFromDeck);
        }
    }

    public void drawCard(int amount) {
        if (amount + getCurrentPlayerField().getHand().size() == 8) {
            GameCard cardForRemove = selectCardToRemoveFromHand();
            getCurrentPlayerField().removeFromHand(cardForRemove);
            getCurrentPlayerField().putInGraveyard(cardForRemove);
        } else if (amount + getCurrentPlayerField().getHand().size() == 9) {
            for (int i = 0; i < 2; i++) {
                GameCard cardForRemove = selectCardToRemoveFromHand();
                getCurrentPlayerField().removeFromHand(cardForRemove);
                getCurrentPlayerField().putInGraveyard(cardForRemove);
            }
        }
        if (amount + getCurrentPlayerField().getHand().size() > 7) {
            getGameControllerView().showError(GameView.ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE);
            return;
        }
        for (int i = 0; i < amount; i++) {
            getCurrentPlayerField().drawCard();
        }
    }

    public void controlRivalMonster() {
        GameCard rivalMonster = getCurrentPlayerController().selectRivalMonster();
        if (rivalMonster == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        } else if (!getRivalPlayerField().isInMonsterZone(rivalMonster)) {
            getGameControllerView().showError(GameView.ERROR_NOT_FROM_PLACE, "rival monster zone");
        } else if (getCurrentPlayerField().isMonsterZoneFull()) {
            getGameControllerView().showError(GameView.ERROR_MONSTER_ZONE_FULL);
        } else {
            cardsAffected.add(rivalMonster);
            getRivalPlayerField().removeFromMonsterZone(rivalMonster);
            getCurrentPlayerField().putInMonsterZone(rivalMonster);
        }
    }

    public void flipAllRivalFaceDownMonsters() {
        for (GameCard rivalMonster : getRivalPlayerField().getMonsterZone()) {
            if (rivalMonster != null) {
                new SummonController(gameControllerID, rivalMonster).flipSummon();
            }
        }
    }

    public void destroyCurrentPlayerMonsters() {
        for (int monsterIndex = 0; monsterIndex < 5; monsterIndex++) {
            GameCard monsterForRemove = getCurrentPlayerField().getMonsterZone()[monsterIndex];
            if (monsterForRemove == null) continue;
            getGameController().removeMonsterCard(monsterForRemove);
        }
    }

    public void destroyRivalMonsters() {
        int monsterIndex = 0;
        for (; monsterIndex < 5; monsterIndex++) {
            GameCard monsterForRemove = getRivalPlayerField().getMonsterZone()[monsterIndex];
            if (monsterForRemove == null) continue;
            getGameController().removeMonsterCard(monsterForRemove);
        }
    }

    public void twinTwisters() {
        GameCard cardToRemoveFromHand = getCurrentPlayerController().selectCardFromHand(null);
        if (cardToRemoveFromHand == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        } else if (!getCurrentPlayerField().isInHand(cardToRemoveFromHand)) {
            getGameControllerView().showError(GameView.ERROR_NOT_FROM_PLACE, "your hand");
        } else {
            getCurrentPlayerField().removeFromHand(cardToRemoveFromHand);
            getCurrentPlayerField().putInGraveyard(cardToRemoveFromHand);
            GameCard[] spellTraps = getCurrentPlayerController().selectSpellTrapsFromField(2);
            if (spellTraps == null) return;
            for (GameCard spellTrap : spellTraps) {
                getGameController().removeSpellTrapCard(spellTrap);
            }
        }
    }

    public void destroySpellTrap() {
        GameCard[] spellTraps = getCurrentPlayerController().selectSpellTrapsFromField(1);
        if (spellTraps == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        } else {
            GameCard spellTrap = spellTraps[0];
            if (spellTrap == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                if (getCurrentPlayerField().isInSpellZone(spellTrap)) {
                    getGameController().removeSpellTrapCard(spellTrap);
                } else {
                    getGameController().removeSpellTrapCard(spellTrap);
                }
            }
        }
    }

    public void messengerOfPeace() {
        boolean confirm = getCurrentPlayerController().confirm("do you want to pay 100 LP for Messenger of peace?!");
        if (confirm) {
            decreaseLP(100);
        } else {
            getGameController().removeSpellTrapCard(effectCard);
        }
    }

    public boolean isAttackerMonsterPowerful(int maxAttackPower) {
        return getGameController().getAttackController().getAttackingMonster().getCurrentAttack() > maxAttackPower;
    }

    public void yami() {
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (monster == null) continue;
            if (((Monster) monster.getCard()).getType().equals(MonsterType.FIEND)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.SPELLCASTER)) {
                monster.addAttackModifier(200, effectCard, false);
                monster.addDefenceModifier(200, effectCard, false);
            } else if (((Monster) monster.getCard()).getType().equals(MonsterType.FAIRY)) {
                monster.addAttackModifier(-200, effectCard, false);
                monster.addDefenceModifier(-200, effectCard, false);
            }
        }
        for (GameCard monster : getRivalPlayerField().getMonsterZone()) {
            if (monster == null) continue;
            if (((Monster) monster.getCard()).getType().equals(MonsterType.FIEND)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.SPELLCASTER)) {
                monster.addAttackModifier(200, effectCard, false);
                monster.addDefenceModifier(200, effectCard, false);
            } else if (((Monster) monster.getCard()).getType().equals(MonsterType.FAIRY)) {
                monster.addAttackModifier(-200, effectCard, false);
                monster.addDefenceModifier(-200, effectCard, false);
            }
        }
    }

    public void forest() {
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (monster == null) continue;
            if (((Monster) monster.getCard()).getType().equals(MonsterType.INSECT)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.BEAST)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.BEAST_WARRIOR)) {
                monster.addAttackModifier(200, effectCard, false);
                monster.addDefenceModifier(200, effectCard, false);
            }
        }
        for (GameCard monster : getRivalPlayerField().getMonsterZone()) {
            if (monster == null) continue;
            if (((Monster) monster.getCard()).getType().equals(MonsterType.INSECT)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.BEAST)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.BEAST_WARRIOR)) {
                monster.addAttackModifier(200, effectCard, false);
                monster.addDefenceModifier(200, effectCard, false);
            }
        }
    }

    public void closedForest() {
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (monster == null) continue;
            if (((Monster) monster.getCard()).getType().equals(MonsterType.BEAST)) {
                int amount = getCurrentPlayerField().getGraveyard().size();
                monster.addAttackModifier(amount, effectCard, false);
                monster.addDefenceModifier(amount, effectCard, false);
            }
        }
    }

    public void umiiruka() {
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (monster == null) continue;
            if (((Monster) monster.getCard()).getType().equals(MonsterType.AQUA)) {
                monster.addAttackModifier(500, effectCard, false);
                monster.addDefenceModifier(-400, effectCard, false);
            }
        }
    }

    public boolean isThereAnotherMonster() {
        return Arrays.stream(getRivalPlayerField().getMonsterZone()).filter(Objects::nonNull).count() > 1;
    }

    public void setZeroAttackForAttackerCard() {
        if (!isUsedBefore) {
            boolean userResponse = getRivalPlayerController().confirm("do you want to active set zero attack for attacker card?!");
            if (userResponse) {
                getGameController().getAttackController().getAttackingMonster().addAttackModifier(-1000000, effectCard, true);
                cardsAffected.add(getGameController().getAttackController().getAttackingMonster());
                setUsed();
            }
        }
    }

    public void equipMonster() {
        GameCard equipMonster = getCurrentPlayerController().equipMonster();
        if (equipMonster == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().isInMonsterZone(equipMonster)) {
            getGameControllerView().showError(GameView.ERROR_NOT_FROM_PLACE, "your monster zone");
        } else {
            cardsAffected.add(equipMonster);
        }
    }

    public void swordOfDarkDestruction() {
        equipMonster();
        for (GameCard equippedCard : cardsAffected) {
            if ((((Monster) equippedCard.getCard()).getType().equals(MonsterType.FIEND))
                    || (((Monster) equippedCard.getCard()).getType().equals(MonsterType.SPELLCASTER))) {
                equippedCard.addAttackModifier(400, effectCard, false);
                equippedCard.addDefenceModifier(-200, effectCard, false);
            }
        }
    }

    public void blackPendant() {
        equipMonster();
        for (GameCard equippedCard : cardsAffected) {
            equippedCard.addAttackModifier(500, effectCard, false);
        }
    }

    public void unitedWeStand() {
        equipMonster();
        int numberOfFaceUpMonsters = 0;
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (monster == null) continue;
            if (!monster.isFaceDown()) numberOfFaceUpMonsters++;
        }
        for (GameCard equippedCard : cardsAffected) {
            equippedCard.addAttackModifier(800 * numberOfFaceUpMonsters, effectCard, false);
        }
    }

    public void magnumShield() {
        equipMonster();
        for (GameCard equippedCard : cardsAffected) {
            if (((Monster) equippedCard.getCard()).getType().equals(MonsterType.WARRIOR)) {
                if (equippedCard.isFaceDown()) {
                    equippedCard.addDefenceModifier(equippedCard.getCurrentAttack(), effectCard, false);
                } else {
                    equippedCard.addAttackModifier(equippedCard.getCurrentDefense(), effectCard, false);
                }
            }
        }
    }

    private void ritualSummon() {
        GameCard monsterToBeRitualSummoned = getCurrentPlayerController().selectRitualMonsterFromHand();
        if (monsterToBeRitualSummoned == null || ((Monster) monsterToBeRitualSummoned.getCard()).getSummon() != MonsterSummon.RITUAL)
            return;
        new SummonController(gameControllerID, monsterToBeRitualSummoned).ritualSummon();
    }

    public void destroyAllRivalCards() {
        destroyRivalMonsters();
        destroyRivalSpellTraps();
        destroyRivalFieldZone();
    }

    public void destroyAttackerCardIfDestroyed() {
        if (getRivalPlayerField().isInGraveyard(effectCard)) {
            GameCard cardToDestroy = getGameController().getAttackController().getAttackingMonster();
            getGameController().removeMonsterCard(cardToDestroy);
            getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, effectCard.getCard().getName());
        }
    }

    public void destroyAnotherCardInBattleIfDestroyed() {
        if (getGameController().getAttackController().getAttackedMonster().equals(effectCard)) {
            if (getRivalPlayerField().isInGraveyard(effectCard)) {
                getGameController().removeMonsterCard(getGameController().getAttackController().getAttackingMonster());
            } else if (getCurrentPlayerField().isInGraveyard(effectCard)) {
                getGameController().removeMonsterCard(getGameController().getAttackController().getAttackingMonster());
            }
        } else if (getGameController().getAttackController().getAttackingMonster().equals(effectCard)) {
            if (getRivalPlayerField().isInGraveyard(effectCard)) {
                getGameController().removeMonsterCard(getGameController().getAttackController().getAttackedMonster());
            } else if (getCurrentPlayerField().isInGraveyard(effectCard)) {
                getGameController().removeMonsterCard(getGameController().getAttackController().getAttackedMonster());
            }
        }
        getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, effectCard.getCard().getName());
    }

    //Texchanger
    public void summonNormalCyberseMonster() {
        boolean confirm = getRivalPlayerController().confirm("do you want summon a normal Cyberse monster?!");
        if (confirm) {
            GameCard monsterToSummon = getRivalPlayerController().specialCyberseSummon();
            if (monsterToSummon == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else if (monsterToSummon.getCard().getCardEffects().size() != 0 ||
                    !((Monster) monsterToSummon.getCard()).getType().equals(MonsterType.CYBERSE)) {
                getGameControllerView().showError(GameView.ERROR_WRONG_CARD_TYPE, "normal Cyberse monster");
            } else {
                if (!effectCard.isRevealed()) effectCard.setRevealed(true);
                new SummonController(gameControllerID, monsterToSummon).specialSummon();
            }
        }
    }

    public void drawCardFromGraveyard(int minLevel) {
        boolean confirm = getCurrentPlayerController()
                .confirm("do you want to draw card with level greater than 7 by remove a card from your hand?!");
        if (confirm) {
            GameCard cardToRemove;
            if ((cardToRemove = selectCardToRemoveFromHand()) == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
                return;
            }
            Player cardPlayer = getGameController().getPlayerByCard(cardToRemove);
            GameCard drawnCard = getCurrentPlayerController().selectCardFromGraveyard(minLevel);
            if (drawnCard == null) return;
            if (((Monster) drawnCard.getCard()).getLevel() < 7) {
                getGameControllerView().showError(GameView.ERROR_WRONG_CARD_TYPE, "card with level greater than 7");
            } else {
                cardPlayer.getField().removeFromHand(cardToRemove);
                cardPlayer.getField().putInGraveyard(cardToRemove);
                getCurrentPlayerField().removeFromGraveyard(drawnCard);
                getCurrentPlayerField().putInHand(drawnCard);
            }
        }
    }

    public void destroyRivalFieldZone() {
        GameCard fieldSpell = getRivalPlayerField().getFieldZone();
        if (fieldSpell != null) {
            getGameController().removeSpellTrapCard(fieldSpell);
        }
    }

    public void destroyRivalSpellTraps() {
        int spellTrapIndex = 0;
        for (; spellTrapIndex < 5; spellTrapIndex++) {
            GameCard spellTrap = getRivalPlayerField().getSpellZone()[spellTrapIndex];
            if (spellTrap == null) continue;
            getGameController().removeSpellTrapCard(spellTrap);
        }
    }

    public void decreaseAttackerLPIfAttackedCardFaceDown(int amount) {
        if (getGameController().getAttackController().getAttackedMonster().isFaceDown()) {
            decreaseAttackerLP(amount);
            getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, effectCard.getCard().getName());
        }
    }

    public void decreaseAttackerLP(int amount) {
        getCurrentPlayer().decreaseLifePoints(amount);
    }

    public void destroyOneOfRivalMonsters() {
        PlayerController cardPlayer = getPlayerControllerByCard(effectCard);
        boolean confirm = Objects.requireNonNull(cardPlayer)
                .confirm("do you want to active " + effectCard.getCard().getName());
        if (confirm) {
            GameCard destructibleCard = Objects.requireNonNull(cardPlayer).selectRivalMonster();
            if (destructibleCard == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else if (!cardPlayer.getRivalPlayer().getField().isInMonsterZone(destructibleCard)) {
                getGameControllerView().showError(GameView.ERROR_NOT_FROM_PLACE, "rival monster zone");
            } else {
                getGameController().removeMonsterCard(destructibleCard);
            }
        }
    }

    public void scanDestroyedRivalMonster() {
        GameCard monsterToScan = getCurrentPlayerController().scanMonsterForScanner();
        if (monsterToScan == null) return;
        effectCard.resetAttackModifier();
        effectCard.resetDefenseModifier();
        effectCard.setEffects(monsterToScan.getCard().getCardEffects());

        effectCard.addAttackModifier(monsterToScan.getCurrentAttack(), true);
        effectCard.addDefenceModifier(monsterToScan.getCurrentDefense(), true);
    }

    public void specialSetFromHand() {
        boolean confirm = getCurrentPlayerController().confirm("do you want to special set a normal card from your hand?!");
        if (confirm) {
            GameCard monsterToSet = getCurrentPlayerController().selectNormalCardFromHand(4);
            if (monsterToSet == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                if (!new SetController(gameControllerID, monsterToSet).specialSet()) {
                    getGameControllerView().showError(GameView.ERROR_EFFECT_ACTIVATING_FAILED, effectCard.getCard().getName());
                }
            }
        }
    }

    public void disposableEffect() {
        getGameTurnController().getDisposableUsedEffects().add(this);
    }

    public void selectFaceUpMonsters() {
        GameCard[] monsterZone = getCurrentPlayerField().getMonsterZone();
        for (GameCard monster : monsterZone) {
            if (monster == null) continue;
            if (!monster.isFaceDown()) cardsAffected.add(monster);
        }
    }

    public void lpsCantChange() {
        getGameController().getAttackController().setLpsCanBeChanged(false);
        Utils.printInfo("lps doesn't change because of " + effectCard.getCard().getName() + " effect");
    }

    public void addAttackToAllMonsters(int amount) {
        if (isUsedBefore) return;
        GameCard[] monsterZone = getCurrentPlayerField().getMonsterZone();
        for (GameCard monster : monsterZone) {
            if (monster != null && !cardsAffected.contains(monster)) cardsAffected.add(monster);
        }
        monsterZone = getRivalPlayerField().getMonsterZone();
        for (GameCard monster : monsterZone) {
            if (monster != null && !cardsAffected.contains(monster)) cardsAffected.add(monster);
        }
        changeAttack(amount);
        setUsed();
    }

    public void changeAttack(int amount) {
        for (GameCard gameCard : cardsAffected) {
            gameCard.addAttackModifier(amount, effectCard, false);
        }
    }
    
    public void combineLevelsOfFaceUpCards() {
        int result = 0;
        ArrayList<Modifier> newModifiers = new ArrayList<>();
        GameCard[] monsterZone = getGameController().getPlayerByCard(effectCard).getField().getMonsterZone();
        for (GameCard gameCard : monsterZone) {
            if (gameCard == null) continue;
            if (!gameCard.getCard().getCardType().equals(CardType.MONSTER)) continue;
            if (!gameCard.isFaceDown()) {
                Monster monster = (Monster) gameCard.getCard();
                result += monster.getLevel() * 300;
            }
        }
        newModifiers.add(new Modifier(result, false));
        for (Modifier modifier : effectCard.getAttackModifier()) {
            if (modifier.isFromEffect()) newModifiers.add(modifier);
        }
        effectCard.setAttackModifier(newModifiers);
    }

    public GameCard selectCardToRemoveFromHand() {
        GameCard cardForRemove;
        cardForRemove = getCurrentPlayerController().selectCardFromHand(null);
        if (cardForRemove == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        }
        return cardForRemove;
    }

    public void magicCylinder() {
        decreaseAttackerLP(getGameController().getAttackController().getAttackingMonster().getCurrentAttack());
        getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, effectCard.getCard().getName());
        getGameController().removeSpellTrapCard(getEffectCard());
    }

    public void destroyAllRivalFaceUpMonsters() {
        List<GameCard> rivalFaceUpMonsters = Arrays.stream(getAnotherPlayerControllerByCard(effectCard).getPlayer().getField().getMonsterZone())
                .filter(Objects::nonNull).collect(Collectors.toList());
        for (GameCard rivalFaceUpMonster : rivalFaceUpMonsters) {
            if (rivalFaceUpMonster == null) continue;
            getGameController().removeMonsterCard(rivalFaceUpMonster);
        }
    }

    public void mindCrush() {
        Card userCard = Objects.requireNonNull(getPlayerControllerByCard(effectCard)).getACard();
        if (userCard == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            return;
        }
        List<GameCard> userCardsInHand = getPlayerByCard(effectCard).getField().getHand().stream()
                .filter(e -> e.getCard().getName().equals(userCard.getName())).collect(Collectors.toList());
        if (userCardsInHand.size() == 0) {
            Utils.printError(userCard.getName() + " card is not in your rival's hand!");
            GameCard randomCard = Objects.requireNonNull(getPlayerControllerByCard(effectCard)).selectRandomCardFromHand();
            while (randomCard == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
                randomCard = Objects.requireNonNull(getPlayerControllerByCard(effectCard)).selectRandomCardFromHand();
            }
            getGameController().removeMonsterCard(randomCard);
        } else {
            getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, getEffectCard().getCard().getName());
            for (GameCard gameCard : userCardsInHand) {
                getGameController().removeMonsterCard(gameCard);
            }
        }
        getGameController().removeSpellTrapCard(effectCard);
    }

    public void trapHole() {
        GameCard summonedCard = getGameController().getSelectionController().getCard();
        getGameController().removeMonsterCard(summonedCard);
        getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, getEffectCard().getCard().getName());
        getGameController().removeSpellTrapCard(effectCard);
        getGameController().deselect();
    }

    public void cantDraw() {
        getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, effectCard.getCard().getName());
        Utils.printInfo("you can't draw card in this turn!");
        getGameController().removeSpellTrapCard(effectCard);
    }

    public void negateAttackPhase() {
        if (getGameTurnController().getPhase().equals(Phase.BATTLE)) {
            getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, effectCard.getCard().getName());
            getGameController().removeSpellTrapCard(effectCard);
            getGameTurnController().setPhase(Phase.MAIN2);
        }
    }

    public void solemnWarning() {
        if (!getCurrentPlayerController().confirm("do you want to activate \"Solemn Warning\" trap by spending 2000LPs?"))
            return;
        if (getPlayerByCard(effectCard).getLifePoints() <= 2000) {
            Utils.printError("not enough LPs to activate this trap");
            return;
        }
        getPlayerByCard(effectCard).decreaseLifePoints(2000);
        GameCard summonedMonster = getCurrentPlayer().getField().getRecentlySummonedMonster();
        if (summonedMonster == null)
            return;
        getGameController().removeMonsterCard(summonedMonster);
        getGameController().removeSpellTrapCard(effectCard);
        getGameControllerView().showSuccess(GameView.SUCCESS_EFFECT, effectCard.getCard().getName());
    }

    public boolean magicJammer() {
        if (getGameController().getSelectionController() == null)
            return false;
        GameCard handCard = Objects.requireNonNull(getPlayerControllerByCard(effectCard)).selectCardFromHand(null);
        if (handCard == null) {
            return false;
        }
        Player player = getPlayerByCard(handCard);
        player.getField().removeFromHand(handCard);
        player.getField().putInGraveyard(handCard);
        GameCard activatedSpell = getGameController().getSelectionController().getCard();
        getGameController().removeSpellTrapCard(activatedSpell);
        return true;
    }

    public void callOfTheHaunted() {
        GameCard monsterToSummon = Objects.requireNonNull(getPlayerControllerByCard(effectCard)).selectCardFromGraveyard();
        if (monsterToSummon == null) {
            return;
        }
        if (!monsterToSummon.getCard().getCardType().equals(CardType.MONSTER)) {
            Utils.printError("trap activation failed");
            return;
        }
        new SummonController(gameControllerID, monsterToSummon).specialSummon();
    }

    private Player getPlayerByCard(GameCard card) {
        return getPlayerControllerByCard(card).getPlayer();
    }

    private void setUsed() {
        isUsedBefore = true;
    }

    public boolean containEffect(Effects effect) {
        if (getEffectCard().getEffects() == null) return false;
        return effectCard.getEffects().contains(effect);
    }

    public void decreaseRemainTurns() {
        remainsTurn--;
    }

    public void increaseLP(int amount) {
        getCurrentPlayer().increaseLifePoints(amount);
    }

    public void decreaseLP(int amount) {
        getCurrentPlayer().decreaseLifePoints(amount);
    }

    private View getGameControllerView() {
        return GameController.getView();
    }

    private PlayerController getPlayerControllerByCard(GameCard card) {
        if (getCurrentPlayerController().getPlayer().getField().isInField(card)) return getCurrentPlayerController();
        if (getRivalPlayerController().getPlayer().getField().isInField(card)) return getRivalPlayerController();
        return null;
    }

    private PlayerController getAnotherPlayerControllerByCard(GameCard card) {
        if (getCurrentPlayerController().getPlayer().getField().isInField(card)) return getRivalPlayerController();
        if (getRivalPlayerController().getPlayer().getField().isInField(card)) return getCurrentPlayerController();
        return null;
    }

    private PlayerController getCurrentPlayerController() {
        return getGameController().getCurrentPlayerController();
    }

    private PlayerController getRivalPlayerController() {
        return getGameController().getRivalPlayerController();
    }

    private Field getAttackerPlayerField() {
        return getGameController()
                .getPlayerByCard(getGameController().getAttackController().getAttackingMonster()).getField();
    }

    private Field getCurrentPlayerField() {
        return getCurrentPlayer().getField();
    }

    private Field getRivalPlayerField() {
        return getRivalPlayer().getField();
    }

    private Player getEffectCardPlayer() {
        return getGameController().getPlayerByCard(effectCard);
    }

    private Player getCurrentPlayer() {
        return getGameController().getCurrentPlayer();
    }

    private Player getRivalPlayer() {
        return getGameController().getRivalPlayer();
    }

    private GameTurnController getGameTurnController() {
        return getGameController().getGameTurnController();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }
}
