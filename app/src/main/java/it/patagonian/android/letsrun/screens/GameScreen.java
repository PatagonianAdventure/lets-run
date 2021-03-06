package it.patagonian.android.letsrun.screens;

import android.graphics.Interpolator;
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
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

import it.patagonian.android.letsrun.game.background.ParallaxBackground;
import it.patagonian.android.letsrun.game.background.ParallaxLayer;
import it.patagonian.android.letsrun.renderers.CarRenderer;
import it.patagonian.android.letsrun.game.Car;
import it.patagonian.android.letsrun.LetsRunGame;
import it.patagonian.android.letsrun.game.Terrain;

/**
 * Main game screen.
 *
 * This is the screen where the fun comes to life :).
 *
 * TODO : We need to cleanup this class
 *
 * Created by gazer on 8/9/14.
 */
public class GameScreen extends InputAdapter implements Screen, ContactListener {
    private static final boolean DEBUG = false;
    private static final float ZOOM_MIN = 1f;
    private static final float ZOOM_MAX = 2f;

    private final OrthographicCamera camera;
    private final int height;
    private final int width;
    private final Box2DDebugRenderer renderer;
    private final Preferences prefs;
    private final Sprite spriteGround;
    private final int ppu;
    private float highDistance;
    private LetsRunGame game;
    private World world;
    private Body groundBody;

    private Car car;
    private float lastCameraPosition;

    private float distance = 0;
    private Terrain terrain;
    private CarRenderer carRenderer;

    private ParallaxBackground background;
    private boolean gameOver;

    public GameScreen(LetsRunGame game) {
        this.game = game;

        prefs = Gdx.app.getPreferences("Preferences");

        highDistance = prefs.getFloat("highDistance");

        height = 15;
        ppu = Gdx.graphics.getHeight() / height;
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

        background = new ParallaxBackground(new ParallaxLayer[]{
                new ParallaxLayer(new TextureRegion(new Texture("backgrounds/day_background.png")),new Vector2(5, 5),new Vector2(0, 0)),
                new ParallaxLayer(new TextureRegion(new Texture("backgrounds/clouds1.png")),new Vector2(20, 20),new Vector2(0, 0)),

                //new ParallaxLayer(atlas.findRegion("bg2"),new Vector2(1.0f,1.0f),new Vector2(0, 500)),
                //new ParallaxLayer(atlas.findRegion("bg3"),new Vector2(0.1f,0),new Vector2(0,Constants.HEIGHT-200), new Vector2(0, 0)),

        }, 640, 360,new Vector2(150,10));

        createPhysics();
        createRenderers();
    }

    private void createRenderers() {
        carRenderer = new CarRenderer(car);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (gameOver) {
            return false;
        }

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
        if (gameOver) {
            game.showMenu();
            return false;
        }

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

        if (DEBUG) {
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            carRenderer.render(game.batch);
            game.batch.end();
            renderer.render(world, camera.combined);
        } else {
            background.render(delta);
            /*
             * Set projection matrix to camera.combined, the same way we did with
             * the debug renderer.
             */
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();


            carRenderer.render(game.batch);

            Vector2 v1 = new Vector2();
            Vector2 v2 = new Vector2();
            for (Fixture f : groundBody.getFixtureList()) {
                EdgeShape shape = (EdgeShape) f.getShape();
                shape.getVertex1(v1);
                shape.getVertex2(v2);
                for (float t = 0f; t <= 1; t += 0.1f) {
                    float w = 0.1f; //v2.x - v1.x;
                    float h = v1.y + t * (v2.y - v1.y); //Math.min(v2.y, v1.y);
                    spriteGround.setSize(w, h + 10);
                    spriteGround.setPosition(v1.x + t * (v2.x - v1.x), -10);
                    spriteGround.draw(game.batch);
                }
            }
            game.batch.end();
        }

        game.hudBatch.begin();
        game.font.setColor(Color.RED);
        game.font.setScale(3);
        game.font.draw(game.hudBatch, "FPS : " + Gdx.graphics.getFramesPerSecond(), 30, 90);

        game.font.setColor(Color.WHITE);
        game.font.setScale(3);
        game.font.draw(game.hudBatch, String.format("Velocidad : %.1f", car.getSpeed().len()), 10, Gdx.graphics.getHeight() - game.font.getAscent()*1.25f);

        String str = String.format("Record : %.1f", highDistance);
        BitmapFont.TextBounds size = game.font.getBounds(str);
        game.font.draw(game.hudBatch, str, Gdx.graphics.getWidth() - size.width - 10, Gdx.graphics.getHeight() - game.font.getAscent()*1.25f);

        if (gameOver) {
            str = "GAME OVER";
            game.font.setColor(Color.RED);
            game.font.setScale(6);
            size = game.font.getBounds(str);
            game.font.draw(game.hudBatch, str, Gdx.graphics.getWidth()/2 - size.width / 2, Gdx.graphics.getHeight()/2 - game.font.getAscent()*1.25f);
        }

        game.hudBatch.end();
    }

    private void update(float delta) {
        background.speed = car.getSpeed();

        float v = Math.max(Math.min(car.getSpeed().x, 1), 0);
        float step = Interpolation.exp5In.apply(ZOOM_MIN, ZOOM_MAX, v/10f);
        if (step < ZOOM_MIN) step = ZOOM_MIN;
        if (step > ZOOM_MAX) step = ZOOM_MAX;

        float newHeight = height * step;
        float newWidth = width * step;
        camera.setToOrtho(false, newWidth, newHeight);

        camera.position.x = car.getChassis().getPosition().x + newWidth/4;
        camera.position.y = car.getChassis().getPosition().y;

        float cameraMovement = camera.position.x - lastCameraPosition;
        distance += cameraMovement;
        highDistance = Math.max(distance, highDistance);

        //camera.set
        camera.update();

        // FIX YOUR STEPS!
        world.step(delta, 8, 3);

        lastCameraPosition = camera.position.x;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.BACK) {
            game.showMenu();
        }
        return super.keyUp(keycode);
    }

    private EdgeShape slicePoly;
    private FixtureDef sliceFixture = new FixtureDef();

    private void createPhysics() {
        terrain = new Terrain(500, 261119780);

        world = new World(new Vector2(0, -10), true);
        world.setContactListener(this);

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

        car = new Car(world, fixtureDef, wheelFixtureDef, 5, 9, 4, 1.42f);
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

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        Body bA = a.getBody();
        Body bB = b.getBody();

        if (isGameOver(bA, b) || (isGameOver(bB, a))) {
            gameOver = true;
        }

    }

    boolean isGameOver(Body b, Fixture f) {
        return (b == groundBody) && (f == car.getHitSensor());
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
