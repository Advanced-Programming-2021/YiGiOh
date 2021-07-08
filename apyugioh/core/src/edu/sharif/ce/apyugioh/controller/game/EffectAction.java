package edu.sharif.ce.apyugioh.controller.game;

import edu.sharif.ce.apyugioh.model.EffectResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

public abstract class EffectAction implements Callable<EffectResponse> {

    @Getter
    @Setter
    ArrayBlockingQueue<EffectResponse> result;
}
