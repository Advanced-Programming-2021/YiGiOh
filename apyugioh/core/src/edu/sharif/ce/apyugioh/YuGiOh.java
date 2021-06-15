package edu.sharif.ce.apyugioh;

import com.badlogic.gdx.Game;

import edu.sharif.ce.apyugioh.controller.ProgramController;

public class YuGiOh extends Game {

    @Override
    public void create() {
        ProgramController.getInstance().initialize(this);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
    }
}
