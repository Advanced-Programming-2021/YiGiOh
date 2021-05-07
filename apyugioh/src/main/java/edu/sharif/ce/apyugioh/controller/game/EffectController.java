package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Effects;
import edu.sharif.ce.apyugioh.model.Field;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.*;

import java.util.ArrayList;
import java.util.List;

public class EffectController {
    private List<GameCard> cardsAffected;
    private GameCard effectCard;
    private int remainsTurn;
    private int gameControllerID;

    public EffectController(int gameControllerID, GameCard effectCard) {
        this.gameControllerID = gameControllerID;
        cardsAffected = new ArrayList<>();
    }

    public boolean canBeDestroyedInNormalAttack(GameCard effectCard) {
        if (effectCard.getCard().getCardEffects().contains(Effects.CANT_BE_DESTROYED_IN_NORMAL_ATTACK))
            return true;
        return false;
    }

    public void decreaseAttackInNormalSummon(GameCard effectCard, int amount) {
        if (effectCard.getCard().getCardEffects().contains(Effects.DECREASE_ATTACK_POWER_IN_NORMAL_SUMMON)) {
            effectCard.addAttackModifier(amount);
        }
    }

    public boolean canRivalActiveTrap() {
        return !effectCard.getCard().getCardEffects().contains(Effects.RIVAL_CANT_ACTIVE_TRAP);
    }

    public boolean doLifePointsChange() {
        return !effectCard.getCard().getCardEffects().contains(Effects.LPS_DOESNT_CHANGE);
    }

    public void specialSummonFromGraveyard() {
        GameCard cardFromGraveyard = null;
        //get card from view
        if (cardFromGraveyard == null) {
            //show error
        } else if (!(getCurrentPlayerField().isInGraveyard(cardFromGraveyard) ||
                getRivalPlayerField().isInGraveyard(cardFromGraveyard))) {
            //this card is not from graveyard
        } else {
            if (getCurrentPlayerField().isInGraveyard(cardFromGraveyard)) {
                getCurrentPlayerField().removeFromGraveyard(cardFromGraveyard);
            } else {
                getRivalPlayerField().removeFromGraveyard(cardFromGraveyard);
            }
            new SummonController(gameControllerID,cardFromGraveyard).specialSummon();
        }
    }

    public void addFieldSpellToHand() {
        GameCard cardFromDeck = null;
        //get card from deck
        if (cardFromDeck == null) {
            //show error
        } else if (!getCurrentPlayerField().isInDeck(cardFromDeck)) {
            //this card is not from your deck
        } else if (!cardFromDeck.getCard().getCardType().equals(CardType.SPELL)) {
            //this card is not spell
        } else if (!((Spell) cardFromDeck.getCard()).getProperty().equals(SpellProperty.FIELD)) {
            //this spell is not field spell
        } else {
            getCurrentPlayerField().removeFromDeck(cardFromDeck);
            getCurrentPlayerField().putInHand(cardFromDeck);
        }
    }

    public void drawCard(int amount) {
        if (amount + getCurrentPlayerField().getHand().size() == 8) {
            GameCard cardForRemove = null;
            //get card from hand
            if (cardForRemove == null) {
                //show error
            } else if (!getCurrentPlayerField().isInHand(cardForRemove)) {
                //this card is not from hand
            } else {
                getCurrentPlayerField().removeFromHand(cardForRemove);
            }
        } else if (amount + getCurrentPlayerField().getHand().size() == 9) {
            for (int i = 0; i < 2; i++) {
                GameCard cardForRemove = null;
                //get card from hand
                if (cardForRemove == null) {
                    //show error
                } else if (!getCurrentPlayerField().isInHand(cardForRemove)) {
                    //this card is not from hand
                } else {
                    getCurrentPlayerField().removeFromHand(cardForRemove);
                }
            }
        }
        for (int i = 0; i < amount; i++) {
            getCurrentPlayerField().putInHand(getCurrentPlayerField().drawCard());
        }
    }

    public void controlRivalMonster() {
        GameCard rivalMonster = null;
        //get one of rival monster
        if (rivalMonster == null) {
            //show error
        } else if (!getRivalPlayerField().isInMonsterZone(rivalMonster)) {
            //this card is not from rival monster zone
        } else {
            cardsAffected.add(rivalMonster);
        }
    }

    public void flipAllRivalFaceDownMonsters() {
        for (GameCard rivalMonster : getRivalPlayerField().getMonsterZone()) {
            if (rivalMonster != null) {
                new SummonController(gameControllerID,rivalMonster).flipSummon();
            }
        }
    }

    public void destroyCurrentPlayerMonsters() {
        int monsterIndex = 0;
        for (; monsterIndex < 5; monsterIndex++) {
            GameCard monsterForRemove = getCurrentPlayerField().getMonsterZone()[monsterIndex];

            if (!getCurrentPlayerField().isInMonsterZone(monsterForRemove)) continue;
            //remove from monster zone
            getCurrentPlayerField().removeFromMonsterZone(monsterForRemove);
            //add monster to graveyard
            getCurrentPlayerField().putInGraveyard(monsterForRemove);
        }
    }

