package org.sandholm.max.kart.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import org.sandholm.max.kart.*;

import java.util.ArrayList;

/**
 * Created by max on 12.12.2015.
 */
public class MainMenuScreen extends MenuScreen {

    Texture backgroundTexture;

    float stateTime;

    public MainMenuScreen(KartGame game) {
        super(game);
    }

    //Screen:
    @Override
    public void show() {
        super.show();

        backgroundTexture = new Texture(Gdx.files.internal("background.png")); //TODO: hardcoded placeholder background
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        menu.menuItems.add(new StartGameMenuItem("Start game"));
        //awmenu.menuItems.add(new SettingsMenuItem("Settings"));
        menu.menuItems.add(new QuitMenuItem("Quit game"));

        updateMenuLabels();
    }

    class StartGameMenuItem extends BaseMenuItem {
        public StartGameMenuItem(String title) {
            super(title);
        }

        @Override
        public void select() {
            game.transitionTo(KartGame.Flow.KART_SELECT_SCREEN);
        }
    }

    class SettingsMenuItem extends BaseMenuItem {
        public SettingsMenuItem(String title) {
            super(title);
        }

        @Override
        public void select() {
            game.transitionTo(KartGame.Flow.SETTINGS_SCREEN);
        }
    }

    class QuitMenuItem extends BaseMenuItem {
        public QuitMenuItem(String title) {
            super(title);
        }

        @Override
        public void select() {
            game.quitGame();
        }
    }

    @Override
    public void render(float deltaTime) {
        stateTime += deltaTime;

        super.render(deltaTime);

        menuDrawingOffset = MathUtils.lerp(menuDrawingOffset, screenHeight / 2 - menu.getMenuIndex() * screenHeight / 7, 0.1f);

        UIFrameBuffer.begin();
        Gdx.gl.glCullFace(GL20.GL_BACK);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glDepthMask(true);
            UIBatch.begin();
            UIBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight,
                    0+(stateTime%1), 0/*-(stateTime%1)*/+(MathUtils.sin(stateTime*4)/7),
                    screenWidth/64+(stateTime%1), screenHeight/64/*-(stateTime%1)*/+(MathUtils.sin(stateTime*4)/7));

            drawMenuLabels();
            UIBatch.end();

        UIFrameBuffer.end();

        UIPostProcShaderProgram.begin();
        UIPostProcShaderProgram.setUniformf("darkness", getFullScreenDarkness());
        UIPostProcShaderProgram.end();

        UIFrameBuffer.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        UIFrameBuffer.getColorBufferTexture().bind();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            UIPostProcShaderProgram.begin();
                fullScreenMesh.render(UIPostProcShaderProgram, GL20.GL_TRIANGLES);

            UIPostProcShaderProgram.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backPressed() {

    }

    @Override
    public void pausePressed() {

    }
}
