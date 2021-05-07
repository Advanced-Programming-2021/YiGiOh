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
    public GameCard[] tributeMonster(int amount){
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

    //EquipMonster
    public void equipMonster(){

    }

    //Select card from graveyard
    public GameCard selectCardFromGraveyard(){
        return null;
    }

    //Select card from monster zone
    public GameCard selectCardFromMonsterZone(){
        return null;
    }

    //Select card from both graveyards
    public GameCard selectCardFromAllGraveyards(){
        return null;
    }

    //Select card from hand
    public GameCard selectCardFromHand(){
        return null;
    }

    //Select card from deck
    public GameCard selectCardFromDeck(){
        return null;
    }

}
