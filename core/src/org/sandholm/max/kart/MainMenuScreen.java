package org.sandholm.max.kart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * Main menu screen
 */
public class MainMenuScreen extends UIScreen implements Screen, InputProcessor, ControllerListener {
    private BitmapFont titleFont;
    private Group titleLabel;

    private BitmapFont subTitleFont;
    private Group subtitleLabel;

    private float stateTime;


    public MainMenuScreen(KartGame game) {
        super(game);
    }

    @Override
    public void show() {
        stateTime = 0f;
        titleFont = generateFont(0.13f);
        titleLabel = new Group();
        Label tmpTitleLabel = new Label("Epic Kart Racing Game", new Label.LabelStyle(titleFont, Color.WHITE));
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
    public void render(float delta) {
        stateTime += delta;

        Gdx.gl.glClearColor(0.4f, 0.5f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        titleLabel.setRotation(MathUtils.sin(stateTime*1.8f)*4f);
        subtitleLabel.setScale(1+0.1f*MathUtils.sin(stateTime*2.5f));

        UIBatch.begin();
        titleFont.setColor(Color.WHITE);
        titleLabel.draw(UIBatch, 1);
        subtitleLabel.draw(UIBatch, 1);
        //drawText(titleFont, UIBatch, "Epic Kart Racing Game", screenWidth/2, screenHeight/2, Anchor.CENTER);
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
        game.setScreen(game.gameControllerSelectScreen);
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
        game.setScreen(game.gameControllerSelectScreen);
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
