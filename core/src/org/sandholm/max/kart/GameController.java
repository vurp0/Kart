package org.sandholm.max.kart;

/**
 * Interface for controllers that control a kart, to be implemented by a class that reads input from e.g. keyboard or gamepads etc.
 */
public interface GameController {

    float getTurning();

    float getAccelerator();

    float getBraking();

    boolean getDrifting();

    void setUIController(UIController controller);
}
