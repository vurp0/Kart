package org.sandholm.max.kart.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.UIController;

/**
 * Created by max on 2016-01-30.
 */
public class ControllerMappingScreen extends UIScreen implements Screen, UIController, ControllerListener {

    Texture backgroundTexture;

    BitmapFont uiFont;

    Group statusLabel;

    public ControllerMappingScreen(KartGame game) {
        super(game);
    }

    @Override
    public void show() {

        backgroundTexture = new Texture(Gdx.files.internal("background.png")); //TODO: hardcoded placeholder background
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        uiFont = generateFont(screenHeight/6);
        statusLabel = new Group();
        Label tmpStatusLabel = new Label("Press button on controller", new Label.LabelStyle(uiFont, Color.WHITE));
        tmpStatusLabel.setPosition(0,0, Align.center);
        statusLabel.addActor(tmpStatusLabel);
        statusLabel.setPosition(screenWidth/2f, screenHeight/2f, Align.center);
        statusLabel.setOrigin(Align.center);

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        uiFont = generateFont(screenHeight/6);

        Controllers.addListener(this);
    }

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);

        UIBatch.begin();
        UIBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight,
                0, 0,
                screenWidth/64, screenHeight/64);
        statusLabel.draw(UIBatch, 1);
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

    }

    @Override
    public void backPressed() {

    }

    @Override
    public void pausePressed() {

    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
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
