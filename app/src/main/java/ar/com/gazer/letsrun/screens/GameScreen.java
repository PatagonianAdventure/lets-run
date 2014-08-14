package ar.com.gazer.letsrun.screens;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import ar.com.gazer.letsrun.Renderers.CarRenderer;
import ar.com.gazer.letsrun.game.Car;
import ar.com.gazer.letsrun.LetsRunGame;
import ar.com.gazer.letsrun.game.Terrain;

/**
 * Main game screen.
 *
 * This is the screen where the fun comes to life :).
 *
 * TODO : We need to cleanup this class
 *
 * Created by gazer on 8/9/14.
 */
public class GameScreen extends InputAdapter implements Screen {
    private final OrthographicCamera camera;
    private final int height;
    private final int width;
    private final Box2DDebugRenderer renderer;
    private final Preferences prefs;
    private final Sprite spriteGround;
    private float highDistance;
    private LetsRunGame game;
    private World world;
    private Body groundBody;

    private Car car;
    private float lastCameraPosition;

    private float distance = 0;
    private Terrain terrain;
    private CarRenderer carRenderer;

    public GameScreen(LetsRunGame game) {
        this.game = game;

        prefs = Gdx.app.getPreferences("Preferences");

        highDistance = prefs.getFloat("highDistance");

        height = 20;
        float ppu = Gdx.graphics.getHeight() / height;
        width = (int) Math.ceil(Gdx.graphics.getWidth() / ppu);

        lastCameraPosition = width/4f;

        renderer = new Box2DDebugRenderer();

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, width, height);
        camera.position.set(width / 2, height / 2 - 1, 0);
        camera.update();

    	/* Set box texture */
        spriteGround = new Sprite(new Texture("ground.png"));

        createPhysics();
        createRenderers();
    }

    private void createRenderers() {
        carRenderer = new CarRenderer(car);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Log.d("Game", "touch down " + screenX + " - " + Gdx.graphics.getWidth());

        if (screenX > Gdx.graphics.getWidth()/2) {
            // Foward - W
            car.accelerate(true);
        } else {
            // Backwards - S
            car.brake(true);
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (screenX > Gdx.graphics.getWidth()/2) {
            // Foward - W
            car.accelerate(false);
        } else {
            // Backwards - S
            car.brake(false);
        }

        return true;
    }

    @Override
    public void render(float delta) {
        update(Gdx.graphics.getDeltaTime());

        Gdx.gl.glClearColor( 0.2f, 0.2f, 0.2f, 1f );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // renderer.render(world, camera.combined);

        /*
         * Set projection matrix to camera.combined, the same way we did with
         * the debug renderer.
         */
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        carRenderer.render(game.batch);

        Vector2 v1 = new Vector2();
        Vector2 v2 = new Vector2();
        for(Fixture f : groundBody.getFixtureList()) {
            EdgeShape shape = (EdgeShape) f.getShape();
            shape.getVertex1(v1);
            shape.getVertex2(v2);
            float w = v2.x - v1.x;
            float h = Math.min(v2.y, v1.y);
            spriteGround.setSize(w, h);
            spriteGround.setPosition(v1.x, 0);
            spriteGround.draw(game.batch);

        }
        game.batch.end();

        game.hudBatch.begin();
        game.font.setColor(Color.RED);
        game.font.setScale(3);
        game.font.draw(game.hudBatch, "FPS : " + Gdx.graphics.getFramesPerSecond(), 30, 90);

        game.font.setColor(Color.WHITE);
        game.font.setScale(3);
        game.font.draw(game.hudBatch, String.format("Distancia : %.1f", distance), 10, Gdx.graphics.getHeight() - game.font.getAscent()*1.25f);

        String str = String.format("Record : %.1f", highDistance);
        BitmapFont.TextBounds size = game.font.getBounds(str);
        game.font.draw(game.hudBatch, str, Gdx.graphics.getWidth() - size.width - 10, Gdx.graphics.getHeight() - game.font.getAscent()*1.25f);
        game.hudBatch.end();
    }

    private void update(float delta) {

        camera.position.x = car.getChassis().getPosition().x + width/4;
        float cameraMovement = camera.position.x - lastCameraPosition;
        distance += cameraMovement;
        highDistance = Math.max(distance, highDistance);

        camera.update();

        // FIX YOUR STEPS!
        world.step(delta, 8, 3);

        lastCameraPosition = camera.position.x;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK) {
            Gdx.app.exit();
        }
        return super.keyUp(keycode);
    }

    private EdgeShape slicePoly;
    private FixtureDef sliceFixture = new FixtureDef();

    private void createPhysics() {
        terrain = new Terrain(500, 261119780);

        world = new World(new Vector2(0, -10), true);
        BodyDef bodyDef = new BodyDef();
        groundBody = world.createBody(bodyDef);
        slicePoly = new EdgeShape();

        for (int x = 0; x < terrain.getCount(); x += 1)
            createSlice(x);

        FixtureDef fixtureDef = new FixtureDef();
        FixtureDef wheelFixtureDef = new FixtureDef();

        fixtureDef.density = 5;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = .3f;

        wheelFixtureDef.density = fixtureDef.density * 1.5f;
        wheelFixtureDef.friction = 50;
        wheelFixtureDef.restitution = 0.4f;

        car = new Car(world, fixtureDef, wheelFixtureDef, 5, 3, 4, 1.42f);
    }

    private float nextY = -1;

    private void createSlice(int x) {
        float y = nextY;
        if (y < 0) {
            y = terrain.getHeightAt(x);
        }
        nextY = terrain.getHeightAt(x);
        slicePoly.set(x, y, x + 1, nextY);
        sliceFixture.shape = slicePoly;
        groundBody.createFixture(sliceFixture);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void hide() {
        prefs.putFloat("highDistance", highDistance);
        prefs.flush();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        slicePoly.dispose();
        world.dispose();
    }
}
