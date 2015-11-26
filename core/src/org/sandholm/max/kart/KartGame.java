package org.sandholm.max.kart;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Created by max on 23.9.2015.
 */
public class KartGame extends Game {
    InputMultiplexer multiplexer;

    KartScreen kartScreen;
    GameController kartGameController;

    GameControllerSelectScreen gameControllerSelectScreen;
    MainMenuScreen mainMenuScreen;

    @Override
    public void create() {
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        gameControllerSelectScreen = new GameControllerSelectScreen(this);
        mainMenuScreen = new MainMenuScreen(this);
        multiplexer.addProcessor(mainMenuScreen);
        Controllers.addListener(mainMenuScreen);
        setScreen(mainMenuScreen);
        System.out.println(Controllers.getControllers());
    }

    public void setKartGameController(GameController gameController) {
        this.kartGameController = gameController;
    }

    @Override
    public void setScreen(Screen screen) {
        multiplexer.clear();
        Controllers.clearListeners();
        super.setScreen(screen);
        //multiplexer.addProcessor((InputProcessor)screen);
        //Controllers.addListener((ControllerListener)screen);
    }

    public void startGame() {
        kartScreen = new KartScreen(this);
        gameControllerSelectScreen.dispose();
        multiplexer.removeProcessor(gameControllerSelectScreen);
        Controllers.removeListener(gameControllerSelectScreen);
        setScreen(kartScreen);
    }
}
