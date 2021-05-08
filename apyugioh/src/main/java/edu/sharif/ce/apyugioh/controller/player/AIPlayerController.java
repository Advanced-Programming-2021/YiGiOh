package edu.sharif.ce.apyugioh.controller.player;

import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.GameCard;

public class AIPlayerController extends PlayerController{

    public AIPlayerController(Player player) {
        super(player);
    }

    public void play() {

    }

    public void standByPhase() {

    }

    public void firstMainPhase() {

    }

    public void battlePhase() {

    }

    public void secondMainPhase() {

    }

    //special Cases

    //TributeMonsterForSummon
    public GameCard[] tributeMonster(int amount){
        return null;
    }

    //Scanner
    public GameCard scanMonsterForScanner(){
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
    public GameCard selectMonsterToSummon(){
        return null;
    }

    //EquipMonster
    public GameCard equipMonster(){
        return null;
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

    @Override
    public boolean confirm(String message) {
        return true;
    }


}
