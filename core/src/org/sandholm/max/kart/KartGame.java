package org.sandholm.max.kart;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;

/**
 * Created by max on 23.9.2015.
 */
public class KartGame extends Game {
    KartScreen kartScreen;

    @Override
    public void create() {
        kartScreen = new KartScreen(this);
        setScreen(kartScreen);
    }
}
