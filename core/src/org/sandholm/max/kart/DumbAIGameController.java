package org.sandholm.max.kart;

/**
 * Dumb AI game controller, does nothing but constantly accelerate and turn left.
 */
public class DumbAIGameController implements GameController {
    @Override
    public float getTurning() {
        return 1;
    }

    @Override
    public float getAccelerator() {
        return 1;
    }

    @Override
    public float getBraking() {
        return 0;
    }

    @Override
    public boolean getDrifting() {
        return false;
    }
}
