package edu.sharif.ce.apyugioh.controller.player;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;

import lombok.Getter;
import lombok.Setter;

public abstract class ConfirmationAction implements Callable<Boolean> {

    @Setter
    @Getter
    public ArrayBlockingQueue<Boolean> choice;
}