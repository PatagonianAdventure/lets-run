package it.patagonian.android.letsrun.screens;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import it.patagonian.android.letsrun.game.FirstHitGroundRayCastCallback;
import it.patagonian.android.letsrun.game.MyContactListener;
import it.patagonian.android.letsrun.game.SmokeParticle;
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
public class GameScreen extends InputAdapter implements Screen {
    private static final boolean DEBUG = true;
    private static final float threshold = 1;
    private static final int MAX_PARTICLES = 100;

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

    private ParallaxBackground background;
    private MyContactListener contactListener;
    private int nextParticleIndex;

    Random random = new Random();

    public GameScreen(LetsRunGame game) {
        this.game = game;

        prefs = Gdx.app.getPreferences("Preferences");

        highDistance = prefs.getFloat("highDistance");

        height = 12;
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

        if (DEBUG) {
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
        game.font.draw(game.hudBatch, String.format("Distancia : %.1f", distance), 10, Gdx.graphics.getHeight() - game.font.getAscent()*1.25f);

        String str = String.format("Record : %.1f", highDistance);
        BitmapFont.TextBounds size = game.font.getBounds(str);
        game.font.draw(game.hudBatch, str, Gdx.graphics.getWidth() - size.width - 10, Gdx.graphics.getHeight() - game.font.getAscent()*1.25f);
        game.hudBatch.end();
    }

    private void update(float delta) {
        background.speed = car.getSpeed();

        camera.position.x = car.getChassis().getPosition().x + width/4;
        camera.position.y = car.getChassis().getPosition().y;
        /*if (camera.position.y < -height/4) {
            camera.position.y = -height/4;
        }*/
        float cameraMovement = camera.position.x - lastCameraPosition;
        distance += cameraMovement;
        highDistance = Math.max(distance, highDistance);

        camera.update();


        // FIX YOUR STEPS!
        world.step(delta, 8, 3);

        decreaseParticleLife(m_smokeParticles, 0.01f);
        calculateParticles();

        lastCameraPosition = camera.position.x;

    }

    private void decreaseParticleLife(ArrayList<SmokeParticle> whichSet, float v) {
        ArrayList<SmokeParticle> toRemove = new ArrayList<SmokeParticle>();
        for (SmokeParticle p : whichSet) {
            p.life -= v;
            if (p.life < 0) {
                particles.add(p);
                toRemove.add(p);
            }
        }
        whichSet.removeAll(toRemove);
    }

    private void calculateParticles() {
        Iterator<Contact> iterator = contactListener.getSteelToConcreteContacts().iterator();
        while (iterator.hasNext()) {
            Contact contact = iterator.next();

            if (contact.getWorldManifold().getPoints().length < 1) {
                continue;
            }

            Fixture fA = contact.getFixtureA();
            Fixture fB = contact.getFixtureB();
            Body bA = fA.getBody();
            Body bB = fB.getBody();

            // get the contact point in world coordinates
            Vector2 worldPoint = contact.getWorldManifold().getPoints()[0];

            // find the relative speed of the fixtures at that point
            Vector2 velA = bA.getLinearVelocityFromWorldPoint(worldPoint);
            Vector2 velB = bB.getLinearVelocityFromWorldPoint(worldPoint);
            float relativeSpeed = velA.sub(velB).len();

            // overall friction of contact
            float totalFriction = fA.getFriction() * fB.getFriction();

            // check if this speed and friction is enough to generate particles
            float intensity = relativeSpeed * totalFriction;
            if ( intensity > threshold )
                spawnParticle(0, worldPoint, velA, velB, intensity);
        }
    }

    float heightOfGroundAt(float x)
    {
        Vector2 rayStart = new Vector2( x, 1000 );
        Vector2 rayEnd = new Vector2(x, -1000);

        FirstHitGroundRayCastCallback callback = new FirstHitGroundRayCastCallback();
        world.rayCast(callback, rayStart, rayEnd);
        if (callback.m_fixture == null)
            return -1000;
        return callback.m_point.y;
    }

    SmokeParticle createParticle()
    {
        SmokeParticle p = new SmokeParticle();
        p.life = 1;

        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.fixedRotation = true;
        p.body = world.createBody(bd);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(.01f);

        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.filter.categoryBits = 2;
        fd.filter.maskBits = 1;
        fd.shape = circleShape;
        p.body.createFixture(fd);

        return p;
    }

    ArrayList<SmokeParticle> particles = new ArrayList<SmokeParticle>();
    ArrayList<SmokeParticle> m_smokeParticles = new ArrayList<SmokeParticle>();
    void spawnParticle(int particleType, Vector2 worldPoint, Vector2 velA, Vector2 velB, float intensity)
    {
        // first, figure out if we should make a new particle, or recycle one
        int currentParticleIndex = nextParticleIndex;
        if ( particles.size() < MAX_PARTICLES ) {
            //create a new particle
            particles.add(createParticle());
            nextParticleIndex++;
        } else {
            //recycle oldest existing particle
            currentParticleIndex %= MAX_PARTICLES;
            nextParticleIndex = (nextParticleIndex+1) % MAX_PARTICLES;
        }

        // get a reference to the particle to be (re)positioned
        SmokeParticle p = particles.get(currentParticleIndex);
        Body b = p.body;
        Array<Fixture> f = b.getFixtureList();

        // set the starting life (in this example, this is used for opacity)
        p.life = intensity * 0.3f;
        p.life = Math.min(1.0f, Math.max(0f, p.life));

        // set the starting position
        b.setTransform(new Vector2(worldPoint.x, heightOfGroundAt(worldPoint.x) + 0.05f), 0);

        // Adding the velocities of the two fixtures gives an average movement.
        // Depending on what kind of particles are being generated, you might
        // want to bias the starting velocity toward one of the fixtures involved.
        Vector2 vel = velA.add(velB);

        // set the appropriate properties, and put the particle in the appropriate list
        if ( particleType == 0 ) {
            vel.scl(0.1f);
            vel.x += -2 + 4*random.nextFloat();
            vel.y += -2 + 4*random.nextFloat();
            b.setLinearVelocity(vel);
            b.setGravityScale(0);
            b.setLinearDamping(4);
            f.get(0).setRestitution(0.1f);
            f.get(0).setFriction(2);

            m_smokeParticles.add(p);
            //m_sparkParticles.erase(p);
            //m_dirtParticles.erase(p);
        }/*
        else if ( particleType == PT_SPARK ) {
            vel.x = RandomFloat(-3,3);
            vel.y = RandomFloat(-3,3);
            b->SetLinearVelocity( vel );
            b->SetGravityScale(0.5);
            b->SetLinearDamping(1);
            f->SetRestitution(0.8);
            f->SetFriction(2);

            m_smokeParticles.erase(p);
            m_sparkParticles.insert(p);
            m_dirtParticles.erase(p);
        }
        else if ( particleType == PT_DIRT ) {
            vel *= 0.3;
            vel.x += RandomFloat(-2,2);
            vel.y += RandomFloat(-2,2);
            b->SetLinearVelocity( vel );
            b->SetGravityScale(1);
            b->SetLinearDamping(0.1);
            f->SetRestitution(0.2);
            f->SetFriction(2);

            m_smokeParticles.erase(p);
            m_sparkParticles.erase(p);
            m_dirtParticles.insert(p);
        }*/
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
        contactListener = new MyContactListener();
        world.setContactListener(contactListener);

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
        fixtureDef.filter.maskBits = (short) 65533;

        wheelFixtureDef.density = fixtureDef.density * 1.5f;
        wheelFixtureDef.friction = 50;
        wheelFixtureDef.restitution = 0.4f;
        wheelFixtureDef.filter.maskBits = (short) 65533;

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
}
