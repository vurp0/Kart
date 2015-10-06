package org.sandholm.max.kart;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Kart physics class
 */
public class Kart {
    public enum Direction{LEFT, RIGHT}
    static float TURNING_SPEED = 80f;   //TWEAK THESE PROPERTIES
    static float DRIFTING_SPEED = 95f;
    static float MASS = 1f;
    static float ENGINE_FORCE = 350f;
    static float FRICTION_S = 2f;

    Body pBody;

    Vector2 position;
    Vector2 velocity;      //length of velocity is value with unit m/s
    float rotation;

    Vector2 acceleration;  //Length is m/s²
    Vector2 engineForce;   //Length is N which is kg*m/s²
    Vector2 frictionForce; //Length is N
    Vector2 brakeForce;


    public Kart(Vector2 position, float rotation, World world) {
        this.position = position;
        this.rotation = rotation;
        velocity = new Vector2();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(this.position);
        bodyDef.angle = rotation*MathUtils.degreesToRadians;
        bodyDef.fixedRotation = true;
        pBody = world.createBody(bodyDef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 10f;
        fixtureDef.restitution = 1f;
        pBody.setLinearDamping(FRICTION_S);
        //pBody.setAngularDamping(10f);
        Fixture fixture = pBody.createFixture(fixtureDef);
        shape.dispose();

        /*acceleration = new Vector2();
        engineForce = new Vector2();
        frictionForce = new Vector2();
        brakeForce = new Vector2();*/
    }

    public void update(float deltaTime) {
        /*frictionForce.set(friction*velocity.len(), 0f);
        frictionForce.setAngle((velocity.angle() - 180f) % 360);

        Vector2 resultantForce = new Vector2();
        resultantForce.add(engineForce);
        resultantForce.add(frictionForce);
        resultantForce.add(brakeForce);

        acceleration.set((resultantForce.len()/MASS)*deltaTime, 0f);
        acceleration.setAngle(resultantForce.angle());

        velocity.add(acceleration.cpy().scl(deltaTime));
        position.add(velocity.cpy().scl(deltaTime));*/
        position.set(pBody.getPosition());
        //System.out.println(position);
        pBody.setTransform(pBody.getPosition(), rotation*MathUtils.degreesToRadians);
        //rotation = pBody.getAngle()* MathUtils.radiansToDegrees;
        //friction = FRICTION_S;
    }

    public void resetFrame() {
        pBody.setLinearDamping(FRICTION_S);
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public void accelerate() {
        pBody.applyForceToCenter(new Vector2(ENGINE_FORCE, 0).rotate(rotation), true);

        //engineForce.set(ENGINE_FORCE, 0);
        //engineForce.setAngle(rotation);
    }

    public void stopAccelerating() {
        /*engineForce.setLength(0);
        engineForce.setAngle(0);*/
    }

    public void brake() {
        pBody.applyForceToCenter(pBody.getLinearVelocity().cpy().rotate(180).scl(1.5f).add(new Vector2(-35f, 0).rotate(rotation)), true);
    }

    public void stopBraking() {
        /*brakeForce.setLength(0);*/
    }

    public void turn(float amount, float deltaTime, boolean drift) {

        if (!drift) {
            rotation += TURNING_SPEED*amount*deltaTime/**(dir == Direction.LEFT ? 1 : -1)*/;
            rotation = (rotation % 360 + 360) % 360;
        } else {
            rotation += DRIFTING_SPEED*amount*deltaTime/**(dir == Direction.LEFT ? 1 : -1)*/;
            rotation = (rotation % 360 + 360) % 360;
            pBody.applyForceToCenter(new Vector2(9f*amount, -2f).rotate(rotation+90/**(dir == Direction.LEFT ? -1 : 1)*/), true);
            pBody.setLinearDamping(FRICTION_S*0.8f);
        }
    }




}
