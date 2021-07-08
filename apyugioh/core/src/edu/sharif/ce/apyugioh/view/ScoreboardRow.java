package edu.sharif.ce.apyugioh.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import edu.sharif.ce.apyugioh.controller.AssetController;

public class ScoreboardRow extends Actor {

    final int rank;
    final String username;
    private int row;
    int y;
    final int score;
    final boolean isMe;
    private Rectangle rectangle;
    private Table table;
    private Label rankLabel, usernameLabel, scoreLabel;

    public ScoreboardRow(int rank, String username,int row, int score, boolean isMe, Skin skin) {
        this.rank = rank;
        this.username = username;
        this.row = row;
        y = Gdx.graphics.getHeight() - 150 - row * 90;
        this.score = score;
        this.isMe = isMe;
        rectangle = new Rectangle();
        rectangle.width = 1080;
        rectangle.height = 80;
        rankLabel = new Label(rank + "", skin);
        rankLabel.setAlignment(Align.center);
        usernameLabel = new Label(username, skin);
        usernameLabel.setAlignment(Align.center);
        scoreLabel = new Label(score + "", skin);
        scoreLabel.setAlignment(Align.center);
        if (isMe) {
            rankLabel.setColor(Color.GOLD);
            usernameLabel.setColor(Color.GOLD);
            scoreLabel.setColor(Color.GOLD);
        }
        table = new Table();
        table.center();
        table.add(rankLabel).width(360).height(80);
        table.add(usernameLabel).width(360).height(80);
        table.add(scoreLabel).width(360).height(80);
        table.setWidth(1080);
        table.setHeight(80);
        table.setX(420);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(AssetController.getScoreboardRow(), 420, y, 1080, 80);
        table.setY(y);
        table.act(Gdx.graphics.getDeltaTime());
        table.draw(batch, parentAlpha);
    }

}
