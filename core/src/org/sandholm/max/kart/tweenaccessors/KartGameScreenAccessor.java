package org.sandholm.max.kart.tweenaccessors;

import aurelienribon.tweenengine.TweenAccessor;
import org.sandholm.max.kart.KartGameScreen;

/**
 * Created by max on 12/11/15.
 */
public class KartGameScreenAccessor implements TweenAccessor<KartGameScreen> {

    public static final int DARKNESS = 1;

    @Override
    public int getValues(KartGameScreen target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case DARKNESS:
                returnValues[0] = target.getDarkness();
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    @Override
    public void setValues(KartGameScreen target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case DARKNESS:
                target.setDarkness(newValues[0]);
                break;
            default:
                assert false;
        }

    }
}