package it.patagonian.android.letsrun.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.WheelJoint;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;

/**
 * Created by gazer on 8/10/14.
 */
public class Car {
    private final CircleShape wheelShape;
    private final PolygonShape chassisShape;
    private final float width;
    private final float height;
    private final Fixture hitBox;
    private Body chassis, leftWheel, rightWheel;
    private WheelJoint leftAxis, rightAxis;
    private float motorSpeed = 40f;

    public Car(World world, FixtureDef chassisFixtureDef, FixtureDef wheelFixtureDef, float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        chassis = world.createBody(bodyDef);

        // chassis
        chassisShape = new PolygonShape();
        chassisFixtureDef.shape = chassisShape;

        chassisShape.set(p1);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p2);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p3);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p4);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p5);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p6);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p7);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p8);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p9);
        chassis.createFixture(chassisFixtureDef);
        chassisShape.set(p10);
        chassis.createFixture(chassisFixtureDef);

        // hitBox
        chassisShape.setAsBox(width/3, height/20, new Vector2(0, height/2), 0);
        chassisFixtureDef.shape = chassisShape;
        float d = chassisFixtureDef.density;
        chassisFixtureDef.density = 0.1f;
        hitBox = chassis.createFixture(chassisFixtureDef);
        hitBox.setSensor(true);
        chassisFixtureDef.density = d;

        // left wheel
        wheelShape = new CircleShape();
        wheelShape.setRadius(height/3.5f);

        wheelFixtureDef.shape = wheelShape;

        leftWheel = world.createBody(bodyDef);
        leftWheel.createFixture(wheelFixtureDef);

        // right wheel
        rightWheel = world.createBody(bodyDef);
        rightWheel.createFixture(wheelFixtureDef);

        // left axis
        WheelJointDef axisDef = new WheelJointDef();
        axisDef.bodyA = chassis;
        axisDef.bodyB = leftWheel;
        axisDef.localAnchorA.set(-width/2 *0.65f + wheelShape.getRadius(), -height/2*1.1f);
        axisDef.frequencyHz = chassisFixtureDef.density * 1.2f;
        axisDef.localAxisA.set(Vector2.Y);
        axisDef.maxMotorTorque = chassisFixtureDef.density * 10;

        leftAxis = (WheelJoint) world.createJoint(axisDef);

        // rigth axis
        axisDef.bodyB = rightWheel;
        axisDef.localAnchorA.set(width/2 *0.9f - wheelShape.getRadius(), -height/2*1.1f);

        rightAxis = (WheelJoint) world.createJoint(axisDef);

        // hit box to detect hit againts ground
        chassisShape.setAsBox(width/4, height/20);
        FixtureDef hit = new FixtureDef();
        hit.shape = chassisShape;

        this.width = width;
        this.height = height;
    }

    public Fixture getHitSensor() {
        return hitBox;
    }

    public void accelerate(boolean b) {
        leftAxis.enableMotor(b);
        leftAxis.setMotorSpeed(-motorSpeed);
    }

    public void brake(boolean b) {
        leftAxis.enableMotor(b);
        leftAxis.setMotorSpeed(motorSpeed);
    }

    public Vector2 getSpeed() {
        return chassis.getLinearVelocity();
    }

    public Body getChassis() {
        return chassis;
    }

    public Body getLeftWheel() {
        return leftWheel;
    }

    public Body getRightWheel() {
        return rightWheel;
    }

    public float getWheelRadius() {
        return wheelShape.getRadius();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    /* This scale down and translate the points created with an
     * external tool. Not ideal but works for now.
     */
    float C = 58;
    float OX = 2.0f;
    float OY = 0.7f;

    /* Chassis points */
    Vector2 p1[] = new Vector2[] {
            new Vector2(5.000f/C - OX, 16.000f/C - OY),
            new Vector2(13.000f/C - OX, 18.000f/C - OY),
            new Vector2(29.000f/C - OX, 51.000f/C - OY),
            new Vector2(22.000f/C - OX, 56.000f/C - OY),
            new Vector2(1.000f/C - OX, 49.000f/C - OY),
            new Vector2(0.000f/C - OX, 24.000f/C - OY)
    };
    Vector2 p2[] = new Vector2[] {
            new Vector2(156.000f/C - OX, 79.000f/C - OY),
            new Vector2(119.000f/C - OX, 82.000f/C - OY),
            new Vector2(107.000f/C - OX, 74.000f/C - OY),
            new Vector2(104.000f/C - OX, 65.000f/C - OY),
            new Vector2(102.000f/C - OX, 52.000f/C - OY),
            new Vector2(113.000f/C - OX, 4.000f/C - OY),
            new Vector2(139.000f/C - OX, 51.000f/C - OY),
    };

    Vector2 p3[] = new Vector2[] {
            new Vector2(43.000f/C - OX, 9.000f/C - OY),
            new Vector2(51.000f/C - OX, 21.000f/C - OY),
            new Vector2(11.000f/C - OX, 18.000f/C - OY),
            new Vector2(18.000f/C - OX, 12.000f/C - OY),
            new Vector2(31.000f/C - OX, 8.000f/C - OY),
    };

    Vector2 p4[] = new Vector2[] {
            new Vector2(231.000f/C - OX, 11.000f/C - OY),
            new Vector2(227.000f/C - OX, 40.000f/C - OY),
            new Vector2(222.000f/C - OX, 47.000f/C - OY),
            new Vector2(202.000f/C - OX, 51.000f/C - OY),
            new Vector2(211.000f/C - OX, 22.000f/C - OY),
            new Vector2(216.000f/C - OX, 10.000f/C - OY),
            new Vector2(220.000f/C - OX, 9.000f/C - OY),
    };

    Vector2 p5[] = new Vector2[] {
            new Vector2(85.000f/C - OX, 7.000f/C - OY),
            new Vector2(113.000f/C - OX, 4.000f/C - OY),
            new Vector2(102.000f/C - OX, 52.000f/C - OY),
            new Vector2(77.000f/C - OX, 21.000f/C - OY),
    };

    Vector2 p6[] = new Vector2[] {
            new Vector2(176.000f/C - OX, 8.000f/C - OY),
            new Vector2(185.000f/C - OX, 25.000f/C - OY),
            new Vector2(167.000f/C - OX, 51.000f/C - OY),
            new Vector2(139.000f/C - OX, 4.000f/C - OY),
    };

    Vector2 p7[] = new Vector2[] {
            new Vector2(27.000f/C - OX, 51.000f/C - OY),
            new Vector2(11.000f/C - OX, 18.000f/C - OY),
            new Vector2(51.000f/C - OX, 21.000f/C - OY),
            new Vector2(102.000f/C - OX, 52.000f/C - OY),
    };

    Vector2 p8[] = new Vector2[] {
            new Vector2(51.000f/C - OX, 21.000f/C - OY),
            new Vector2(77.000f/C - OX, 21.000f/C - OY),
            new Vector2(102.000f/C - OX, 52.000f/C - OY),
    };

    Vector2 p9[] = new Vector2[] {
            new Vector2(167.000f/C - OX, 51.000f/C - OY),
            new Vector2(185.000f/C - OX, 25.000f/C - OY),
            new Vector2(201.000f/C - OX, 26.000f/C - OY),
            new Vector2(202.000f/C - OX, 51.000f/C - OY),
    };

    Vector2 p10[] = new Vector2[] {
            new Vector2(201.000f/C - OX, 26.000f/C - OY),
            new Vector2(211.000f/C - OX, 22.000f/C - OY),
            new Vector2(202.000f/C - OX, 51.000f/C - OY),
    };

}
