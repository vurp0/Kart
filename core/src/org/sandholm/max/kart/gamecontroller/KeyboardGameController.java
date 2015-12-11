package org.sandholm.max.kart.gamecontroller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import org.sandholm.max.kart.UIController;

/**
 * GameController that reads input from keyboard to control a Kart
 */
public class KeyboardGameController implements GameController, InputProcessor {
    private org.sandholm.max.kart.UIController UIController;

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
        return Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT);
    }

    public void setUIController(UIController controller) {
        this.UIController = controller;
    }

    //InputProcessor
    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.W:
                UIController.upPressed();
                break;
            case Input.Keys.S:
                UIController.downPressed();
                break;
            case Input.Keys.A:
                UIController.leftPressed();
                break;
            case Input.Keys.D:
                UIController.rightPressed();
                break;
            case Input.Keys.BACKSPACE:
                UIController.backPressed();
                break;
            case Input.Keys.ENTER:
                UIController.OKPressed();
                break;
            case Input.Keys.ESCAPE:
                UIController.pausePressed();
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
