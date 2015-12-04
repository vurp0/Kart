package org.sandholm.max.kart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * GameController that reads input from keyboard to control a Kart
 */
public class KeyboardGameController implements GameController, InputProcessor {
    private MenuController menuController;

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

    @Override
    public void setMenuController(MenuController controller) {
        this.menuController = controller;
    }

    //InputProcessor
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W) {
            menuController.upPressed();
        } else if (keycode == Input.Keys.S) {
            menuController.downPressed();
        } else if (keycode == Input.Keys.A) {
            menuController.leftPressed();
        } else if (keycode == Input.Keys.D) {
            menuController.rightPressed();
        } else if (keycode == Input.Keys.BACKSPACE) {
            menuController.backPressed();
        } else if (keycode == Input.Keys.ENTER) {
            menuController.OKPressed();
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
