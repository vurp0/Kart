package org.sandholm.max.kart;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controllers;
import org.sandholm.max.kart.gamecontroller.GameController;
import org.sandholm.max.kart.screens.KartGameScreen;
import org.sandholm.max.kart.screens.MainMenuScreen;
import org.sandholm.max.kart.screens.TitleScreen;
import org.sandholm.max.kart.screens.UIScreen;
import org.sandholm.max.kart.tweenaccessors.UIScreenAccessor;

/**
 * Created by max on 23.9.2015.
 */
public class KartGame extends Game {
    public enum Flow{TITLE_SCREEN,MAIN_MENU_SCREEN,KART_MAP_SELECT_SCREEN,GAME_SCREEN,RESULTS_SCREEN};

    public InputMultiplexer multiplexer;

    KartGameScreen kartGameScreen;
    public GameController kartGameController;

    TitleScreen titleScreen;

    public TweenManager tweenManager;

    @Override
    public void create() {
        tweenManager = new TweenManager();
        Tween.setCombinedAttributesLimit(1);
        Tween.registerAccessor(UIScreen.class, new UIScreenAccessor());

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
            case MAIN_MENU_SCREEN:
                MainMenuScreen mainMenuScreen = new MainMenuScreen(this);
                Tween.to(getScreen(), UIScreenAccessor.FULL_SCREEN_DARKNESS, 2f).target(0f).ease(TweenEquations.easeOutQuart).start(tweenManager).setCallback(new SwitchScreenCallback(mainMenuScreen)).setCallbackTriggers(TweenCallback.COMPLETE);
                kartGameController.setUIController(mainMenuScreen);
                break;
            case KART_MAP_SELECT_SCREEN:
                System.out.println("TODO: switch to kart&map select screen"); //TODO
                break;
            case GAME_SCREEN:
                kartGameScreen = new KartGameScreen(this);
                Tween.to(getScreen(), UIScreenAccessor.FULL_SCREEN_DARKNESS, 2f).target(0f).ease(TweenEquations.easeOutQuart).start(tweenManager).setCallback(new SwitchScreenCallback(kartGameScreen)).setCallbackTriggers(TweenCallback.COMPLETE);
                //((UIScreen) getScreen()).startFadeOut();
                kartGameController.setUIController(kartGameScreen);
                break;
            case RESULTS_SCREEN:
                System.out.println("TODO: switch to results screen"); //TODO
                break;

        }
    }

    class SwitchScreenCallback implements TweenCallback {
        Screen screen;

        public SwitchScreenCallback(Screen screen) {
            this.screen = screen;
        }

        @Override
        public void onEvent(int eventType, BaseTween<?> baseTween) {
            if (eventType == TweenCallback.COMPLETE) {
                getScreen().dispose();
                setScreen(screen);
            }
        }
    }

    /*public void startGame() {
        kartGameScreen = new KartGameScreen(this);
        setScreen(kartGameScreen);
    }*/
}
