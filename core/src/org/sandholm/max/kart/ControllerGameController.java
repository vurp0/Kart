package org.sandholm.max.kart;

import com.badlogic.gdx.controllers.Controller;

/**
 * Sorry about the ridiculous class name. A GameController which uses input from a LibGDX Controller.
 */
public class ControllerGameController implements GameController {
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

    public Controller getController() {
        return controller;
    }
}
