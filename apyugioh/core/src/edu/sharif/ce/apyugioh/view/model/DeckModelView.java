package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.card.Card;
import edu.sharif.ce.apyugioh.model.card.CardType;

public class DeckModelView {

    private HashMap<String, CardModelView> cardViews;
    private List<String> cardsToGive;

    public DeckModelView() {
        List<Card> cards = DatabaseManager.getCards().getAllCards();
        cardViews = new HashMap<>();
        Texture backTexture = new Texture(Gdx.files.local("assets/cards/monster/Unknown.jpg"));
        TextureAtlas atlas = new TextureAtlas();
        atlas.addRegion("Unknown", backTexture, 0, 0, 354, 508);
        for (Card card : cards) {
            try {
                Texture frontTexture = new Texture(Gdx.files.local("assets/cards/" +
                        (card.getCardType().equals(CardType.MONSTER) ? "monster" : "spell_trap") + "/" +
                        Utils.firstUpperOnly(card.getName()).replaceAll("\\s+", "") + ".jpg"));
                atlas.addRegion(Utils.firstUpperOnly(card.getName()).replaceAll("\\s+", ""),
                        frontTexture, 0, 0, 421, 614);
                Sprite frontSprite = atlas.createSprite(Utils.firstUpperOnly(card.getName()).replaceAll("\\s+", ""));
                frontSprite.setSize(12, 16);
                Sprite backSprite = atlas.createSprite("Unknown");
                backSprite.setSize(12, 16);
                cardViews.put(Utils.firstUpperOnly(card.getName()), new CardModelView(card.getName(), frontSprite, backSprite));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cardsToGive = new ArrayList<>(cardViews.keySet());
    }

    public CardModelView getCard(String name) {
        return cardViews.get(Utils.firstUpperOnly(name));
    }

    public CardModelView getRandom() {
        String cardName = cardsToGive.get(MathUtils.random(cardsToGive.size() - 1));
        cardsToGive.remove(cardName);
        if (cardsToGive.size() == 0) {
            cardsToGive = new ArrayList<>(cardViews.keySet());
        }
        return cardViews.get(cardName);
    }
}
