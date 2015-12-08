package org.sandholm.max.kart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by max on 12/8/15.
 */
public class KartGameCameraGroupStrategy extends CameraGroupStrategy {
    ShaderProgram kartGameShader;

    public KartGameCameraGroupStrategy(Camera camera) {
        super(camera);
        String vertexShader = Gdx.files.internal("shaders/blackwhite.vert").readString();
        String fragmentShader = Gdx.files.internal("shaders/blackwhite.frag").readString();
        kartGameShader = new ShaderProgram(vertexShader,fragmentShader);
        ShaderProgram.pedantic = true;
    }

    @Override
    public ShaderProgram getGroupShader(int group) {
        return kartGameShader;
    }

}
