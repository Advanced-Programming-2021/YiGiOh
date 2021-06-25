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

    public CardActor(String cardName,CardType cardType,float width,float height,int amount){
        super();
        cardSprite = new Sprite(new Texture(Gdx.files.local("assets/cards/" +
                (cardType.equals(CardType.MONSTER) ? "monster" : "spell_trap") + "/" +
                Utils.firstUpperOnly(cardName.replaceAll("\\s+", "") + ".jpg"))));
        setWidth(width);
        setHeight(height);
        this.amount = amount;
    }

    public CardActor(){
        super();
        cardSprite = new Sprite(new Texture(Gdx.files.local("assets/cards/monster/Unknown.jpg")));
    }

    public static CardActor clone(CardActor cardActor){
        CardActor cloneCard = new CardActor();
        cloneCard.getCardSprite().setTexture(cardActor.getCardSprite().getTexture());
        cloneCard.setWidth(cardActor.getWidth());
        cloneCard.setHeight(cardActor.getHeight());
        return cloneCard;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        cardSprite.setBounds(getX(),getY(),getWidth(),getHeight());
        super.draw(batch, parentAlpha);
        cardSprite.draw(batch, parentAlpha);
        if (amount>1){
            Label amountLabel = new Label(""+amount, AssetController.getSkin("first"),"title");
            amountLabel.getStyle().fontColor = Color.BLACK;
            amountLabel.setPosition(cardSprite.getX()+cardSprite.getWidth()/2f, cardSprite.getY()+20);
            amountLabel.draw(batch,parentAlpha);
        }
    }

    public Sprite getCardSprite() {
        return cardSprite;
    }

}
