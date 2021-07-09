package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import edu.sharif.ce.apyugioh.controller.Utils;

public class CameraAction extends GameAction {

    private PerspectiveCamera cam;
    private Vector3 camStartPosition;
    private Vector3 camFinalPosition;
    private final Vector3 camTempPosition;
    private Vector3 camStartDirection;
    private Vector3 camFinalDirection;
    private final Vector3 camTempDirection;
    private Vector3 camStartUp;
    private Vector3 camFinalUp;
    private Vector3 target, lookAt;
    private float speed;

    public CameraAction(PerspectiveCamera cam, Vector3 target, Vector3 lookAt, float speed) {
        this.cam = cam;
        this.target = target;
        this.speed = speed;
        this.lookAt = lookAt;
        camTempPosition = new Vector3();
        camTempDirection = new Vector3();
    }

    @Override
    public void update(float delta) {
        if (alpha == 0 && !onStartCalled) {
            camStartPosition = cam.position.cpy();
            camStartDirection = cam.direction.cpy();
            //camStartUp = cam.up.cpy();
            camFinalPosition = target.cpy();
            cam.position.set(camFinalPosition);
            cam.lookAt(lookAt);
            //cam.rotate(cam.direction, 180);
            camFinalDirection = cam.direction.cpy();
            //camFinalUp = cam.up.cpy();
            //cam.rotate(cam.direction, 180);
            cam.position.set(camStartPosition);
            cam.direction.set(camStartDirection);
            //cam.up.set(camStartUp);
            cam.normalizeUp();
            onStart();
            onStartCalled = true;
        }
        float lastAlpha = alpha;
        alpha += speed * delta;
        if (alpha > 1 || Utils.almostEqual(alpha, 1, 0.01f)) alpha = 1;
        if (Utils.almostEqual(cam.position.x, target.x, 0.01f) && Utils.almostEqual(cam.position.y, target.y, 0.01f) && Utils.almostEqual(cam.position.z, target.z, 0.01f)) {
            alpha = 1;
        }
        camTempPosition.set(camStartPosition);
        camTempPosition.lerp(camFinalPosition, alpha);
        cam.position.set(camTempPosition);
        camTempDirection.set(camStartDirection);
        camTempDirection.lerp(camFinalDirection, alpha).nor();
        cam.direction.set(camTempDirection);
        if (alpha - lastAlpha < 0.8f) {
            cam.rotate(cam.direction, 180 * (alpha - lastAlpha));
        }
        cam.normalizeUp();
        if (Utils.almostEqual(alpha, 1, 0.01f) && !onDoneCalled) {
            cam.position.set(target);
            cam.direction.set(0, 0, -1);
            if (lookAt.y > 0) {
                cam.up.set(0, -1, 0);
            } else {
                cam.up.set(0, 1, 0);
            }
            cam.lookAt(lookAt.x, lookAt.y, lookAt.z);
            onDone();
            onDoneCalled = true;
        }
        cam.update();
    }

    @Override
    public void onDone() {

    }

    @Override
    public void onStart() {

    }
}
