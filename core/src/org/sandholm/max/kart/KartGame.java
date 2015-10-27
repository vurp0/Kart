package org.sandholm.max.kart;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Created by max on 23.9.2015.
 */
public class KartGame extends Game {
    InputMultiplexer multiplexer;

    KartScreen kartScreen;
    GameController kartGameController;

    GameControllerSelectScreen gameControllerSelectScreen;

    @Override
    public void create() {
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        gameControllerSelectScreen = new GameControllerSelectScreen(this);
        multiplexer.addProcessor(gameControllerSelectScreen);
        Controllers.addListener(gameControllerSelectScreen);
        setScreen(gameControllerSelectScreen);
        System.out.println(Controllers.getControllers());
    }

    public void setKartGameController(GameController gameController) {
        this.kartGameController = gameController;
    }

    public void startGame() {
        kartScreen = new KartScreen(this);
        gameControllerSelectScreen.dispose();
        multiplexer.removeProcessor(gameControllerSelectScreen);
        Controllers.removeListener(gameControllerSelectScreen);
        setScreen(kartScreen);
    }
}
