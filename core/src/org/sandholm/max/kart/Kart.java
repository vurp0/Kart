package org.sandholm.max.kart;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Kart physics class
 */
public class Kart {
    public enum Direction{LEFT, RIGHT}
    float TOP_SPEED;                    //per second
    static float TURNING_SPEED = 60f;   //per second
    static float DRIFTING_SPEED = 75f;
    static float MASS = 1f;             //kilograms
    static float ENGINE_FORCE = 150f;  //newtons, completely realistic
    static float FRICTION_S = 70f;

    float friction = 60f;         //coefficient of friction

    Body pBody;

    Vector2 position;
    Vector2 velocity;      //length of velocity is value with unit m/s
    float rotation;

    Vector2 acceleration;  //Length is m/s²
    Vector2 engineForce;   //Length is N which is kg*m/s²
    Vector2 frictionForce; //Length is N
    Vector2 brakeForce;


    public Kart(float topSpeed, Vector2 position, float rotation, World world) {
        TOP_SPEED = topSpeed;
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
        fixtureDef.density = 5f;
        pBody.setLinearDamping(2f);
        //pBody.setAngularDamping(10f);
        Fixture fixture = pBody.createFixture(fixtureDef);
        shape.dispose();

        acceleration = new Vector2();
        engineForce = new Vector2();
        frictionForce = new Vector2();
        brakeForce = new Vector2();
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
        friction = FRICTION_S;
    }

    public void resetFrame() {
        pBody.setLinearDamping(2f);
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getDrawRotation() {
        return velocity.angle();
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
        engineForce.setLength(0);
        engineForce.setAngle(0);
    }

    public void brake() {
        pBody.applyForceToCenter(pBody.getLinearVelocity().cpy().rotate(180).scl(1.5f).add(new Vector2(-35f, 0).rotate(rotation)), true);
        //brakeForce.set(120f*velocity.len()-350f,0);
        //brakeForce.setAngle((rotation-180f)%360);
    }

    public void stopBraking() {
        brakeForce.setLength(0);
    }

    public void turn(Direction dir, float deltaTime, boolean drift) {
        //engineForce.setAngle(engineForce.angle()+10*(dir == Direction.LEFT ? -1 : 1));
        //engineForce.set(engineForce.x, engineForce.x*(dir == Direction.LEFT ? -1 : 1));
        //acceleration.setAngle(acceleration.angle()+TURNING_SPEED*deltaTime*(dir == Direction.LEFT ? -1 : 1));

        if (!drift) {
            //pBody.applyTorque(TURNING_SPEED*(dir == Direction.LEFT ? 1 : -1), true);

            rotation += TURNING_SPEED*deltaTime*(dir == Direction.LEFT ? 1 : -1);
            rotation = (rotation % 360 + 360) % 360;
            friction = 140f;
        } else {
            rotation += DRIFTING_SPEED*deltaTime*(dir == Direction.LEFT ? 1 : -1);
            rotation = (rotation % 360 + 360) % 360;
            pBody.setLinearDamping(0.75f);
            friction = 70f;
        }
    }




}