    public void destroyRivalMonsters() {
        int monsterIndex = 0;
        for (; monsterIndex < 5; monsterIndex++) {
            GameCard monsterForRemove = getRivalPlayerField().getMonsterZone()[monsterIndex];

            if (!getRivalPlayerField()
                    .isInMonsterZone(monsterForRemove)) continue;
            //remove from monster zone
            getRivalPlayerField().removeFromMonsterZone(monsterForRemove);
            //add monster to graveyard
            getRivalPlayerField().putInGraveyard(monsterForRemove);
        }
    }

    public void twinTwisters() {
        GameCard cardToRemoveFromHand = null;
        //get card from view
        if (cardToRemoveFromHand == null) {
            //show error
        } else if (!getCurrentPlayerField().isInHand(cardToRemoveFromHand)) {
            //this card is not from your hand
        } else {
            getCurrentPlayerField().removeFromHand(cardToRemoveFromHand);
            getCurrentPlayerField().putInGraveyard(cardToRemoveFromHand);
            List<GameCard> spellTraps = null;
            //get list of spell traps
            for (GameCard spellTrap : spellTraps) {
                if (getCurrentPlayerField().isInSpellZone(spellTrap)) {
                    getCurrentPlayerField().removeFromSpellZone(spellTrap);
                    getCurrentPlayerField().putInGraveyard(spellTrap);
                } else {
                    getRivalPlayerField().removeFromSpellZone(spellTrap);
                    getRivalPlayerField().putInGraveyard(spellTrap);
                }
            }
        }
    }

    public void destroySpellTrap() {
        GameCard spellTrap = null;
        //get card from view
        if (spellTrap == null) {
            //show error
        } else {
            if (getCurrentPlayerField().isInSpellZone(spellTrap)) {
                getCurrentPlayerField().removeFromSpellZone(spellTrap);
                getCurrentPlayerField().putInGraveyard(spellTrap);
            } else {
                getRivalPlayerField().removeFromSpellZone(spellTrap);
                getRivalPlayerField().putInGraveyard(spellTrap);
            }
        }
    }

