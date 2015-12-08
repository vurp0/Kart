package org.sandholm.max.kart;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controllers;

/**
 * Created by max on 23.9.2015.
 */
public class KartGame extends Game {
    public enum Flow{TITLE_SCREEN,KART_SELECT_SCREEN,MAP_SELECT_SCREEN,GAME_SCREEN,RESULTS_SCREEN};

    InputMultiplexer multiplexer;

    KartGameScreen kartGameScreen;
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
        super.setScreen(screen);
    }

    public void transitionTo(Flow flow) {
        switch (flow) {
            case TITLE_SCREEN:
                setScreen(titleScreen);
                break;
            case KART_SELECT_SCREEN:
                System.out.println("TODO: switch to kart select screen"); //TODO
                break;
            case MAP_SELECT_SCREEN:
                System.out.println("TODO: switch to map select screen"); //TODO
                break;
            case GAME_SCREEN:
                kartGameScreen = new KartGameScreen(this);
                setScreen(kartGameScreen);
                kartGameController.setUIController(kartGameScreen);
                break;
            case RESULTS_SCREEN:
                System.out.println("TODO: switch to results screen"); //TODO
                break;

        }
    }

    /*public void startGame() {
        kartGameScreen = new KartGameScreen(this);
        setScreen(kartGameScreen);
    }*/
}
