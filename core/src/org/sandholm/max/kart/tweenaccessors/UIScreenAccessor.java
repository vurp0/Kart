package org.sandholm.max.kart.tweenaccessors;

import aurelienribon.tweenengine.TweenAccessor;
import org.sandholm.max.kart.screens.UIScreen;

/**
 * Created by max on 12/11/15.
 */
public class UIScreenAccessor implements TweenAccessor<UIScreen> {

    public static final int FULL_SCREEN_DARKNESS = 1;
    public static final int BACKGROUND_DARKNESS = 2;

    @Override
    public int getValues(UIScreen target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case FULL_SCREEN_DARKNESS:
                returnValues[0] = target.getFullScreenDarkness();
                return 1;
            case BACKGROUND_DARKNESS:
                returnValues[0] = target.getBackgroundDarkness();
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(UIScreen target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case FULL_SCREEN_DARKNESS:
                target.setFullScreenDarkness(newValues[0]);
                break;
            case BACKGROUND_DARKNESS:
                target.setBackgroundDarkness(newValues[0]);
                break;
            default:
                assert false;
        }

    }
}