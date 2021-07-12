package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.CardType;
import lombok.Getter;
import lombok.Setter;

public class CardActor extends Actor {

    @Getter
    @Setter
    private int amount;
    private Sprite cardSprite;
    private Card card;

    public CardActor(Card card,float width,float height,int amount){
        super();
        this.card = card;
        cardSprite = new Sprite(new Texture(Gdx.files.local("assets/cards/" +
                (card.getCardType().equals(CardType.MONSTER) ? "monster" : "spell_trap") + "/" +
                Utils.firstUpperOnly(card.getName().replaceAll("\\s+", "") + ".jpg"))));
        setWidth(width);
        setHeight(height);
        this.amount = amount;
    }

    public CardActor(){
        super();
        amount = 1;
        cardSprite = new Sprite(new Texture(Gdx.files.local("assets/cards/monster/Unknown.jpg")));
    }

    public static CardActor clone(CardActor cardActor){
        CardActor cloneCard = new CardActor();
        cloneCard.getCardSprite().setTexture(cardActor.getCardSprite().getTexture());
        cloneCard.setWidth(cardActor.getWidth());
        cloneCard.setHeight(cardActor.getHeight());
        cloneCard.setCard(cardActor.getCard());
        cloneCard.setAmount(1);
        return cloneCard;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Sprite getCardSprite() {
        return cardSprite;
    }

    public void changeAmount(int delta){
        amount += delta;
        if (amount<0)
            amount = 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        cardSprite.setBounds(getX(),getY(),getWidth(),getHeight());
        super.draw(batch, parentAlpha);
        cardSprite.draw(batch, parentAlpha);
        if (amount!=1){
            Label amountLabel = new Label(""+amount, AssetController.getSkin("first"),"title");
            Color lastColor = amountLabel.getStyle().fontColor;
            amountLabel.getStyle().fontColor = Color.BLACK;
            amountLabel.setPosition(cardSprite.getX()+cardSprite.getWidth()/2f, cardSprite.getY()+20);
            amountLabel.draw(batch,parentAlpha);
            amountLabel.getStyle().fontColor = lastColor;
        }
    }

}
