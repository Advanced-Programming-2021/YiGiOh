package edu.sharif.ce.apyugioh.view.menu.deckmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.DeckMenuController;
import edu.sharif.ce.apyugioh.model.Deck;
import lombok.Getter;
import lombok.Setter;

class DeckListElement extends Actor {

    @Getter
    @Setter
    private Sprite topCardSprite;
    @Getter
    @Setter
    private Deck deck;
    private Label deckNameLabel;
    private int titleLimit;
    private boolean isSelectable;
    private final float nameLabelHeight = 40f;

    public DeckListElement(String deckName, int userId, float width, float height, int titleLimit) {
        super();
        isSelectable = true;
        setWidth(width);
        setHeight(height);
        this.titleLimit = titleLimit;
        deckNameLabel = new Label(deckName, AssetController.getSkin("first"), "title");
        topCardSprite = new Sprite();
        topCardSprite.setSize(width, height - nameLabelHeight);
        loadTopMostCard();
        setDeck(Deck.getDeckByName(userId, deckName));
    }

    public void loadTopMostCard() {
        topCardSprite = new Sprite(new Texture(Gdx.files.local("assets/cards/monster/Unknown.jpg")));
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
        if (deck == null)
            return;
        deckNameLabel.setText(deck.getName());
        if (deckNameLabel.getText().length > titleLimit) {
            StringBuilder newTitle = new StringBuilder(deckNameLabel.getText());
            newTitle.delete(titleLimit - 3, deckNameLabel.getText().length);
            newTitle.append("...");
            deckNameLabel.setText(newTitle.toString());
        }
    }

    public void setIsSelectable(boolean isSelectable) {
        this.isSelectable = isSelectable;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (deck == null)
            return;
        Color pastColor = deckNameLabel.getStyle().fontColor;
        if (isSelectable) {
            if (deck != null && DeckMenuController.getInstance().getSelectedDeck() != null &&
                    DeckMenuController.getInstance().getSelectedDeck().getId() == deck.getId())
                deckNameLabel.getStyle().fontColor = Color.YELLOW;
            else
                deckNameLabel.getStyle().fontColor = Color.WHITE;
        }
        deckNameLabel.setPosition(getX() + (getWidth() - deckNameLabel.getWidth()) / 2f, getY());
        topCardSprite.setBounds(getX(), getY() + nameLabelHeight, getWidth(), getHeight() - nameLabelHeight);
        super.draw(batch, parentAlpha);
        topCardSprite.draw(batch, parentAlpha);
        deckNameLabel.draw(batch, parentAlpha);
        deckNameLabel.getStyle().fontColor = pastColor;
    }
}
