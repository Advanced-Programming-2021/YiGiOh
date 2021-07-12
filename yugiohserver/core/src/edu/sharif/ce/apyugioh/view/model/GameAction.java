package edu.sharif.ce.apyugioh.view.model;

import lombok.Getter;

public abstract class GameAction {

    @Getter
    protected float alpha;
    protected boolean onDoneCalled, onStartCalled;

    public abstract void update(float delta);

    public abstract void onDone();

    public abstract void onStart();
}
