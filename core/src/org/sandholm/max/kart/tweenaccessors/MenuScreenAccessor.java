package org.sandholm.max.kart.tweenaccessors;

import aurelienribon.tweenengine.TweenAccessor;
import org.sandholm.max.kart.screens.MenuScreen;

/**
 * Created by max on 3/16/16.
 */
public class MenuScreenAccessor implements TweenAccessor<MenuScreen> {

    public static final int MENU_DRAWING_OFFSET = 3;

    @Override
    public int getValues(MenuScreen target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case MENU_DRAWING_OFFSET:
                returnValues[0] = target.getMenuDrawingOffset();
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(MenuScreen target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case MENU_DRAWING_OFFSET:
                target.setMenuDrawingOffset(newValues[0]);
                break;
            default:
                assert false;
        }

    }
}
