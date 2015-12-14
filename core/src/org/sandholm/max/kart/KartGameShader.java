package org.sandholm.max.kart;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by max on 14.12.2015.
 */
public class KartGameShader implements Shader {
    String vertShaderSource;
    String fragShaderSource;

    ShaderProgram program;
    Camera camera;
    RenderContext context;

    int u_projTrans;
    int u_worldTrans;

    public KartGameShader(String vertShaderSource, String fragShaderSource) {
        this.vertShaderSource = vertShaderSource;
        this.fragShaderSource = fragShaderSource;
    }

    @Override
    public void init() {
        program = new ShaderProgram(vertShaderSource, fragShaderSource);
        if (!program.isCompiled())
            throw new GdxRuntimeException(program.getLog());
        u_projTrans = program.getUniformLocation("u_projTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
		this.camera = camera;
		this.context = context;
		program.begin();
		program.setUniformMatrix(u_projTrans, camera.combined);
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);

    }

    @Override
    public void render(Renderable renderable) {
		program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
		renderable.meshPart.render(program);
    }

    @Override
    public void end() {
		program.end();

    }

    @Override
    public void dispose() {
        program.dispose();
    }
}
