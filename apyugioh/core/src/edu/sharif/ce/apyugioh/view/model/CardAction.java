package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import edu.sharif.ce.apyugioh.controller.Utils;
import lombok.Getter;

public class CardAction {

    private CardModelView card;
    private Matrix4 target;
    private float speed;
    @Getter
    private float alpha;
    private boolean onDoneCalled, onStartCalled;

    public CardAction(CardModelView card, Vector3 toPosition, float deltaAngle, float speed) {
        this.card = card;
        target = card.getTransform();
        target.setTranslation(toPosition);
        target.rotate(0, 1, 0, deltaAngle);
        this.speed = speed;
        alpha = 0;
    }

    public CardAction(CardModelView card, Matrix4 target, float speed) {
        this.card = card;
        this.target = target;
        this.speed = speed;
    }

    public void update(float delta) {
        if (alpha == 0 && !onStartCalled) {
            onStart();
            onStartCalled = true;
        }
        alpha += speed * delta;
        if (alpha > 1) alpha = 1;
        Vector3 cardPosition = card.getPosition();
        if (Utils.almostEqual(cardPosition.x, target.val[Matrix4.M03]) && Utils.almostEqual(cardPosition.y, target.val[Matrix4.M13]) && Utils.almostEqual(cardPosition.z, target.val[Matrix4.M23])) {
            alpha = 1;
        }
        if (alpha == 1 && !onDoneCalled) {
            onDone();
            onDoneCalled = true;
        }
        card.lerp(target, alpha);
    }

    public void onDone() {

    }

    public void onStart() {

    }

}
