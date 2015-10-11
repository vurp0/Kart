package org.sandholm.max.kart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * GameController that reads input from keyboard to control a Kart
 */
public class KeyboardGameController implements GameController {
    @Override
    public float getTurning() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            return 1;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public float getAccelerator() {
        return Gdx.input.isKeyPressed(Input.Keys.W)?1:0;
    }

    @Override
    public float getBraking() {
        return Gdx.input.isKeyPressed(Input.Keys.S)?1:0;
    }

    @Override
    public boolean getDrifting() {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE);
    }
}
