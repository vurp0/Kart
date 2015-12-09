package org.sandholm.max.kart;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.graphics.g3d.decals.GroupStrategy;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;

public class KartGameCameraGroupStrategy extends CameraGroupStrategy {
    private static final int GROUP_OPAQUE = 0;
    private static final int GROUP_BLEND = 1;

    Pool<Array<Decal>> arrayPool = new Pool<Array<Decal>>(16) {
        @Override
        protected Array<Decal> newObject () {
            return new Array();
        }
    };
    Array<Array<Decal>> usedArrays = new Array<Array<Decal>>();
    ObjectMap<DecalMaterial, Array<Decal>> materialGroups = new ObjectMap<DecalMaterial, Array<Decal>>();

    Camera camera;
    ShaderProgram shader;

    public KartGameCameraGroupStrategy (Camera camera, ShaderProgram shader) {
        super(camera);
        setCamera(camera);
        this.shader = shader;
        //createDefaultShader();
    }

    public KartGameCameraGroupStrategy (Camera camera, Comparator<Decal> sorter, ShaderProgram shader) {
        super(camera, sorter);
        this.shader = shader;
        //createDefaultShader();

    }

    public void setCamera (Camera camera) {
        this.camera = camera;
    }

    public Camera getCamera () {
        return camera;
    }

    @Override
    public int decideGroup (Decal decal) {
        return decal.getMaterial().isOpaque() ? GROUP_OPAQUE : GROUP_BLEND;
    }

    @Override
    public void afterGroup (int group) {
        if (group == GROUP_BLEND) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        }
    }

    @Override
    public void beforeGroups () {
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        shader.begin();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        //shader.setUniformi("u_texture", 0);
    }

    @Override
    public void afterGroups () {
        shader.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
    }

    /*NOT USED:
    private void createDefaultShader () {
        /*String vertexShader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "uniform mat4 u_projectionViewMatrix;\n" //
                + "varying vec4 v_color;\n" //
                + "varying vec2 v_texCoords;\n" //
                + "\n" //
                + "void main()\n" //
                + "{\n" //
                + "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
                + "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
                + "   gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
                + "}\n"; * /
        String vertexShader = Gdx.files.internal("shaders/blackwhite.vert").readString();
        /*String fragmentShader = "#ifdef GL_ES\n" //
                + "precision mediump float;\n" //
                + "#endif\n" //
                + "varying vec4 v_color;\n" //default
                + "varying vec2 v_texCoords;\n" //
                + "uniform sampler2D u_texture;\n" //
                + "void main()\n"//
                + "{\n" //
                + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
                + "}"; * /
        String fragmentShader = Gdx.files.internal("shaders/blackwhite.frag").readString();

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) throw new IllegalArgumentException("couldn't compile shader: " + shader.getLog());
    }*/

    @Override
    public ShaderProgram getGroupShader (int group) {
        return shader;
    }

    @Override
    public void dispose () {
        if (shader != null) shader.dispose();
    }
}