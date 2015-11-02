package org.sandholm.max.kart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;

/**
 * Abstract class for implementing menu Screens.
 */
abstract public class UIScreen {
    protected Matrix4 UIProjection;
    protected SpriteBatch UIBatch;

    protected FreeTypeFontGenerator fontGenerator;

    protected int screenWidth;
    protected int screenHeight;

    protected KartGame game;

    public UIScreen(KartGame game) {
        this.game = game;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        UIProjection = new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight);
        UIBatch = new SpriteBatch();
        UIBatch.setProjectionMatrix(UIProjection);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));
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
}
