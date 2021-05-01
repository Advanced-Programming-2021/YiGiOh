package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.card.GameCard;

public class SummonController {
    private GameCard card;
    private int gameControllerID;

    public SummonController(int gameControllerID){
        this.gameControllerID = gameControllerID;
        GameController.getGameControllerById(gameControllerID).setSelectionController(null);
    }

    public boolean normalSummon() {

        return true;
    }

    public boolean tributeSummon() {
        return true;
    }

    public boolean specialSummon() {
        return true;
    }

    public boolean ritualSummon() {
        return true;
    }

    public boolean flipSummon() {
        return true;
    }
}
