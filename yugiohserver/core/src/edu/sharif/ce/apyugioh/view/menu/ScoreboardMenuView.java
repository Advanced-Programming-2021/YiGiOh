package edu.sharif.ce.apyugioh.view.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import edu.sharif.ce.apyugioh.YuGiOh;
import edu.sharif.ce.apyugioh.controller.AssetController;
import edu.sharif.ce.apyugioh.controller.MainMenuController;
import edu.sharif.ce.apyugioh.controller.ScoreboardController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ScoreboardRow;

import java.util.List;
import java.util.Map;

public class ScoreboardMenuView extends Menu {


    private Texture row;
    private SpriteBatch batch;
    private Stage stage;
    private Texture backgroundTexture;

    public ScoreboardMenuView(YuGiOh game) {

        super(game);
        stage = new Stage();
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("backgrounds/main" + 10 + ".jpg"));

    }

    @Override
    public void show() {
        super.show();
        String username = ScoreboardController.getInstance().getUser().getUsername();
        List<Map.Entry<String, Integer>> results = DatabaseManager.getScoreboard();
        Label title = new Label("Scoreboard", AssetController.getSkin("first"), "title");
        Table table = prepareTable(username, results, title);
        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    private Table prepareTable(String username, List<Map.Entry<String, Integer>> results, Label title) {
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(title).center().padTop(70);
        int lastScore = -1;
        int scoreCounter = 0;
        for (int i = 0; i < results.size(); i++) {
            table.row();
            if (results.get(i).getValue().equals(lastScore)) scoreCounter++;
            else {
                scoreCounter = 0;
                lastScore = results.get(i).getValue();
            }
            table.add(new ScoreboardRow(i + 1 - scoreCounter, results.get(i).getKey(),
                    i + 1,
                    results.get(i).getValue(),
                    results.get(i).getKey().equals(username),
                    AssetController.getSkin("first"))).expand().fill().center();
        }
        table.padBottom(30);
        return table;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            MainMenuController.getInstance().showMainMenu();
        }

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        stage.dispose();
    }
}
