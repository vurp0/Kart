package org.sandholm.max.kart.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.tweenaccessors.UIScreenAccessor;

/**
 * Abstract class for adding KartGame-specific functionality to the game's Screens.
 */
abstract public class UIScreen {
    protected Matrix4 UIProjection;
    protected SpriteBatch UIBatch;

    protected ShaderProgram UIShader;
    protected ShaderProgram UIPostProcShaderProgram;


    protected FrameBuffer UIFrameBuffer;

    protected FreeTypeFontGenerator fontGenerator;

    protected Mesh fullScreenMesh = createFullScreenQuad();
    protected int screenWidth;
    protected int screenHeight;

    protected float UIDarkness = 0f;

    public float getFullScreenDarkness() {
        return UIDarkness;
    }
    public void setFullScreenDarkness(float darkness) {
        this.UIDarkness = darkness;
    }

    protected KartGame game;

    public UIScreen(KartGame game) {
        this.game = game;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        UIProjection = new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight);
        UIBatch = new SpriteBatch();
        UIBatch.setProjectionMatrix(UIProjection);

        String vertexShader = Gdx.files.internal("shaders/uiscreen.vert").readString();
        String fragmentShader = Gdx.files.internal("shaders/uiscreen.frag").readString();
        UIShader = new ShaderProgram(vertexShader, fragmentShader);
        if (!UIShader.isCompiled()) throw new IllegalArgumentException("couldn't compile UIShader: " + UIShader.getLog());
        UIBatch.setShader(UIShader);

        String postProcVertexShader = Gdx.files.internal("shaders/kartgame.postproc.vert").readString();
        String postProcFragmentShader = Gdx.files.internal("shaders/kartgame.postproc.frag").readString();
        UIPostProcShaderProgram = new ShaderProgram(postProcVertexShader, postProcFragmentShader);
        if (!UIPostProcShaderProgram.isCompiled()) throw new IllegalArgumentException("couldn't compile postproc shader: " + UIPostProcShaderProgram.getLog());

        UIFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, 64, 64, true);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
    }

    boolean fadeInStarted = false;
    public void render(float deltaTime) {
        UIShader.begin();
        UIShader.setUniformf("darkness", UIDarkness);
        UIShader.end();
        if (!fadeInStarted && deltaTime < 1f/30f) {
            fadeInStarted = true;
            Tween.to(this, UIScreenAccessor.FULL_SCREEN_DARKNESS, 1f).target(1f).ease(TweenEquations.easeOutQuart).start(game.tweenManager).setCallback(new FadeInTweenCallback()).setCallbackTriggers(TweenCallback.COMPLETE);
        }
        game.tweenManager.update(deltaTime);
    }

    class FadeInTweenCallback implements TweenCallback {
        @Override
        public void onEvent(int eventType, BaseTween<?> source) {
            if (eventType == TweenCallback.COMPLETE) {
                fadeInEnded();
            }

        }
    }

    public void fadeInEnded() {

    }

    public void resize(int width, int height){
        screenWidth = width;
        screenHeight = height;
        UIProjection.setToOrtho2D(0, 0, width, height);
        UIBatch.setProjectionMatrix(UIProjection);
    }

    public BitmapFont generateFont(float size) {  //TODO: cache generated fonts somehow
        //size is part of screenHeight
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(screenHeight*size);
        return fontGenerator.generateFont(parameter);
    }

    enum Anchor{NW, N, NE, E, SE, S, SW, W, CENTER}

    private static GlyphLayout glyphLayout = new GlyphLayout();

    public static void drawText(BitmapFont font, SpriteBatch batch, CharSequence text, float x, float y, Anchor anchor) {
        glyphLayout.setText(font, text);
        float drawX = x;
        float drawY = y;
        switch(anchor) {
            case NW:
                drawX = x;
                drawY = y;
                break;
            case N:
                drawX = x - (glyphLayout.width / 2);
                drawY = y;
                break;
            case NE:
                drawX = x - glyphLayout.width;
                drawY = y;
                break;
            case E:
                drawX = x - glyphLayout.width;
                drawY = y + (glyphLayout.height / 2);
                break;
            case SE:
                drawX = x - glyphLayout.width;
                drawY = y + glyphLayout.height;
                break;
            case S:
                drawX = x - (glyphLayout.width / 2);
                drawY = y + glyphLayout.height;
                break;
            case SW:
                drawX = x;
                drawY = y + glyphLayout.height;
                break;
            case W:
                drawX = x;
                drawY = y + (glyphLayout.height / 2);
                break;
            case CENTER:
                drawX = x - (glyphLayout.width / 2);
                drawY = y + (glyphLayout.height / 2);
                break;
            default:
                drawX = x;
                drawY = y;
                break;
        }

        font.draw(batch, glyphLayout, drawX, drawY);

    }
    protected static Mesh createFullScreenQuad(){
        float[] verts = new float[] {
                -1f,-1f,  0f, 0f,
                1f,-1f,  1f, 0f,
                1f, 1f,  1f, 1f,
                -1f, 1f,  0f, 1f
        };
        short[] indices = new short[] { 0, 1, 2, 2, 3, 0 };
        Mesh tmpMesh = new Mesh(true, 4, 6
                , new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position")
                , new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"));
        tmpMesh.setVertices(verts);
        tmpMesh.setIndices(indices);
        return tmpMesh;
    }


}
