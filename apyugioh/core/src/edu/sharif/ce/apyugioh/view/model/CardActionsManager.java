package edu.sharif.ce.apyugioh.view.model;

import java.util.ArrayList;
import java.util.List;

public class CardActionsManager {

    List<CardAction> actions;

    public CardActionsManager() {
        actions = new ArrayList<>();
    }

    public void addAction(CardAction action) {
        actions.add(action);
    }

    public void update(float delta) {
        if (!actions.isEmpty()) {
            actions.get(0).update(delta);
            if (actions.get(0).getAlpha() > 0.99) {
                actions.remove(0);
            }
        }
    }

    public boolean isDone() {
        return actions.isEmpty();
    }
}
