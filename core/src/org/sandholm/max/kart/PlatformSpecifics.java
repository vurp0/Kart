package org.sandholm.max.kart;

import org.sandholm.max.kart.screens.MenuScreen;

/**
 * Created by max on 2016-01-22.
 */
public interface PlatformSpecifics {

    public MenuScreen newSettingsScreen(KartGame game);
}
