package org.sandholm.max.kart.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
        menu.menuItems.add(new SettingsMenuItem("Settings"));
        menu.menuItems.add(new QuitMenuItem("Quit game"));

        updateMenuLabels();
    }

    class StartGameMenuItem extends BaseMenuItem {
        public StartGameMenuItem(String title) {
            super(title);
        }

        @Override
        public void select() {
            game.transitionTo(KartGame.Flow.GAME_SCREEN);
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
            Gdx.app.exit();
        }
    }

    @Override
    public void render(float deltaTime) {
        stateTime += deltaTime;

        super.render(deltaTime);

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

    }

    @Override
    public void pausePressed() {

    }
}
