package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.card.GameCard;
import edu.sharif.ce.apyugioh.model.card.Monster;

import java.util.List;

public class AttackController {
    private GameCard attackingMonster;
    private GameCard attackedMonster;
    private int gameControllerID;

    public AttackController(int gameControllerID){
        attackingMonster = GameController.getGameControllerById(gameControllerID).getSelectionController().getCard();
        this.gameControllerID = gameControllerID;
    }

    public AttackController(int gameControllerID,int position){
        attackingMonster = GameController.getGameControllerById(gameControllerID).getSelectionController().getCard();
        attackedMonster = GameController.getGameControllerById(gameControllerID).getRivalPlayer().getField().getMonsterZone()[position];
        this.gameControllerID = gameControllerID;
    }

    public boolean attack() {
        return true;
    }

    public boolean directAttack() {
        getGameController().getRivalPlayer().setLifePoints(getGameController().getRivalPlayer().getLifePoints() - getAttackPoints());
        return true;
    }

    public int getAttackPoints(){
        int attackPoints = ((Monster)attackingMonster.getCard()).getAttackPoints();
        for(Integer modifier:attackingMonster.getAttackModifier())
            attackPoints += modifier;
        if (attackPoints < 0)
            attackPoints = 0;
        //special Cases
        if (attackingMonster.getCard().getName().equals("The Calculator")){
            int levelsSum = 0;
            for(GameCard card:getGameController().getCurrentPlayer().getField().getMonsterZone()){
                if (card.isRevealed())
                    levelsSum += ((Monster)card.getCard()).getLevel();
            }
            attackPoints = levelsSum*300;
        }
        return attackPoints;
    }

    public int getDefensePoint(){
        int defensePoints = ((Monster)attackedMonster.getCard()).getDefensePoints();
        for (Integer modifier : attackingMonster.getDefenceModifier())
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