    public void yami() {
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (((Monster) monster.getCard()).getType().equals(MonsterType.FIEND)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.SPELLCASTER)) {
                monster.addAttackModifier(200);
                monster.addDefenceModifier(200);
            } else if (((Monster) monster.getCard()).getType().equals(MonsterType.FAIRY)) {
                monster.addAttackModifier(-200);
                monster.addDefenceModifier(-200);
            }
        }
        for (GameCard monster : getRivalPlayerField().getMonsterZone()) {
            if (((Monster) monster.getCard()).getType().equals(MonsterType.FIEND)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.SPELLCASTER)) {
                monster.addAttackModifier(200);
                monster.addDefenceModifier(200);
            } else if (((Monster) monster.getCard()).getType().equals(MonsterType.FAIRY)) {
                monster.addAttackModifier(-200);
                monster.addDefenceModifier(-200);
            }
        }
    }

    public void forest() {
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (((Monster) monster.getCard()).getType().equals(MonsterType.INSECT)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.BEAST)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.BEAST_WARRIOR)) {
                monster.addAttackModifier(200);
                monster.addDefenceModifier(200);
            }
        }
        for (GameCard monster : getRivalPlayerField().getMonsterZone()) {
            if (((Monster) monster.getCard()).getType().equals(MonsterType.INSECT)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.BEAST)
                    || ((Monster) monster.getCard()).getType().equals(MonsterType.BEAST_WARRIOR)) {
                monster.addAttackModifier(200);
                monster.addDefenceModifier(200);
            }
        }
    }

    public void closedForest() {
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (((Monster) monster.getCard()).getType().equals(MonsterType.BEAST)) {
                int amount = getCurrentPlayerField().getGraveyard().size();
                monster.addAttackModifier(amount);
                monster.addDefenceModifier(amount);
            }
        }
    }

    public void umiiruka() {
        for (GameCard monster : getCurrentPlayerField().getMonsterZone()) {
            if (((Monster) monster.getCard()).getType().equals(MonsterType.AQUA)) {
                monster.addAttackModifier(500);
                monster.addDefenceModifier(-400);
            }
        }
    }

    public void swordOfDarkDestruction() {
        GameCard equipMonster = null;
        //get monster form view
        if (equipMonster == null) {
            //show error
        } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().isInMonsterZone(equipMonster)) {
            //card is not from monster zone
        } else {
            
        }
    }

    public void destroyAllRivalCards() {
        destroyRivalMonsters();
        destroyRivalSpellTraps();
        destroyRivalFieldZone();
    }

    public void destroyAttackerCard() {
        GameCard cardToDestroy = getGameController().getSelectionController().getCard();
        //remove from monster zone
        getCurrentPlayerField().removeFromMonsterZone(cardToDestroy);
        //add to graveyard
        getCurrentPlayerField().putInGraveyard(cardToDestroy);
    }

    public void specialSummonByRemoveCardFromHand() {
        GameCard cardToRemoveFromHand = null;
        //get card from view
        if (cardToRemoveFromHand == null) {
            //show error
        } else if (!getCurrentPlayerField().isInHand(cardToRemoveFromHand)) {
            //this card is not from your hand
        } else {
            getCurrentPlayerField().removeFromHand(cardToRemoveFromHand);
            new SummonController(gameControllerID,effectCard).specialSummon();
        }
    }

    public void setNormalMonster() {
        GameCard monsterToSet = null;
        //get monster from view
        if (monsterToSet == null) {

        } else if (monsterToSet.getCard().getCardEffects().size() != 0) {
            //this monster has some effects
        } else {
            new SetController(gameControllerID).specialSet(monsterToSet);
        }
    }

    public void drawCardFromGraveyard() {
        GameCard drawnCard = null;
        //get card from graveyard
        if (((Monster) drawnCard.getCard()).getLevel() < 7) {
            //level is under 7
        } else {
            //remove from graveyard
            getCurrentPlayerField().removeFromGraveyard(drawnCard);
            //add card to hand
            getCurrentPlayerField().putInHand(drawnCard);
        }
    }

    public void specialSummonNormalMonster() {
        GameCard monsterToSummon = null;
        //get monster from view
        if (monsterToSummon == null) {
            return;
        } else if (monsterToSummon.getCard().getCardEffects().size() != 0) {
            //this monster has some effects
        } else {
            new SummonController(gameControllerID,monsterToSummon).specialSummon();
        }
    }

    public boolean isNeutralizerAttack(GameCard effectCard) {
        return !getGameTurnController().getDisposableUsedCards().contains(effectCard);
    }

    public void destroyRivalFieldZone() {
        GameCard fieldSpell = getRivalPlayerField().getFieldZone();
        if (fieldSpell != null) {
            //remove from field zone
            getRivalPlayerField().removeFromFieldZone(fieldSpell);
            //add field spell to graveyard
            getRivalPlayerField().putInGraveyard(fieldSpell);
        }
    }

    public void destroyRivalSpellTraps() {
        int spellTrapIndex = 0;
        for (; spellTrapIndex < 5; spellTrapIndex++) {
            GameCard spellTrap = getRivalPlayerField().getSpellZone()[spellTrapIndex];

            if (!getRivalPlayerField().isInSpellZone(spellTrap)) continue;
            //remove from spell zone
            getRivalPlayerField().removeFromSpellZone(spellTrap);
            //add spell or trap to graveyard
            getRivalPlayerField().putInGraveyard(spellTrap);
        }
    }

    public void decreaseAttackerLP(int amount) {
        getCurrentPlayer().decreaseLifePoints(amount);
    }

    public void destroyOneOfRivalMonsters() {
        GameCard destructibleCard = null;
        //Get a monster from view
        if (destructibleCard == null) {
            return;
        } else if (!getRivalPlayerField().isInMonsterZone(destructibleCard)) {
            //card is not from rival monster zone
        } else {
            getRivalPlayerField().removeFromMonsterZone(destructibleCard);
            getRivalPlayerField().putInGraveyard(destructibleCard);
        }
    }

    public void scanAnotherCard(GameCard card) {
        //some codes
    }

    public void disposableEffect() {
        getGameTurnController().getDisposableUsedCards().add(effectCard);
    }

    public void selectFaceUpMonsters() {
        GameCard[] monsterZone = getCurrentPlayerField().getMonsterZone();
        for (GameCard monster : monsterZone) {
            if (!monster.isFaceDown()) cardsAffected.add(monster);
        }
    }

    public void changeAttack(int amount) {
        for (GameCard gameCard : cardsAffected) {
            gameCard.addAttackModifier(amount);
        }
    }

    public void changeDefence(int amount) {
        for (GameCard gameCard : cardsAffected) {
            gameCard.addDefenceModifier(amount);
        }
    }

    public int combineLevelsOfFaceUpCards() {
        int result = 0;
        GameCard[] monsterZone = getCurrentPlayerField().getMonsterZone();
        for (GameCard gameCard : monsterZone) {
            if (gameCard == null) continue;
            if (!gameCard.getCard().getCardType().equals(CardType.MONSTER)) continue;
            if (!gameCard.isFaceDown()) {
                Monster monster = (Monster) gameCard.getCard();
                result = monster.getLevel();
            }
        }
        return result;
    }

    public boolean containEffect(Effects effect) {
        return effectCard.getCard().getCardEffects().contains(effect);
    }

    private Field getCurrentPlayerField() {
        return getCurrentPlayer().getField();
    }

    private Field getRivalPlayerField() {
        return getRivalPlayer().getField();
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
