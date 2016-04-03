package org.sandholm.max.kart.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.gamecontroller.ControllerGameController;
import org.sandholm.max.kart.gamecontroller.KeyboardGameController;
import org.sandholm.max.kart.tweenaccessors.UIScreenAccessor;

/**
 * Created by max on 4/3/16.
 */
public class LowResMainMenuScreen extends UIScreen implements Screen, InputProcessor, ControllerListener {

    private float stateTime;
    private Texture backgroundTexture;

    public LowResMainMenuScreen(KartGame game) {
        super(game);

        backgroundTexture = new Texture(Gdx.files.internal("background.png")); //TODO: hardcoded placeholder background
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float deltaTime) {
        stateTime += deltaTime;

        UIShader.begin();
        UIShader.setUniformf("darkness", UIDarkness);
        UIShader.end();
        if (!fadeInStarted && deltaTime < 1f/30f) {
            fadeInStarted = true;
            Tween.to(this, UIScreenAccessor.FULL_SCREEN_DARKNESS, 1f).target(1f).ease(TweenEquations.easeOutQuart).start(game.tweenManager).setCallback(new FadeInTweenCallback()).setCallbackTriggers(TweenCallback.COMPLETE);
        }
        game.tweenManager.update(deltaTime);

        UIFrameBuffer.begin();
            Gdx.gl.glCullFace(GL20.GL_BACK);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
            Gdx.gl.glDepthMask(true);

            //DO SOMETHING
            UIBatch.begin();
                UIBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight,
                        0+(stateTime%1), 0/*-(stateTime%1)*/+(MathUtils.sin(stateTime*4)/7),
                        screenWidth/64+(stateTime%1), screenHeight/64/*-(stateTime%1)*/+(MathUtils.sin(stateTime*4)/7));
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
}
