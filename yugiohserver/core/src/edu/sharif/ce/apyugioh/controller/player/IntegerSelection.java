package edu.sharif.ce.apyugioh.controller.player;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

public abstract class IntegerSelection implements Callable<Integer> {

    @Getter
    @Setter
    ArrayBlockingQueue<Integer> choice;
}
