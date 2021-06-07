package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.Effects;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.Trigger;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttackController {
    private GameCard attackingMonster;
    private GameCard attackedMonster;
    private int gameControllerID;
    private boolean lpsCanBeChanged;

    public AttackController(int gameControllerID) {
        attackingMonster = GameController.getGameControllerById(gameControllerID).getSelectionController().getCard();

        this.gameControllerID = gameControllerID;
        lpsCanBeChanged = true;
    }

    public AttackController(int gameControllerID, int position) {
        attackingMonster = GameController.getGameControllerById(gameControllerID).getSelectionController().getCard();
        attackedMonster = GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField().getMonsterZone()[position - 1];

        this.gameControllerID = gameControllerID;
        lpsCanBeChanged = true;
    }

    public boolean attack() {
        boolean wasRevealed = attackedMonster.isRevealed();
        if (!wasRevealed) {
            new SummonController(gameControllerID, attackedMonster).flipSummon();
            attackedMonster.setFaceDown(true);
            Utils.printInfo("opponent's monster was " + attackedMonster.getCard().getName() + "\n");
        }
        if (attackedMonster.isFaceDown())
            attackToDefensiveMonster(getAttackPoints(attackingMonster),getDefensePoints(attackedMonster));
        else
            attackToOffensiveMonster(getAttackPoints(attackingMonster),getAttackPoints(attackedMonster));
        if (!wasRevealed) {
            getGameController().applyEffect(Trigger.AFTER_FLIP_SUMMON);
        }
        getGameController().applyEffect(Trigger.AFTER_ATTACK);
        return true;
    }

    private void attackToDefensiveMonster(int playerPoints,int rivalPoints){
        if (playerPoints > rivalPoints) {
            if (canBeDestroyed(attackedMonster)) {
                getGameController().knockOutMonster(attackedMonster);
                Utils.printSuccess("the defense position monster is destroyed");
            } else {
                Utils.printSuccess("no card was destroyed because of attacked card effects");
            }
        } else if (rivalPoints > playerPoints) {
            int damagePoints = rivalPoints - playerPoints;
            damagePlayer(getGameController().getCurrentPlayer(),damagePoints);
            if (lpsCanBeChanged)
                Utils.printError("no card is destroyed and you received " + damagePoints +" battle damage");
        } else{
            Utils.printInfo("no card is destroyed");
        }
    }

    private void attackToOffensiveMonster(int playerPoints,int rivalPoints){
        if (playerPoints > rivalPoints){
            int damagePoints = playerPoints - rivalPoints;
            if (canBeDestroyed(attackedMonster)) {
                getGameController().knockOutMonster(attackedMonster);
                damagePlayer(getGameController().getRivalPlayer(),damagePoints);
                if (lpsCanBeChanged)
                    Utils.printSuccess("your opponent’s monster is destroyed and your opponent receives " + damagePoints + " battle damage");
            } else {
                Utils.printSuccess("no card was destroyed because of attacked card effects");
            }
        } else if (rivalPoints > playerPoints){
            int damagePoints = rivalPoints - playerPoints;
            if (canBeDestroyed(attackingMonster)) {
                getGameController().knockOutMonster(attackingMonster);
                damagePlayer(getGameController().getCurrentPlayer(),damagePoints);
                if (lpsCanBeChanged)
                    Utils.printError("Your monster card is destroyed and you received " + damagePoints + " battle damage");
            } else {
                Utils.printSuccess("no card was destroyed because of attacking card effects");
            }
        } else {
            if (canBeDestroyed(attackedMonster)) {
                getGameController().knockOutMonster(attackedMonster);
            } else {
                Utils.printSuccess("attacked card can't be destroyed");
            }
            if (canBeDestroyed(attackingMonster)) {
                getGameController().knockOutMonster(attackingMonster);
            } else {
                Utils.printSuccess("attacking card can't be destroyed");
            }
            Utils.printInfo("both you and your opponent monster cards are destroyed and no one receives damage");
        }
    }

    public boolean directAttack() {
        damagePlayer(getGameController().getRivalPlayer(),getAttackPoints(attackingMonster));
        return true;
    }

    private void damagePlayer(Player player,int damagePoints){
        if (lpsCanBeChanged)
            player.setLifePoints(player.getLifePoints() - damagePoints);
    }

    public int getAttackPoints(GameCard card) {
        return card.getCurrentAttack();
    }

    public int getDefensePoints(GameCard card) {
        return card.getCurrentDefense();
    }

    private GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }

    private boolean canBeDestroyed(GameCard card) {
        return !card.getCard().getCardEffects().contains(Effects.CANT_BE_DESTROYED_IN_NORMAL_ATTACK);
    }

}
