package org.sandholm.max.kart.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.gamecontroller.ControllerGameController;
import org.sandholm.max.kart.gamecontroller.KeyboardGameController;

/**
 * Title screen
 * This class also detects the type of input and creates an appropriate GameController for use in the game
 */
public class TitleScreen extends UIScreen implements Screen, InputProcessor, ControllerListener {
    private BitmapFont titleFont;
    private Group titleLabel;

    private BitmapFont subTitleFont;
    private Group subtitleLabel;

    private Texture backgroundTexture;

    private float stateTime;

    public TitleScreen(KartGame game) {
        super(game);
    }

    @Override
    public void show() {
        stateTime = 0f;

        backgroundTexture = new Texture(Gdx.files.internal("maps/testlevel/background.png")); //TODO: get a real background here, not this hardcoded placeholder

        titleFont = generateFont(0.13f);
        titleLabel = new Group();
        Label tmpTitleLabel = new Label("Game", new Label.LabelStyle(titleFont, Color.WHITE));
        tmpTitleLabel.setPosition(0,0,Align.center);
        titleLabel.addActor(tmpTitleLabel);
        titleLabel.setPosition(screenWidth*0.5f, screenHeight*0.6f, Align.center);
        titleLabel.setOrigin(Align.center);

        subTitleFont = generateFont(0.06f);
        subtitleLabel = new Group();
        Label tmpSubtitleLabel = new Label("Press button", new Label.LabelStyle(subTitleFont, Color.WHITE));
        tmpSubtitleLabel.setPosition(0,0,Align.center);
        subtitleLabel.addActor(tmpSubtitleLabel);
        subtitleLabel.setPosition(screenWidth*0.5f, screenHeight*0.3f, Align.center);
        subtitleLabel.setOrigin(Align.center);

        game.multiplexer.addProcessor(this);
        Controllers.addListener(this);

    }


    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        stateTime += deltaTime;

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        UIBatch.begin();
        UIBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight);
        titleFont.setColor(Color.WHITE);
        titleLabel.draw(UIBatch, 1);
        subtitleLabel.draw(UIBatch, 1);
        UIBatch.end();

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        titleLabel.setPosition(screenWidth*0.5f, screenHeight*0.6f, Align.center);
        titleLabel.setOrigin(Align.center);
        subtitleLabel.setPosition(screenWidth*0.5f, screenHeight*0.3f, Align.center);
        subtitleLabel.setOrigin(Align.center);
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
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        game.multiplexer.clear();
        Controllers.clearListeners();
        ControllerGameController gameController = new ControllerGameController(controller);
        Controllers.addListener(gameController);
        game.setKartGameController(gameController);
        game.transitionTo(KartGame.Flow.MAIN_MENU_SCREEN);
        return true;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        game.multiplexer.clear();
        Controllers.clearListeners();
        KeyboardGameController controller = new KeyboardGameController();
        game.multiplexer.addProcessor(controller);
        game.setKartGameController(controller);
        game.transitionTo(KartGame.Flow.MAIN_MENU_SCREEN);
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
