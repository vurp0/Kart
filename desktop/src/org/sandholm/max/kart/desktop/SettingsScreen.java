package org.sandholm.max.kart.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import org.sandholm.max.kart.BaseMenuItem;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.Menu;
import org.sandholm.max.kart.UIController;
import org.sandholm.max.kart.screens.MenuScreen;

import java.util.ArrayList;

/**
 * Created by max on 12.12.2015.
 */
public class SettingsScreen extends MenuScreen {

    Texture backgroundTexture;

    Preferences videoPrefs;
    boolean fullScreen;

    float stateTime;

    public SettingsScreen(KartGame game) {
        super(game);

        videoPrefs = Gdx.app.getPreferences("org.sandholm.max.kart.videoprefs");
    }

    //Screen:
    @Override
    public void show() {
        super.show();

        videoPrefs = Gdx.app.getPreferences("org.sandholm.max.kart.videoprefs");

        fullScreen = videoPrefs.getBoolean("fullscreen");

        backgroundTexture = new Texture(Gdx.files.internal("background.png")); //TODO: hardcoded placeholder background
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);


        menu.menuItems.add(new ToggleFullscreenMenuItem());

        updateMenuLabels();

    }

    class ToggleFullscreenMenuItem extends BaseMenuItem {
        public ToggleFullscreenMenuItem() {
            super("Full screen");
        }

        @Override
        public void select() {
            videoPrefs.putBoolean("fullscreen", !fullScreen);
            videoPrefs.flush();
            fullScreen = videoPrefs.getBoolean("fullscreen");

            updateMenuLabels();

            if (fullScreen) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode(Gdx.graphics.getMonitor()));
            } else {
                Gdx.graphics.setWindowedMode(800, 480);
            }
            resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            Gdx.graphics.setCursor(Gdx.graphics.newCursor(new Pixmap(1, 1, Pixmap.Format.RGBA8888), 0, 0));

            updateMenuLabels();
        }

        @Override
        public String getTitle() {
            return "Full screen: "+(fullScreen?"yes":"no");
        }
    }

    @Override
    public void render(float deltaTime) {
        stateTime += deltaTime;

        super.render(deltaTime);

        menuDrawingOffset = MathUtils.lerp(menuDrawingOffset, screenHeight / 2 - menu.getMenuIndex() * screenHeight / 7, 0.1f);

        UIBatch.begin();
        UIBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight,
                0+(stateTime%1), 0/*-(stateTime%1)*/+(MathUtils.sin(stateTime*4)/7),
                screenWidth/64+(stateTime%1), screenHeight/64/*-(stateTime%1)*/+(MathUtils.sin(stateTime*4)/7));

        drawMenuLabels();

        UIBatch.end();
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
        game.transitionTo(KartGame.Flow.MAIN_MENU_SCREEN);
    }

    @Override
    public void pausePressed() {

    }
}
