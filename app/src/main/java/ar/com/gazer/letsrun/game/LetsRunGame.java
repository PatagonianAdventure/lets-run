package ar.com.gazer.letsrun.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ar.com.gazer.letsrun.screens.GameScreen;

/**
 * Created by gazer on 8/8/14.
 */
public class LetsRunGame extends Game {

    private GameScreen gameScreen;
    public SpriteBatch batch;
    public SpriteBatch hudBatch;
    public ShapeRenderer shapes;
    public BitmapFont font;

    @Override
    public void create () {
        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();
        shapes = new ShapeRenderer();
        font = new BitmapFont();

        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    @Override
    public void dispose() {
        gameScreen.dispose();
        batch.dispose();
        shapes.dispose();
        super.dispose();
    }
}
