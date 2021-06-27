package edu.sharif.ce.apyugioh.view.model;

import java.util.ArrayList;
import java.util.List;

import edu.sharif.ce.apyugioh.controller.Utils;

public class CardActionsManager {

    private List<CardAction> actions;

    public CardActionsManager() {
        actions = new ArrayList<>();
    }

    public void addAction(CardAction action) {
        actions.add(action);
    }

    public void update(float delta) {
        if (!actions.isEmpty()) {
            float baseAlpha = actions.get(0).getAlpha();
            actions.get(0).update(delta);
            if (Utils.almostEqual(actions.get(0).getAlpha(), 0.99f)) {
                actions.remove(0);
                if (Utils.almostEqual(baseAlpha, delta, 0.01f)) {
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
