package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import lombok.Getter;

public class CardAction {

    private CardModelView card;
    private Matrix4 target;
    private float deltaAngle, speed;
    @Getter
    private float alpha;
    private boolean onDoneCalled, onStartCalled;

    public CardAction(CardModelView card, Vector3 toPosition, float deltaAngle, float speed) {
        this.card = card;
        target = card.getTransform();
        target.setTranslation(toPosition);
        target.rotate(0, 1, 0, deltaAngle);
        this.deltaAngle = deltaAngle;
        this.speed = speed;
        alpha = 0;
    }

    public void update(float delta) {
        if (alpha == 0 && !onStartCalled) {
            onStart();
            onStartCalled = true;
        }
        alpha += speed * delta;
        if (alpha > 1) alpha = 1;
        Vector3 cardPosition = card.getPosition();
        if (almostEqual(cardPosition.x, target.val[Matrix4.M03]) && almostEqual(cardPosition.y, target.val[Matrix4.M13]) && almostEqual(cardPosition.z, target.val[Matrix4.M23])) {
            alpha = 1;
        }
        if (alpha == 1 && !onDoneCalled) {
            onDone();
            onDoneCalled = true;
        }
        card.lerp(target, alpha);
    }

    private boolean almostEqual(float a, float b) {
        return Math.abs(a - b) < 0.1f;
    }

    public void onDone() {

    }

    public void onStart() {

    }

}
