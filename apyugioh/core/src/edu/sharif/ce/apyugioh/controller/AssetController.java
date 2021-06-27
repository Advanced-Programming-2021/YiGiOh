package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.HashMap;
import java.util.Map;

import edu.sharif.ce.apyugioh.view.model.DeckModelView;
import lombok.Getter;

public class AssetController {
    private static HashMap<String, Skin> SKINS;
    private static HashMap<String, Sound> SOUNDS;
    private static HashMap<Long, Sound> currentlyPlayingSounds;
    @Getter
    private static DeckModelView deck;

    public static void loadAssets() {
        SKINS = new HashMap<>();
        SOUNDS = new HashMap<>();
        currentlyPlayingSounds = new HashMap<>();

        SKINS.put("first", new Skin(Gdx.files.internal("skins/first_skin.json")));
        SOUNDS.put("click", Gdx.audio.newSound(Gdx.files.internal("sounds/button_click.mp3")));
        SOUNDS.put("chain", Gdx.audio.newSound(Gdx.files.internal("sounds/chain.mp3")));
        SOUNDS.put("deal", Gdx.audio.newSound(Gdx.files.internal("sounds/card_deal.mp3")));
        SOUNDS.put("flip", Gdx.audio.newSound(Gdx.files.internal("sounds/card_flip.mp3")));
    }

    public static void loadDeck() {
        deck = new DeckModelView();
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

    public static void playSound(String name) {
        if (getSound(name) != null) {
            currentlyPlayingSounds.put(getSound(name).play(), getSound(name));
        }
    }

    public static void stopSound() {
        for (Map.Entry<Long, Sound> sound : currentlyPlayingSounds.entrySet()) {
            sound.getValue().stop(sound.getKey());
        }
        currentlyPlayingSounds.clear();
    }
}
