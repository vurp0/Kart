package org.sandholm.max.kart;

import org.sandholm.max.kart.screens.MenuScreen;

/**
 * Created by max on 2016-01-22.
 */
public interface PlatformSpecifics {
    int getWindowWidth();
    int getWindowHeight();

    MenuScreen newSettingsScreen(KartGame game);

    void quitGame();
}
