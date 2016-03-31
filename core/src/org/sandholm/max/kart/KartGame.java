package org.sandholm.max.kart;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.Pixmap;
import org.sandholm.max.kart.gamecontroller.GameController;
import org.sandholm.max.kart.screens.*;
import org.sandholm.max.kart.tweenaccessors.MenuScreenAccessor;
import org.sandholm.max.kart.tweenaccessors.UIScreenAccessor;

/**
 * Created by max on 23.9.2015.
 */
public class KartGame extends Game {
    static float FADEOUT_TIME = 1f;

    public PlatformSpecifics platformSpecifics;

    public enum Flow{TITLE_SCREEN,MAIN_MENU_SCREEN,SETTINGS_SCREEN,CONTROLLER_MAPPING_SCREEN,KART_SELECT_SCREEN,MAP_SELECT_SCREEN,GAME_SCREEN,RESULTS_SCREEN};

    public InputMultiplexer multiplexer;

    KartGameScreen kartGameScreen;
    public GameController kartGameController;

    public String playerKartFileName;

    TitleScreen titleScreen;

    public TweenManager tweenManager;

    public KartGame(PlatformSpecifics platformSpecifics) {
        super();
        this.platformSpecifics = platformSpecifics;
    }

    @Override
    public void create() {

        Preferences videoPrefs = Gdx.app.getPreferences("org.sandholm.max.kart.videoprefs");
        if (!videoPrefs.contains("fullscreen")) {
            videoPrefs.putBoolean("fullscreen", true);
        }
        boolean fullScreen = videoPrefs.getBoolean("fullscreen");
        if (fullScreen) {
            Gdx.graphics.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(platformSpecifics.getWindowWidth(), platformSpecifics.getWindowHeight());
        }

        Gdx.graphics.setCursor(Gdx.graphics.newCursor(new Pixmap(1, 1, Pixmap.Format.RGBA8888), 0, 0));

        tweenManager = new TweenManager();
        Tween.setCombinedAttributesLimit(4);
        Tween.registerAccessor(UIScreen.class, new UIScreenAccessor());
        Tween.registerAccessor(MenuScreen.class, new MenuScreenAccessor());
        Tween.registerAccessor(MenuScreen.class, new UIScreenAccessor());

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
                Tween.to(getScreen(), UIScreenAccessor.FULL_SCREEN_DARKNESS, FADEOUT_TIME).target(0f).ease(TweenEquations.easeOutQuart).start(tweenManager).setCallback(new SwitchScreenCallback(mainMenuScreen)).setCallbackTriggers(TweenCallback.COMPLETE);
                kartGameController.setUIController(mainMenuScreen);
                break;
            case SETTINGS_SCREEN:
                MenuScreen settingsScreen = platformSpecifics.newSettingsScreen(this);
                Tween.to(getScreen(), UIScreenAccessor.FULL_SCREEN_DARKNESS, FADEOUT_TIME).target(0f).ease(TweenEquations.easeOutQuart).start(tweenManager).setCallback(new SwitchScreenCallback(settingsScreen)).setCallbackTriggers(TweenCallback.COMPLETE);
                kartGameController.setUIController(settingsScreen);
                break;
            case CONTROLLER_MAPPING_SCREEN:
                ControllerMappingScreen controllerMappingScreen = new ControllerMappingScreen(this);
                Tween.to(getScreen(), UIScreenAccessor.FULL_SCREEN_DARKNESS, FADEOUT_TIME).target(0f).ease(TweenEquations.easeOutQuart).start(tweenManager).setCallback(new SwitchScreenCallback(controllerMappingScreen)).setCallbackTriggers(TweenCallback.COMPLETE);
                kartGameController.setUIController(controllerMappingScreen);
                break;
            case KART_SELECT_SCREEN:
                MenuScreen kartSelectScreen = new KartSelectScreen(this);
                Tween.to(getScreen(), UIScreenAccessor.FULL_SCREEN_DARKNESS, FADEOUT_TIME).target(0f).ease(TweenEquations.easeOutQuart).start(tweenManager).setCallback(new SwitchScreenCallback(kartSelectScreen)).setCallbackTriggers(TweenCallback.COMPLETE);
                kartGameController.setUIController(kartSelectScreen);
                break;
            case MAP_SELECT_SCREEN:
                System.out.println("TODO: switch to kart&map select screen"); //TODO
                break;
            case GAME_SCREEN:
                kartGameScreen = new KartGameScreen(this, playerKartFileName);
                Tween.to(getScreen(), UIScreenAccessor.FULL_SCREEN_DARKNESS, FADEOUT_TIME).target(0f).ease(TweenEquations.easeOutQuart).start(tweenManager).setCallback(new SwitchScreenCallback(kartGameScreen)).setCallbackTriggers(TweenCallback.COMPLETE);
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

    public void quitGame() {
        platformSpecifics.quitGame();
        Gdx.app.exit();
    }
}
