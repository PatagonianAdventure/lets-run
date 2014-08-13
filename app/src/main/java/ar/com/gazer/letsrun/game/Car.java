package ar.com.gazer.letsrun.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
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
    private final PolygonShape chassisShape;
    private final float width;
    private final float height;
    private Body chassis, leftWheel, rightWheel;
    private WheelJoint leftAxis, rightAxis;
    private float motorSpeed = 50f;

    public Car(World world, FixtureDef chassisFixtureDef, FixtureDef wheelFixtureDef, float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        // chassis
        chassisShape = new PolygonShape();
        // counterclockwise order
        // chassisShape.set(new float[] {-width/2, -height/2, width/2, -height/2, width/2, height/2, -width/2, height/2});
        chassisShape.setAsBox(width/2, height/2);

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
        rightWheel = world.createBody(bodyDef);
        rightWheel.createFixture(wheelFixtureDef);

        // left axis
        WheelJointDef axisDef = new WheelJointDef();
        axisDef.bodyA = chassis;
        axisDef.bodyB = leftWheel;
        axisDef.localAnchorA.set(-width/2 *0.65f + wheelShape.getRadius(), -height/2*1.1f);
        axisDef.frequencyHz = chassisFixtureDef.density * 1.2f;
        axisDef.localAxisA.set(Vector2.Y);
        axisDef.maxMotorTorque = chassisFixtureDef.density * 17;

        leftAxis = (WheelJoint) world.createJoint(axisDef);

        // rigth axis
        axisDef.bodyB = rightWheel;
        axisDef.localAnchorA.set(width/2 *0.9f - wheelShape.getRadius(), -height/2*1.1f);

        rightAxis = (WheelJoint) world.createJoint(axisDef);

        this.width = width;
        this.height = height;
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
}
