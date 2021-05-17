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
        GameCard cardFromGraveyard = getCurrentPlayerController().selectCardFromAllGraveyards();
        if (cardFromGraveyard == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
        } else if (!(getCurrentPlayerField().isInGraveyard(cardFromGraveyard) ||
                getRivalPlayerField().isInGraveyard(cardFromGraveyard))) {
            getGameControllerView().showError(GameView.ERROR_NOT_FROM_PLACE, "graveyard");
        } else {
            new SummonController(gameControllerID, cardFromGraveyard).specialSummon();
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
                if (getCurrentPlayerField().isInSpellZone(spellTrap)) {
                    getGameController().removeSpellTrapCard(spellTrap);
                } else {
                    getGameController().removeSpellTrapCard(spellTrap);
                }
            }
        }
    }

    public void destroySpellTrap() {
        GameCard spellTrap = getCurrentPlayerController().selectSpellTrapsFromField(1)[0];
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

    private void ritualSummon(){
        GameCard monsterToBeRitualSummoned = getCurrentPlayerController().selectRitualMonsterFromHand();
        if (monsterToBeRitualSummoned == null || ((Monster)monsterToBeRitualSummoned.getCard()).getSummon() != MonsterSummon.RITUAL)
            return;
        new SummonController(gameControllerID,monsterToBeRitualSummoned).ritualSummon();
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
                new SummonController(gameControllerID, monsterToSummon).specialSummon();
            }
        }
    }

    public void drawCardFromGraveyard(int mostLevel) {
        boolean confirm = getCurrentPlayerController()
                .confirm("do you want to draw card with level less than 7 by remove a card from your hand?");
        if (confirm) {
            GameCard drawnCard = getCurrentPlayerController().selectCardFromGraveyard(mostLevel);
            if (drawnCard == null) return;
            if (((Monster) drawnCard.getCard()).getLevel() < 7) {
                getGameControllerView().showError(GameView.ERROR_WRONG_CARD_TYPE, "card with level less than 7");
            } else {
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
        boolean confirm = Objects.requireNonNull(getPlayerControllerByCard(effectCard))
                .confirm("do you want to active " + effectCard.getCard().getName());
        if (confirm) {
            GameCard destructibleCard = Objects.requireNonNull(getPlayerControllerByCard(effectCard)).selectRivalMonster();
            if (destructibleCard == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else if (!getRivalPlayerField().isInMonsterZone(destructibleCard)) {
                getGameControllerView().showError(GameView.ERROR_NOT_FROM_PLACE, "rival monster zone");
            } else {
                getGameController().removeMonsterCard(destructibleCard);
            }
        }
    }

    public void scanDestroyedRivalMonster() {
        GameCard monsterToScan = getCurrentPlayerController().scanMonsterForScanner();
        if (monsterToScan == null) return;
        cardsAffected.add(monsterToScan);
        effectCard.setAttackModifier(new ArrayList<>());
        effectCard.setDefenceModifier(new ArrayList<>());

        effectCard.addAttackModifier(cardsAffected.get(0).getCurrentAttack(), true);
        effectCard.addDefenceModifier(cardsAffected.get(0).getCurrentDefense(), true);
    }

    public void specialSetFromHand() {
        boolean confirm = getCurrentPlayerController().confirm("do you want to special set a normal card from your hand?!");
        if (confirm) {
            GameCard monsterToSet = getCurrentPlayerController().selectNormalCardFromHand(4);
            if (monsterToSet == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                new SetController(gameControllerID).specialSet(monsterToSet);
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

    public void changeDefence(int amount) {
        for (GameCard gameCard : cardsAffected) {
            gameCard.addDefenceModifier(amount, effectCard, false);
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
        do {
            cardForRemove = getCurrentPlayerController().selectCardFromHand(null);
            if (cardForRemove == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            }
        } while (cardForRemove == null);
        return cardForRemove;
    }

    public void magicCylinder() {
        decreaseAttackerLP(getGameController().getAttackController().getAttackingMonster().getCurrentAttack());
    }

    public void destroyAllRivalFaceUpMonsters() {
        List<GameCard> rivalFaceUpMonsters = Arrays.stream(getRivalPlayerField().getMonsterZone()).filter(Objects::nonNull).collect(Collectors.toList());
        for (GameCard rivalFaceUpMonster : rivalFaceUpMonsters) {
            if (rivalFaceUpMonster == null) continue;
            getGameController().removeMonsterCard(rivalFaceUpMonster);
        }
    }

    public void mindCrush() {
        Card userCard = getCurrentPlayerController().getACard();
        if (userCard == null) {
            getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            return;
        }
        List<GameCard> userCardsInHand = getCurrentPlayerField().getHand().stream().filter(e -> e.getCard().getName()
                .equals(userCard.getName())).collect(Collectors.toList());
        if (userCardsInHand.size() == 0) {
            GameCard randomCard = getCurrentPlayerController().selectRandomCardFromHand();
            if (randomCard == null) {
                getGameControllerView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                getGameController().removeMonsterCard(randomCard);
            }
        } else {
            for (GameCard gameCard : userCardsInHand) {
                getGameController().removeMonsterCard(gameCard);
            }
        }
    }

    public void trapHole() {
        if (getGameController().getSelectionController() == null)
            return;
        GameCard summonedCard = getGameController().getSelectionController().getCard();
        if (((Monster)summonedCard.getCard()).getAttackPoints() <= 1000)
            return;
        //check for activating trap
        if (!getCurrentPlayerController().confirm("Do you want to activate your \"Trap Hole\" trap?"))
            return;
        if (!activateTrapAndCheck())
            return;
        getGameController().removeMonsterCard(summonedCard);
    }

    public void negateAttackPhase() {
        if (!getCurrentPlayerController().confirm("do you want to activate your \"Negate Attack\" trap?"))
            return;
        if (!activateTrapAndCheck())
            return;
        getGameController().nextPhase();
    }

    public void solemnWarning() {
        if (!getCurrentPlayerController().confirm("do you want to activate \"Solemn Warning\" trap by spending 2000LPs?"))
            return;
        if (getCurrentPlayer().getLifePoints() <= 2000){
            Utils.printError("not enough LPs to activate this trap");
            return;
        }
        if (!activateTrapAndCheck())
            return;
        getCurrentPlayer().decreaseLifePoints(2000);
        GameCard summonedMonster = getCurrentPlayer().getField().getRecentlySummonedMonster();
        if (summonedMonster == null)
            return;
        getGameController().removeMonsterCard(summonedMonster);
    }

    public void magicJammer() {
        if (getGameController().getSelectionController() == null)
            return;
        if (!getGameController().getCurrentPlayerController().confirm("do you want to activate your \"Magic Jammer\" trap?"))
            return;
        GameCard handCard = getGameController().getCurrentPlayerController().selectCardFromHand(null);
        if (handCard == null) {
            Utils.printError("trap activation failed");
            return;
        }
        if (!activateTrapAndCheck())
            return;
        getGameController().getCurrentPlayer().getField().removeFromHand(handCard);
        getGameController().getCurrentPlayer().getField().putInGraveyard(handCard);
        GameCard activatedSpell = getGameController().getSelectionController().getCard();
        getGameController().removeSpellTrapCard(activatedSpell);
    }

    public void callOfTheHaunted() {
        if (!getCurrentPlayerController().confirm("do you want to activate your \"Call of the Haunted\" trap?"))
            return;
        GameCard monsterToSummon = getCurrentPlayerController().selectCardFromGraveyard();
        if (monsterToSummon == null || !monsterToSummon.getCard().getCardType().equals(CardType.MONSTER)){
            Utils.printError("trap activation failed");
            return;
        }
        if (!activateTrapAndCheck())
            return;
        new SummonController(gameControllerID,monsterToSummon).specialSummon();
    }

    private boolean activateTrapAndCheck(){
        Utils.printSuccess("trap activated successfully");
        getGameController().removeSpellTrapCard(effectCard);
        EffectResponse effectResponse = getGameController().applyEffect(Trigger.BEFORE_ACTIVE_TRAP);
        if (effectResponse != null && effectResponse.equals(EffectResponse.ACTIVE_TRAP_CANT_BE_DONE)){
            Utils.printError("your trap function failed");
            return false;
        }
        return true;
    }

    public void resetAffectedCards() {
        cardsAffected = new ArrayList<>();
    }

    private void setUsed() {
        isUsedBefore = true;
    }

    public boolean containEffect(Effects effect) {
        return effectCard.getCard().getCardEffects().contains(effect);
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
