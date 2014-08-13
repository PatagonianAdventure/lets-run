package ar.com.gazer.letsrun.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

/**
 * Created by gazer on 8/10/14.
 */
public class Car {
    private final CircleShape wheelShape;
    private Body chassis, leftWheel, rigthWheel;
    private WheelJoint leftAxis, rightAxis;
    private float motorSpeed = 75f;

    public Car(World world, FixtureDef chassisFixtureDef, FixtureDef wheelFixtureDef, float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // chassis
        PolygonShape chassisShape = new PolygonShape();
        // counterclockwise order
        chassisShape.set(new float[] {-width/2, -height/2, width/2, -height/2, width/2*0.4f, height/2, -width/2*0.8f, height/2*0.8f});

        chassisFixtureDef.shape = chassisShape;

        chassis = world.createBody(bodyDef);
        chassis.createFixture(chassisFixtureDef);

        // left wheel
        wheelShape = new CircleShape();
        wheelShape.setRadius(height/3.5f);

        wheelFixtureDef.shape = wheelShape;

        leftWheel = world.createBody(bodyDef);
        leftWheel.createFixture(wheelFixtureDef);

        // right wheel
        rigthWheel = world.createBody(bodyDef);
        rigthWheel.createFixture(wheelFixtureDef);

        // left axis
        WheelJointDef axisDef = new WheelJointDef();
        axisDef.bodyA = chassis;
        axisDef.bodyB = leftWheel;
        axisDef.localAnchorA.set(-width/2 *0.9f + wheelShape.getRadius(), -height/2 * 1.25f);
        axisDef.frequencyHz = chassisFixtureDef.density;
        axisDef.localAxisA.set(Vector2.Y);
        axisDef.maxMotorTorque = chassisFixtureDef.density * 50;

        leftAxis = (WheelJoint) world.createJoint(axisDef);

        // rigth axis
        axisDef.bodyB = rigthWheel;
        axisDef.localAnchorA.x *= -1;

        rightAxis = (WheelJoint) world.createJoint(axisDef);
    }

    public void accelerate(boolean b) {
        leftAxis.enableMotor(b);
        leftAxis.setMotorSpeed(-motorSpeed);
    }

    public void brake(boolean b) {
        leftAxis.enableMotor(b);
        leftAxis.setMotorSpeed(motorSpeed);
    }

    public Body getChassis() {
        return chassis;
    }

    public void setWheelSprite(Sprite sprite) {
        /*
         * Set the size of the sprite. We have to remember that we are not
         * working in pixels, but with meters. The size of the sprite will be
         * the same as the size of the box; 1 meter wide, 1 meters tall.
         */
        sprite.setSize(wheelShape.getRadius()*2, wheelShape.getRadius()*2);
        //leftWheel.setUserData(sprite);
        //rigthWheel.setUserData(sprite);
    }
}
