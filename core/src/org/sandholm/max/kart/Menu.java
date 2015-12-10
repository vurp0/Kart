package org.sandholm.max.kart;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by max on 12/10/15.
 */
public interface Menu {

    class BaseMenuItem {
        String title;

        public BaseMenuItem(String title) {

        }

    }

    void render(SpriteBatch batch);

    void nextItem();
    void previousItem();
    void selectItem();
}

