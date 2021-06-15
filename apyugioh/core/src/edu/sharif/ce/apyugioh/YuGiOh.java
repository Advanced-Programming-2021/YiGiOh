package edu.sharif.ce.apyugioh;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

import edu.sharif.ce.apyugioh.controller.ProgramController;

public class YuGiOh extends Game {

    @Getter
    @Setter
    private HashMap<String, Skin> skin;

    @Override
    public void create() {
        skin = new HashMap<>();
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
