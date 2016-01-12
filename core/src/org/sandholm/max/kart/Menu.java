package org.sandholm.max.kart;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;

/**
 * Created by max on 12/10/15.
 */
public class Menu {

    public enum ActionType {SUBMENU, OTHER}

    public ArrayList<BaseMenuItem> menuItems;

    int menuIndex;

    class BaseMenuItem {
        public ActionType actionType = ActionType.OTHER;

        String title;

        public BaseMenuItem(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void select() {

        }

    }

    public void nextItem() {
        menuIndex++;

        int range_size = menuItems.size() + 1;
        if (menuIndex < 0)
            menuIndex += range_size * ((0 - menuIndex) / range_size + 1);
        menuIndex = (menuIndex - menuItems.size()) % range_size;
    }

    public void previousItem() {
        menuIndex--;

        int range_size = menuItems.size() + 1;
        if (menuIndex < 0)
            menuIndex += range_size * ((0 - menuIndex) / range_size + 1);
        menuIndex = (menuIndex - menuItems.size()) % range_size;
    }

    public void selectItem() {
        menuItems.get(menuIndex).select();
    }


}

