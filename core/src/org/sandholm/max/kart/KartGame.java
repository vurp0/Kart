package org.sandholm.max.kart;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Created by max on 23.9.2015.
 */
public class KartGame extends Game {
    public enum Flow{TITLE_SCREEN,KART_SELECT_SCREEN,MAP_SELECT_SCREEN,GAME_SCREEN,RESULTS_SCREEN};

    InputMultiplexer multiplexer;

    KartScreen kartScreen;
    GameController kartGameController;

    TitleScreen titleScreen;

    @Override
    public void create() {
        multiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(multiplexer);
        titleScreen = new TitleScreen(this);
        multiplexer.addProcessor(titleScreen);
        Controllers.addListener(titleScreen);
        setScreen(titleScreen);
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

    public void transitionTo(Flow flow) {

        //TODO
    }

    public void startGame() {
        kartScreen = new KartScreen(this);
        setScreen(kartScreen);
    }
}
