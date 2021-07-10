package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import lombok.*;

public class CardDetail extends Actor {

    @Setter
    @Getter
    private int xPosition, yPosition;
    @Getter
    @Setter
    private Texture texture;
    @Getter
    private FileHandle fileHandle;

    public CardDetail(Texture texture) {
        this.texture = texture;
        xPosition = 30;
        yPosition = 300;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, xPosition, yPosition);
    }
}
