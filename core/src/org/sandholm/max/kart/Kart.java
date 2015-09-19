package org.sandholm.max.kart;

import com.badlogic.gdx.math.Vector2;

/**
 * Kart physics class
 */
public class Kart {
    public enum Direction{LEFT, RIGHT}
    float TOP_SPEED;                    //per second
    static float TURNING_SPEED = 50f;   //per second
    static float MASS = 1f;             //kilograms
    static float ENGINE_FORCE = 1000f;  //newtons, completely realistic
    static float FRICTION_S = 70f;

    float friction = 60f;         //coefficient of friction

    Vector2 position;
    Vector2 velocity;      //length of velocity is value with unit m/s
    float rotation;

    Vector2 acceleration;  //Length is m/s²
    Vector2 engineForce;   //Length is N which is kg*m/s²
    Vector2 frictionForce; //Length is N
    Vector2 brakeForce;


    public Kart(float topSpeed, Vector2 position, float rotation) {
        TOP_SPEED = topSpeed;
        this.position = position;
        this.rotation = rotation;
        velocity = new Vector2();

        acceleration = new Vector2();
        engineForce = new Vector2();
        frictionForce = new Vector2();
        brakeForce = new Vector2();
    }

    public void update(float deltaTime) {
        frictionForce.set(friction*velocity.len(), 0f);
        frictionForce.setAngle((velocity.angle() - 180f) % 360);

        Vector2 resultantForce = new Vector2();
        resultantForce.add(engineForce);
        resultantForce.add(frictionForce);
        resultantForce.add(brakeForce);

        acceleration.set((resultantForce.len()/MASS)*deltaTime, 0f);
        acceleration.setAngle(resultantForce.angle());

        velocity.add(acceleration.cpy().scl(deltaTime));
        position.add(velocity.cpy().scl(deltaTime));
        //System.out.println(position);

        //rotation = velocity.angle();
        friction = FRICTION_S;
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
        engineForce.set(ENGINE_FORCE, 0);
        engineForce.setAngle(rotation);
    }

    public void stopAccelerating() {
        engineForce.setLength(0);
        engineForce.setAngle(0);
    }

    public void brake() {
        brakeForce.set(120f*velocity.len(),0);
        brakeForce.setAngle((velocity.angle()-180f)%360);
    }

    public void stopBraking() {
        brakeForce.setLength(0);
    }

    public void turn(Direction dir, float deltaTime, boolean drift) {
        //engineForce.setAngle(engineForce.angle()+10*(dir == Direction.LEFT ? -1 : 1));
        //engineForce.set(engineForce.x, engineForce.x*(dir == Direction.LEFT ? -1 : 1));
        //acceleration.setAngle(acceleration.angle()+TURNING_SPEED*deltaTime*(dir == Direction.LEFT ? -1 : 1));
        rotation += TURNING_SPEED*deltaTime*(dir == Direction.LEFT ? 1 : -1);
        rotation %= 360;
        if (!drift) {
            friction = 140f;
        }
    }




}
