package org.sandholm.max.kart.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.UIController;

/**
 * Created by max on 12.12.2015.
 */
public class MainMenuScreen extends UIScreen implements Screen, UIController {

    Texture backgroundTexture;

    public MainMenuScreen(KartGame game) {
        super(game);

    }

    //Screen:
    @Override
    public void show() {

        backgroundTexture = new Texture(Gdx.files.internal("background.png")); //TODO: hardcoded placeholder background

    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        UIBatch.begin();
        UIBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
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

    //UIController:
    @Override
    public void upPressed() {

    }

    @Override
    public void downPressed() {

    }

    @Override
    public void leftPressed() {

    }

    @Override
    public void rightPressed() {

    }

    @Override
    public void OKPressed() {
        game.transitionTo(KartGame.Flow.GAME_SCREEN);
    }

    @Override
    public void backPressed() {

    }

    @Override
    public void pausePressed() {

    }
}
