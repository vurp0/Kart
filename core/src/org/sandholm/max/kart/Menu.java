package org.sandholm.max.kart;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;

/**
 * Created by max on 12/10/15.
 */
public class Menu {

    public Menu() {
        this.menuItems = new ArrayList<>();
    }

    public enum ActionType {SUBMENU, OTHER}

    public ArrayList<BaseMenuItem> menuItems;

    public int getMenuIndex() {
        return menuIndex;
    }

    int menuIndex;


    public void nextItem() {
        menuIndex++;
        menuIndex = MathUtils.clamp(menuIndex, 0, menuItems.size()-1);
    }

    public void previousItem() {
        menuIndex--;

        menuIndex = MathUtils.clamp(menuIndex, 0, menuItems.size()-1);
    }

    public void selectItem() {
        menuItems.get(menuIndex).select();
    }


}

