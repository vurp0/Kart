package org.sandholm.max.kart;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

/**
 * Created by max on 12/15/15.
 */
public class KartGameShaderProvider extends BaseShaderProvider {
    String vertShader;
    String fragShader;

    public KartGameShaderProvider(String vertShader, String fragShader) {
        this.vertShader = vertShader;
        this.fragShader = fragShader;
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        return new KartGameShader(renderable, new KartGameShader.Config(vertShader, fragShader));
    }
}
