package org.sandholm.max.kart;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 * Sorry about the ridiculous class name. A GameController which uses input from a LibGDX Controller.
 */
public class ControllerGameController implements GameController,ControllerListener {
    MenuController menuController;
    Controller controller;

    public ControllerGameController(Controller controller) {
        this.controller = controller; //for now, let's just assume that it's valid and perfect in every way
    }

    @Override
    public float getTurning() {
        return -controller.getAxis(0);
    }

    @Override
    public float getAccelerator() {
        return controller.getButton(1)?1:0;
    }

    @Override
    public float getBraking() {
        return controller.getButton(2)?1:0;
    }

    @Override
    public boolean getDrifting() {
        return controller.getButton(5);
    }

    @Override
    public void setMenuController(MenuController controller) {
        menuController = controller;
    }

    public Controller getController() {
        return controller;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {
        //TODO
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (controller == this.controller) { //TODO: fix controller button mapping in a sensible way
            if (buttonCode == 1) {
                menuController.upPressed();
            } else if (buttonCode == 2) {
                menuController.downPressed();
            } else if (buttonCode == 3) {
                menuController.leftPressed();
            } else if (buttonCode == 4) {
                menuController.rightPressed();
            } else if (buttonCode == 5) {
                menuController.OKPressed();
            } else if (buttonCode == 6) {
                menuController.backPressed();
            }
        }
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }
}
