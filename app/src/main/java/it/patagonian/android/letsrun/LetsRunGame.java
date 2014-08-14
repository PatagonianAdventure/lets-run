package it.patagonian.android.letsrun;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import it.patagonian.android.letsrun.screens.GameScreen;
import it.patagonian.android.letsrun.screens.MenuScreen;

/**
 * Created by gazer on 8/8/14.
 */
public class LetsRunGame extends Game {

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

        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        super.dispose();
    }

    public void playSolo() {
        setScreen(new GameScreen(this));
    }
}
