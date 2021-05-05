package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Effects;
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

    public static boolean canBeDestroyedInNormalAttack(GameCard effectCard) {
        if (effectCard.getCard().getCardEffects().contains(Effects.CANT_BE_DESTROYED_IN_NORMAL_ATTACK))
            return true;
        return false;
    }

    public static void decreaseAttackInNormalSummon(GameCard effectCard, int amount) {
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
        } else if (!(GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().isInGraveyard(cardFromGraveyard) ||
                GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                        .getField().isInGraveyard(cardFromGraveyard))) {
            //this card is not from graveyard
        } else {
            if (GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().isInGraveyard(cardFromGraveyard)) {
                GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                        .getField().removeFromGraveyard(cardFromGraveyard);
            } else {
                GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                        .getField().removeFromGraveyard(cardFromGraveyard);
            }
            new SummonController(gameControllerID).specialSummon(cardFromGraveyard);
        }
    }

    public void addFieldSpellToHand() {
        GameCard cardFromDeck = null;
        //get card from deck
        if (cardFromDeck == null) {
            //show error
        } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().isInDeck(cardFromDeck)) {
            //this card is not from your deck
        } else if (!cardFromDeck.getCard().getCardType().equals(CardType.SPELL)) {
            //this card is not spell
        } else if (!((Spell) cardFromDeck.getCard()).getProperty().equals(SpellProperty.FIELD)) {
            //this spell is not field spell
        } else {
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().removeFromDeck(cardFromDeck);
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().putInHand(cardFromDeck);
        }
    }

    public void drawCard(int amount) {
        if (amount + GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().getHand().size() == 8) {
            GameCard cardForRemove = null;
            //get card from hand
            if (cardForRemove == null) {
                //show error
            } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().isInHand(cardForRemove)) {
                //this card is not from hand
            } else {
                GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                        .getField().removeFromHand(cardForRemove);
            }
        } else if (amount + GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().getHand().size() == 9) {
            for (int i = 0; i < 2; i++) {
                GameCard cardForRemove = null;
                //get card from hand
                if (cardForRemove == null) {
                    //show error
                } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                        .getField().isInHand(cardForRemove)) {
                    //this card is not from hand
                } else {
                    GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                            .getField().removeFromHand(cardForRemove);
                }
            }
        }
        for (int i = 0; i < amount; i++) {
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().putInHand(GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().drawCard());
        }
    }

    public void controlRivalMonster() {
        GameCard rivalMonster = null;
        //get one of rival monster
        if (rivalMonster == null) {
            //show error
        } else if (!GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                .getField().isInMonsterZone(rivalMonster)) {
            //this card is not from rival monster zone
        } else {
            cardsAffected.add(rivalMonster);
        }
    }

    public void flipAllRivalFaceDownMonsters() {
        for (GameCard rivalMonster : GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                .getField().getMonsterZone()) {
            if (rivalMonster != null) {
                new SummonController(gameControllerID).flipSummon();
            }
        }
    }

    public void destroyCurrentPlayerMonsters() {
        int monsterIndex = 0;
        for (; monsterIndex < 5; monsterIndex++) {
            GameCard monsterForRemove = GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().getMonsterZone()[monsterIndex];

            if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer().getField()
                    .isInMonsterZone(monsterForRemove)) continue;
            //remove from monster zone
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().removeFromMonsterZone(monsterForRemove);
            //add monster to graveyard
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().putInGraveyard(monsterForRemove);
        }
    }

    public void destroyRivalMonsters() {
        int monsterIndex = 0;
        for (; monsterIndex < 5; monsterIndex++) {
            GameCard monsterForRemove = GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().getMonsterZone()[monsterIndex];

            if (!GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField()
                    .isInMonsterZone(monsterForRemove)) continue;
            //remove from monster zone
            GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().removeFromMonsterZone(monsterForRemove);
            //add monster to graveyard
            GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().putInGraveyard(monsterForRemove);
        }
    }

    public void twinTwisters() {
        GameCard cardToRemoveFromHand = null;
        //get card from view
        if (cardToRemoveFromHand == null) {
            //show error
        } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().isInHand(cardToRemoveFromHand)) {
            //this card is not from your hand
        } else {
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().removeFromHand(cardToRemoveFromHand);
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().putInGraveyard(cardToRemoveFromHand);
            List<GameCard> spellTraps = null;
            //get list of spell traps
            for (GameCard spellTrap : spellTraps) {
                if (GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                        .getField().isInSpellZone(spellTrap)) {
                    GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                            .getField().removeFromSpellZone(spellTrap);
                    GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                            .getField().putInGraveyard(spellTrap);
                } else {
                    GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                            .getField().removeFromSpellZone(spellTrap);
                    GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                            .getField().putInGraveyard(spellTrap);
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
            if (GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().isInSpellZone(spellTrap)) {
                GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                        .getField().removeFromSpellZone(spellTrap);
                GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                        .getField().putInGraveyard(spellTrap);
            } else {
                GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                        .getField().removeFromSpellZone(spellTrap);
                GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                        .getField().putInGraveyard(spellTrap);
            }
        }
    }

    public void yami() {
        for (GameCard monster : GameController.getGameControllerById(gameControllerID)
                .getCurrentPlayer().getField().getMonsterZone()) {
            if (((Monster)monster.getCard()).getType().equals(MonsterType.FIEND)
                    || ((Monster)monster.getCard()).getType().equals(MonsterType.SPELLCASTER)) {
                monster.addAttackModifier(200);
                monster.addDefenceModifier(200);
            } else if (((Monster)monster.getCard()).getType().equals(MonsterType.FAIRY)) {
                monster.addAttackModifier(-200);
                monster.addDefenceModifier(-200);
            }
        }
        for (GameCard monster : GameController.getGameControllerById(gameControllerID)
                .getRivalPlayer().getField().getMonsterZone()) {
            if (((Monster)monster.getCard()).getType().equals(MonsterType.FIEND)
                    || ((Monster)monster.getCard()).getType().equals(MonsterType.SPELLCASTER)) {
                monster.addAttackModifier(200);
                monster.addDefenceModifier(200);
            } else if (((Monster)monster.getCard()).getType().equals(MonsterType.FAIRY)) {
                monster.addAttackModifier(-200);
                monster.addDefenceModifier(-200);
            }
        }
    }

    public void forest() {
        for (GameCard monster : GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().getMonsterZone()) {
            if (((Monster)monster.getCard()).getType().equals(MonsterType.INSECT)
                    || ((Monster)monster.getCard()).getType().equals(MonsterType.BEAST)
                    || ((Monster)monster.getCard()).getType().equals(MonsterType.BEAST_WARRIOR)) {
                monster.addAttackModifier(200);
                monster.addDefenceModifier(200);
            }
        }
        for (GameCard monster : GameController.getGameControllerById(gameControllerID)
                .getRivalPlayer().getField().getMonsterZone()) {
            if (((Monster)monster.getCard()).getType().equals(MonsterType.INSECT)
                    || ((Monster)monster.getCard()).getType().equals(MonsterType.BEAST)
                    || ((Monster)monster.getCard()).getType().equals(MonsterType.BEAST_WARRIOR)) {
                monster.addAttackModifier(200);
                monster.addDefenceModifier(200);
            }
        }
    }

    public void closedForest() {
        for (GameCard monster : GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().getMonsterZone()) {
            if (((Monster)monster.getCard()).getType().equals(MonsterType.BEAST)) {
                int amount = GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                        .getField().getGraveyard().size();
                monster.addAttackModifier(amount);
                monster.addDefenceModifier(amount);
            }
        }
    }

    public void umiiruka() {
        for (GameCard monster : GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().getMonsterZone()) {
            if (((Monster)monster.getCard()).getType().equals(MonsterType.AQUA)) {
                monster.addAttackModifier(500);
                monster.addDefenceModifier(-400);
            }
        }
    }

    public void destroyAllRivalCards() {
        destroyRivalMonsters();
        destroyRivalSpellTraps();
        destroyRivalFieldZone();
    }

    public void destroyAttackerCard() {
        GameCard cardToDestroy = GameController.getGameControllerById(gameControllerID)
                .getSelectionController().getCard();
        //remove from monster zone
        GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().removeFromMonsterZone(cardToDestroy);
        //add to graveyard
        GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().putInGraveyard(cardToDestroy);
    }

    public void specialSummonByRemoveCardFromHand() {
        GameCard cardToRemoveFromHand = null;
        //get card from view
        if (cardToRemoveFromHand == null) {
            //show error
        } else if (!GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().isInHand(cardToRemoveFromHand)) {
            //this card is not from your hand
        } else {
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().removeFromHand(cardToRemoveFromHand);
            new SummonController(gameControllerID).specialSummon(effectCard);
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
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().removeFromGraveyard(drawnCard);
            //add card to hand
            GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                    .getField().putInHand(drawnCard);
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
            new SummonController(gameControllerID).specialSummon(monsterToSummon);
        }
    }

    public boolean isNeutralizerAttack(GameCard effectCard) {
        return !GameController.getGameControllerById(gameControllerID).getGameTurnController().getDisposableUsedCards().contains(effectCard);
    }

    public void destroyRivalFieldZone() {
        GameCard fieldSpell = GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                .getField().getFieldZone();
        if (fieldSpell != null) {
            //remove from field zone
            GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().removeFromFieldZone(fieldSpell);
            //add field spell to graveyard
            GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().putInGraveyard(fieldSpell);
        }
    }

    public void destroyRivalSpellTraps() {
        int spellTrapIndex = 0;
        for (; spellTrapIndex < 5; spellTrapIndex++) {
            GameCard spellTrap = GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().getSpellZone()[spellTrapIndex];

            if (!GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField()
                    .isInSpellZone(spellTrap)) continue;
            //remove from spell zone
            GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().removeFromSpellZone(spellTrap);
            //add spell or trap to graveyard
            GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().putInGraveyard(spellTrap);
        }
    }

    public void decreaseAttackerLP(int amount) {
        GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .changeLifePoints(amount);
    }

    public void destroyOneOfRivalMonsters() {
        GameCard destructibleCard = null;
        //Get a monster from view
        if (destructibleCard == null) {
            return;
        } else if (!GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                .getField().isInMonsterZone(destructibleCard)) {
            //card is not from rival monster zone
        } else {
            GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().removeFromMonsterZone(destructibleCard);
            GameController.getGameControllerById(gameControllerID).getRivalPlayer()
                    .getField().putInGraveyard(destructibleCard);
        }
    }

    public void scanAnotherCard(GameCard card) {
        //some codes
    }

    public void disposableEffect() {
        GameController.getGameControllerById(gameControllerID).getGameTurnController()
                .getDisposableUsedCards().add(effectCard);
    }

    public void selectFaceUpMonsters() {
        GameCard[] monsterZone = GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().getMonsterZone();
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
        GameCard[] monsterZone = GameController.getGameControllerById(gameControllerID).getCurrentPlayer()
                .getField().getMonsterZone();
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
}
