package edu.sharif.ce.apyugioh.controller.player;

import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.GameCard;

public class NormalPlayerController extends PlayerController{

    public NormalPlayerController(Player player) {
        super(player);
    }

    //special Cases

    //Scanner
    public GameCard scanMonsterForScanner(){
        return null;
    }

    //TributeMonsterForSummon
    public GameCard tributeMonster(int amount){
        return null;
    }

    //Man-Eater Bug
    public GameCard directRemove(){
        return null;
    }

    //TexChanger
    public GameCard specialCyberseSummon(){
        return null;
    }

    //HeraldOfCreation
    public GameCard summonFromGraveyard(){
        return null;
    }

    //Beast King Barbaros & Tricky
    public int chooseHowToSummon(){
        return 0;
    }

    //terratiger
    public void selectMonsterToSummon(){

    }

}
