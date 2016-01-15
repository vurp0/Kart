package org.sandholm.max.kart;

/**
 * Created by max on 1/14/16.
 */
public class BaseMenuItem {
    public Menu.ActionType actionType = Menu.ActionType.OTHER;

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