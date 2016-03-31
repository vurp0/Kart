package org.sandholm.max.kart.desktop;

import com.badlogic.gdx.Gdx;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.PlatformSpecifics;
import org.sandholm.max.kart.screens.MenuScreen;

/**
 * Created by max on 2016-01-22.
 */
public class DesktopSpecifics implements PlatformSpecifics {
    private SettingsScreen settingsScreen;

    static int WINDOW_WIDTH = 256;
    static int WINDOW_HEIGHT = 256;

    @Override
    public int getWindowWidth() {
        return WINDOW_WIDTH;
    }

    @Override
    public int getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    @Override
    public MenuScreen newSettingsScreen(KartGame game) {
        settingsScreen = new SettingsScreen(game);
        return settingsScreen;
    }

    @Override
    public void quitGame() {
        Gdx.graphics.setWindowedMode(WINDOW_WIDTH, WINDOW_HEIGHT);
    }


}
