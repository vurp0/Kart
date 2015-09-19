package org.sandholm.max.kart;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class KartGame implements ApplicationListener {
	static float CAMERA_HEIGHT = 1.5f;

	SpriteBatch batch;
	Texture img;
	PerspectiveCamera camera;

    MyInputProcessor inputProcessor;

	Kart playerKart;

	public void create () {

		float fieldOfView = 45;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new PerspectiveCamera(fieldOfView, 1, h/w);
		camera.position.set(0f, 0f, 1f);
		camera.lookAt(0f, 1f, 1f);
		camera.near = 0.1f;
		camera.far = 10000f;
		camera.update();

		batch = new SpriteBatch();
		img = new Texture("ground.png");

		playerKart = new Kart(100f, new Vector2(90,40), 90);

        inputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
        float deltaTime = Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			playerKart.brake();
		}/*
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
			camera.translate((float) -Math.cos(Math.toRadians(angle))*MOVEMENT_SPEED, (float) -Math.sin(Math.toRadians(angle))*MOVEMENT_SPEED, 0f);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
			camera.translate((float) Math.cos(Math.toRadians(angle))*MOVEMENT_SPEED, (float) Math.sin(Math.toRadians(angle))*MOVEMENT_SPEED, 0f);
		}*/
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerKart.turn(Kart.Direction.LEFT, deltaTime, false);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerKart.turn(Kart.Direction.RIGHT, deltaTime, false);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			playerKart.turn(Kart.Direction.LEFT, deltaTime, true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			playerKart.turn(Kart.Direction.RIGHT, deltaTime, true);
		}

		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			//camera.translate((float) -Math.sin(Math.toRadians(angle))*MOVEMENT_SPEED, (float) Math.cos(Math.toRadians(angle))*MOVEMENT_SPEED, 0f);
			playerKart.accelerate();
		}
		playerKart.update(deltaTime);

		camera.position.set(new Vector3(playerKart.getPosition(), 0).add(-5f, 0f, CAMERA_HEIGHT));
		camera.up.set(0f,0f,1f);
        camera.rotateAround(new Vector3(playerKart.getPosition(), 0), new Vector3(0f, 0f, 1f), playerKart.getRotation());
		camera.lookAt(new Vector3(playerKart.getPosition(), CAMERA_HEIGHT));
		camera.update();

		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		batch.draw(img, 0f, 0f, 100f, 100f);
		batch.end();
		Gdx.graphics.setTitle(String.valueOf(Gdx.graphics.getFramesPerSecond()));

		playerKart.stopAccelerating();
		playerKart.stopBraking();

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}

	class MyInputProcessor implements InputProcessor {

		@Override
		public boolean keyDown(int keycode) {
			return false;
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
}
