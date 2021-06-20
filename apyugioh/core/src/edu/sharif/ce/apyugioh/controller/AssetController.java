package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;

public class AssetController {
    private static HashMap<String, Skin> SKINS;
    private static HashMap<String, Sound> SOUNDS;

    public static void loadAssets() {
        SKINS = new HashMap<>();
        SOUNDS = new HashMap<>();

        SKINS.put("first", new Skin(Gdx.files.internal("skins/first_skin.json")));
        SOUNDS.put("click", Gdx.audio.newSound(Gdx.files.internal("sounds/button_click.mp3")));
        SOUNDS.put("chain", Gdx.audio.newSound(Gdx.files.internal("sounds/chain.mp3")));
    }

    public static void addSkin(String name, Skin skin) {
        SKINS.put(name, skin);
    }

    public static Skin getSkin(String name) {
        return SKINS.get(name);
    }

    public static void addSound(String name, Sound sound) {
        SOUNDS.put(name, sound);
    }

    public static Sound getSound(String name) {
        return SOUNDS.get(name);
    }
}
