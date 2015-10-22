package org.sandholm.max.kart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;

/**
 * Abstract class for implementing menu Screens.
 */
abstract public class MenuScreen implements Screen {
    protected Matrix4 projection;
    protected SpriteBatch batch;

    protected FreeTypeFontGenerator fontGenerator;

    protected int screenWidth;
    protected int screenHeight;

    protected KartGame game;

    public MenuScreen(KartGame game) {
        this.game = game;
    }

    @Override
    public void show(){
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        projection = new Matrix4().setToOrtho2D(0, 0, screenWidth, screenHeight);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(projection);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Regular.ttf"));

    }

    @Override
    public void resize(int width, int height){
        screenWidth = width;
        screenHeight = height;
        projection.setToOrtho2D(0, 0, width, height);
        batch.setProjectionMatrix(projection);
    }

    public BitmapFont generateFont(float size) {  //TODO: cache generated fonts somehow
        //size is part of screenHeight
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(screenHeight*size);
        return fontGenerator.generateFont(parameter);
    }
}
