package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.card.GameCard;

public class AttackController {
    private GameCard attackingMonster;
    private GameCard attackedMonster;
    private int gameControllerID;

    public AttackController(int gameControllerID){
        this.gameControllerID = gameControllerID;
    }

    public boolean attack() {
        return true;
    }

    public boolean directAttack() {
        return true;
    }
}
