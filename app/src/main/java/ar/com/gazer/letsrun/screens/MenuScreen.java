package ar.com.gazer.letsrun.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import ar.com.gazer.letsrun.LetsRunGame;

/**
 * Show the main menu and buttons.
 *
 * Created by gazer on 8/13/14.
 */
public class MenuScreen implements Screen {
    private final LetsRunGame game;
    private final Stage stage;

    public MenuScreen(final LetsRunGame game) {
        this.game = game;

        Skin skin = new Skin(Gdx.files.internal("menuSkin/uiskin.json"));

        stage = new Stage(new ExtendViewport(640, 480));

        Table table = new Table(skin);
        table.setFillParent(true);
        table.defaults().pad(5).space(0);
        table.setWidth(480);
        table.row();

        TextButton ranking = new TextButton("Play Solo", skin);
        ranking.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.playSolo();
            }
        });

        table.add(ranking).height(60).fill().uniform().width(480);
        table.row();
        table.row();

        TextButton host = new TextButton("Host a Game", skin);
        table.add(host).height(60).fill().uniform();
        table.row();

        TextButton join = new TextButton("Join a Game", skin);
        table.add(join).height(60).fill().uniform();
        table.row();

        stage.addActor(table);

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0.2f, 0.2f, 0.2f, 1f );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        Table.drawDebug(stage);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
