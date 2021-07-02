package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import edu.sharif.ce.apyugioh.controller.Utils;

public class CardAction extends GameAction {

    private CardModelView card;
    private Matrix4 start, temp, target;
    private float speed;

    public CardAction(CardModelView card, Vector3 toPosition, float deltaAngle, float speed) {
        this.card = card;
        target = card.getTransform();
        target.setTranslation(toPosition);
        target.rotate(0, 1, 0, deltaAngle);
        this.speed = speed;
        alpha = 0;
        temp = new Matrix4();
    }

    public CardAction(CardModelView card, Matrix4 target, float speed) {
        this.card = card;
        this.target = target;
        this.speed = speed;
        temp = new Matrix4();
    }

    public void update(float delta) {
        if (alpha == 0 && !onStartCalled) {
            onStart();
            onStartCalled = true;
            start = card.getTransform();
        }
        alpha += speed * delta;
        if (alpha > 1) alpha = 1;
        Vector3 cardPosition = card.getPosition();
        if (Utils.almostEqual(cardPosition.x, target.val[Matrix4.M03], 0.01f) && Utils.almostEqual(cardPosition.y, target.val[Matrix4.M13], 0.01f) && Utils.almostEqual(cardPosition.z, target.val[Matrix4.M23], 0.01f)) {
            alpha = 1;
        }
        if (Utils.almostEqual(alpha, 1, 0.01f) && !onDoneCalled) {
            onDone();
            onDoneCalled = true;
            alpha = 1;
        }
        temp.set(start);
        temp.lerp(target, alpha);
        card.set(temp);
        //card.lerp(target, alpha);
    }

    public void onDone() {

    }

    public void onStart() {

    }

}
