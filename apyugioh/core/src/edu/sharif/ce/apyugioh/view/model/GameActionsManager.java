package edu.sharif.ce.apyugioh.view.model;

import java.util.ArrayList;
import java.util.List;

import edu.sharif.ce.apyugioh.controller.Utils;

public class GameActionsManager {

    private final List<GameAction> actions;
    private boolean isRecursive;

    public GameActionsManager() {
        actions = new ArrayList<>();
        isRecursive = true;
    }

    public GameActionsManager(boolean isRecursive) {
        this();
        this.isRecursive = isRecursive;
    }

    public void addAction(GameAction action) {
        actions.add(action);
    }

    public void update(float delta) {
        if (!actions.isEmpty()) {
            float baseAlpha = actions.get(0).getAlpha();
            actions.get(0).update(delta);
            if (Utils.almostEqual(actions.get(0).getAlpha(), 1, 0.01f)) {
                actions.remove(0);
                if (isRecursive) {
                    update(delta);
                }
            }
        }
    }

    public boolean isDone() {
        return actions.isEmpty();
    }

    public void clear() {
        actions.clear();
    }
}
