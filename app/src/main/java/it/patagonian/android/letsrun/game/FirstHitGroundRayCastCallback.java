package it.patagonian.android.letsrun.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

/**
 * Created by gazer on 8/24/14.
 */
public class FirstHitGroundRayCastCallback implements RayCastCallback {
    public Vector2 m_point;
    public Fixture m_fixture;

    @Override
    public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
        if (fixture.getBody().getType() != BodyDef.BodyType.StaticBody)
            return -1;

        m_point = point;
        m_fixture = fixture;
        return fraction;
    }
}
