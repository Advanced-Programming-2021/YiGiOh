package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.Trigger;
import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;
import edu.sharif.ce.apyugioh.view.GameView;

import java.util.List;

public class AttackController {
    private GameCard attackingMonster;
    private GameCard attackedMonster;
    private int gameControllerID;

    public AttackController(int gameControllerID){
        attackingMonster = GameController.getGameControllerById(gameControllerID).getSelectionController().getCard();
        this.gameControllerID = gameControllerID;
    }

    public AttackController(int gameControllerID,int position) {
        attackingMonster = GameController.getGameControllerById(gameControllerID).getSelectionController().getCard();
        attackedMonster = GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField().getMonsterZone()[position];
        this.gameControllerID = gameControllerID;
    }

    public boolean attack() {
        boolean wasRevealed = attackedMonster.isRevealed();
        if (!wasRevealed) {
            new SummonController(gameControllerID, attackedMonster).flipSummon();
            attackedMonster.setFaceDown(true);
        }
        int playerPoints = getAttackPoints(attackingMonster);
        int rivalPoints = 0;
        if (attackedMonster.isFaceDown()){
            rivalPoints = getDefensePoints(attackedMonster);
        }else{
            rivalPoints = getAttackPoints(attackedMonster);
        }
        if (!wasRevealed){
            getGameController().getCurrentPlayerEffectControllers().add(new EffectController(gameControllerID,attackedMonster));
            getGameController().applyEffect(Trigger.AFTER_FLIP_SUMMON);
        }
        return true;
    }

    public boolean directAttack() {
        getGameController().getRivalPlayer().setLifePoints(getGameController().getRivalPlayer().getLifePoints() - getAttackPoints(attackingMonster));
        return true;
    }

    public int getAttackPoints(GameCard card){
        int attackPoints = ((Monster)card.getCard()).getAttackPoints();
        for(Integer modifier:card.getAttackModifier())
            attackPoints += modifier;
        if (attackPoints < 0)
            attackPoints = 0;
        //special Cases
        if (card.getCard().getName().equals("The Calculator")){
            int levelsSum = 0;
            for(GameCard summingCard:getGameController().getCurrentPlayer().getField().getMonsterZone()){
                if (summingCard.isRevealed())
                    levelsSum += ((Monster)summingCard.getCard()).getLevel();
            }
            attackPoints = levelsSum*300;
        }
        return attackPoints;
    }

    public int getDefensePoints(GameCard card){
        int defensePoints = ((Monster)card.getCard()).getDefensePoints();
        for (Integer modifier : card.getDefenceModifier())
            defensePoints += modifier;
        if (defensePoints < 0)
            defensePoints = 0;
        //special cases

        return defensePoints;
    }

    private GameController getGameController(){
        return GameController.getGameControllerById(gameControllerID);
    }

}
