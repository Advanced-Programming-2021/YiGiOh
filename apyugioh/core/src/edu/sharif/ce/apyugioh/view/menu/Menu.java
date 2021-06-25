package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.view.View;

public class Menu extends View implements Screen {

    protected final YuGiOh game;
    protected PerspectiveCamera cam;
    protected ModelBatch modelBatch;
    protected AssetManager assets;
    protected Array<ModelInstance> instances = new Array<>();
    protected Environment environment;
    protected Viewport viewport;
    protected int lastX, lastY;
    private Vector3 camPos;
    protected boolean moveCamera = true;

    public Menu(YuGiOh game) {
        this.game = game;
        modelBatch = new ModelBatch();
        environment = new Environment();
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        viewport = new FitViewport(cam.viewportWidth, cam.viewportHeight, cam);
        cam.position.set(0f, 0f, 0f);
        cam.lookAt(20, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        assets = new AssetManager();
        lastX = Gdx.graphics.getWidth() / 2;
        lastY = Gdx.graphics.getHeight() / 2;
        camPos = Vector3.Zero;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if (moveCamera) {
            if (Math.abs(Gdx.input.getX() - lastX) > 5 || Math.abs(Gdx.input.getY() - lastY) > 5) {
                lastX = Gdx.input.getX();
                lastY = Gdx.input.getY();
                changeCameraLocation(lastX - Gdx.graphics.getWidth() / 2, lastY - Gdx.graphics.getHeight() / 2);
                cam.update();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        cam.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
    }

    private void changeCameraLocation(float deltaX, float deltaY) {
        if (Gdx.graphics.getWidth() < 2 || Gdx.graphics.getHeight() < 2) {
            return;
        }
        deltaX /= Gdx.graphics.getWidth();
        deltaY /= Gdx.graphics.getHeight();
        deltaX *= 2;
        deltaY *= 2;
        if (Math.abs(deltaX) < 1 && Math.abs(deltaY) < 1) {
            float z = (float) (Math.sqrt(2) - Math.sqrt(2 - deltaX * deltaX - deltaY * deltaY));
            camPos.y = deltaY;
            camPos.z = -deltaX;
            camPos.x = -z;
            cam.position.lerp(camPos, 1);
        }
    }
}
