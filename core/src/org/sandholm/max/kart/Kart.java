package org.sandholm.max.kart;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by max on 9/13/15.
 */
public class Kart {
    public enum Direction{LEFT, RIGHT};
    float TOP_SPEED;              //per second
    static float TURNING_SPEED = 40f;   //per second
    static float DECELERATION = 1f;

    Vector2 position;
    float rotation;

    Vector2 velocity;
    Vector2 acceleration;

    public Kart(float topSpeed, Vector2 position) {
        TOP_SPEED = topSpeed;
        this.position = position;
        velocity = new Vector2();
        acceleration = new Vector2();
    }

    public void update(float deltaTime) {
        acceleration.setAngle((rotation+90f)%360);

        velocity.add(acceleration.cpy().scl(deltaTime));
        velocity.setLength(velocity.len()-DECELERATION*deltaTime);
        velocity.clamp(0f, TOP_SPEED);
        position.add(velocity.cpy().scl(deltaTime));
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public void startAccelerating() {
        acceleration.set(10f, 0f);
        acceleration.setAngle(rotation);
    }

    public void stopAccelerating() {
        acceleration.setLength(0f);
    }

    public void accelerate() {
        acceleration.set(10f, 0f);
    }

    public void turn(Direction dir, float deltaTime) {
        rotation += TURNING_SPEED*deltaTime*(dir == Direction.LEFT ? 1 : -1);
        velocity.setAngle(rotation);
    }


}
