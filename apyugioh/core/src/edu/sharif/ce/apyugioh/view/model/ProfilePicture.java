package edu.sharif.ce.apyugioh.view.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ProfileController;
import lombok.Getter;
import lombok.Setter;

public class ProfilePicture extends Actor {

    private static final int SIZE;

    static {
        SIZE = 90;
    }

    @Getter
    private FileHandle pictureFile;
    @Getter
    private Pixmap pixmap;
    @Getter
    private Texture texture;
    private boolean isRounded;
    @Setter
    @Getter
    private int xPosition, yPosition;

    public ProfilePicture(FileHandle fileHandle, boolean isRounded) {
        this.pictureFile = fileHandle;
        this.isRounded = isRounded;
        initializePixmap();
        xPosition = 219;
        yPosition = Gdx.graphics.getHeight() - 186;
    }

    private void initializePixmap() {
        pixmap = new Pixmap(pictureFile);
        if (!isRounded) {
            roundPixmap();
            isRounded = true;
        }
        texture = new Texture(pixmap);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, xPosition, yPosition);
    }

    private void roundPixmap() {
        int width = pixmap.getWidth();
        int height = pixmap.getHeight();
        if(width != height)
        {
            for(int y=0;y<Math.max(width, height);y++)
            {
                for(int x=0;x<Math.max(width, height);x++)
                {
                    //check if pixel is outside circle. Set pixel to transparant;
                    if(y >= height || x >= width) {
                        pixmap.drawPixel(x, y, 100);
                    }
                    else {
                        pixmap.drawPixel(x, y, pixmap.getPixel(x, y));
                    }
                }
            }
        }
        resizePixmap();
        Pixmap round = new Pixmap(pixmap.getWidth(), pixmap.getHeight(),Pixmap.Format.RGBA8888);
        double radius = SIZE/2.0;
        for(int y=0;y<SIZE;y++)
        {
            for(int x=0;x<SIZE;x++)
            {
                //check if pixel is outside circle. Set pixel to transparant;
                double dist_x = (radius - x);
                double dist_y = (radius - y);
                double dist = Math.sqrt((dist_x*dist_x) + (dist_y*dist_y));
                if(dist < radius)
                {
                    round.drawPixel(x, y, pixmap.getPixel(x, y));
                }
                else
                    round.drawPixel(x, y, 0);
            }
        }
        Gdx.app.log("info", "pixmal rounded!");
        pixmap = round;
        PixmapIO.writePNG(new FileHandle("assets/db/profiles/" + ProfileController.getInstance().getUser().getUsername() + ".png"), pixmap);
        ProfileController.getInstance().getUser().setAvatar(ProfileController.getInstance().getUser().getUsername() + ".png");
    }

    private void resizePixmap() {
        Pixmap resizedPixmap = new Pixmap(SIZE, SIZE, pixmap.getFormat());
        resizedPixmap.drawPixmap(pixmap,
                0, 0, pixmap.getWidth(), pixmap.getHeight(),
                0, 0, resizedPixmap.getWidth(), resizedPixmap.getHeight());
        pixmap = resizedPixmap;
    }

    public void setProfilePicture(FileHandle fileHandle, boolean isRounded) {
        if (isRounded) {
            this.pictureFile = fileHandle;
            initializePixmap();
            return;
        }
        this.pictureFile = fileHandle;
        this.isRounded = false;
        initializePixmap();
    }
}
