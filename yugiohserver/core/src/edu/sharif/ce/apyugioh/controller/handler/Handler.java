package edu.sharif.ce.apyugioh.controller.handler;

import com.esotericsoftware.kryonet.Connection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Handler {

    public Object handleObject;
    public Connection connection;
    private Handler nextHandler;

    public Handler() {
    }

    public Handler(Object handleObject, Connection connection) {
        this.handleObject = handleObject;
        this.connection = connection;
    }

    public void handle() {
        if (handleObject == null || connection == null) return;
        if (!handleAction()) {
            if (nextHandler != null) {
                nextHandler.setHandleObject(handleObject);
                nextHandler.setConnection(connection);
                nextHandler.handle();
            }
        }
    }

    public abstract boolean handleAction();

}
