package org.sandholm.max.kart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Matrix4;

/**
 * Created by max on 22.10.2015.
 */
abstract public class MenuScreen implements Screen {
    protected Matrix4 projection;
    protected SpriteBatch batch;

    protected FreeTypeFontGenerator fontGenerator;

    protected int screenWidth;
    protected int screenHeight;

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

    public BitmapFont generateFont(int size) {
        return null;
    }
}
