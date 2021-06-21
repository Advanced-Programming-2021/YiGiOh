package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.card.Card;

public class CardModelView implements Disposable {

    private CardFrontView cardFront;
    private CardBackView cardBack;
    private String cardName;

    public CardModelView(String cardName, Sprite front, Sprite back) {
        this.cardName = cardName;
        cardFront = new CardFrontView(front);
        cardBack = new CardBackView(back);
    }

    @Override
    public void dispose() {

    }

    public void render(ModelBatch batch, Environment environment) {
        cardFront.environment = environment;
        cardBack.environment = environment;
        batch.render(cardFront);
        batch.render(cardBack);
    }

    public void setTranslation(float x, float y, float z) {
        cardFront.worldTransform.setTranslation(x, y, z);
        cardBack.worldTransform.setTranslation(x, y, z);
    }

    public void translate(float x, float y, float z) {
        cardFront.worldTransform.translate(x, y, z);
        cardBack.worldTransform.translate(x, y, z);
    }

    public void setToRotation(float x, float y, float z, float degrees) {
        cardFront.worldTransform.setToRotation(x, y, z, degrees);
        cardBack.worldTransform.setToRotation(x, y, z, degrees);
    }

    public void rotate(float x, float y, float z, float degrees) {
        cardFront.worldTransform.rotate(x, y, z, degrees);
        cardBack.worldTransform.rotate(x, y, z, degrees);
    }

    public Vector3 getPosition() {
        return new Vector3(cardFront.worldTransform.val[Matrix4.M03], cardFront.worldTransform.val[Matrix4.M13], cardFront.worldTransform.val[Matrix4.M23]);
    }

    public Matrix4 getTransform() {
        return cardFront.worldTransform.cpy();
    }

    public void lerp(Matrix4 target, float alpha) {
        cardFront.worldTransform.lerp(target, alpha);
        cardBack.worldTransform.lerp(target, alpha);
    }

    public Card getCard() {
        return DatabaseManager.getCards().getCardByName(cardName);
    }
}
