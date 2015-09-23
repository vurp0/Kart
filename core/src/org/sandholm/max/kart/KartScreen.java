package org.sandholm.max.kart;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class KartScreen implements Screen {
	KartGame game;

	static float CAMERA_HEIGHT = 3f;

	float stateTime = 0f;

	DecalBatch decalBatch;
	Texture groundTexture;
	Texture kartTextureSheet;
	TextureRegion[] kartTextureRegions;
	PerspectiveCamera camera;

	float cameraAngle;

	Decal groundDecal;
	Decal kartDecal;
	ArrayList<Decal> otherKartDecal;

    MyInputProcessor inputProcessor;

	Kart playerKart;
	ArrayList<Kart> otherKarts;

	public KartScreen(KartGame game) {
		this.game = game;
	}

	@Override
	public void show() {

		float fieldOfView = 45;
		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		camera = new PerspectiveCamera(fieldOfView, 1, h / w);
		camera.position.set(0f, 0f, 1f);
		camera.lookAt(0f, 1f, 1f);
		camera.near = 0.1f;
		camera.far = 10000f;
		camera.update();

		//spriteBatch = new SpriteBatch();
		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		groundTexture = new Texture("ground2.png");
		kartTextureSheet = new Texture("supermariokart_characters_sheet.png");
		kartTextureRegions = new TextureRegion[22];
		kartTextureRegions[0] = new TextureRegion(kartTextureSheet, 1 * 40, 0 * 40, 32, 30);        //this huge mass will maybe eventually be replaced with automation
		kartTextureRegions[1] = new TextureRegion(kartTextureSheet, 2 * 40, 0 * 40, 32, 30);
		kartTextureRegions[2] = new TextureRegion(kartTextureSheet, 3 * 40, 0 * 40, 32, 30);
		kartTextureRegions[3] = new TextureRegion(kartTextureSheet, 4 * 40, 0 * 40, 32, 30);
		kartTextureRegions[4] = new TextureRegion(kartTextureSheet, 5 * 40, 0 * 40, 32, 30);
		kartTextureRegions[5] = new TextureRegion(kartTextureSheet, 0 * 40, 1 * 40, 32, 30);
		kartTextureRegions[6] = new TextureRegion(kartTextureSheet, 1 * 40, 1 * 40, 32, 30);
		kartTextureRegions[7] = new TextureRegion(kartTextureSheet, 2 * 40, 1 * 40, 32, 30);
		kartTextureRegions[8] = new TextureRegion(kartTextureSheet, 3 * 40, 1 * 40, 32, 30);
		kartTextureRegions[9] = new TextureRegion(kartTextureSheet, 4 * 40, 1 * 40, 32, 30);
		kartTextureRegions[10] = new TextureRegion(kartTextureSheet, 5 * 40, 1 * 40, 32, 30);
		kartTextureRegions[11] = new TextureRegion(kartTextureSheet, 0 * 40, 0 * 40, 32, 30);
		kartTextureRegions[12] = new TextureRegion(kartTextureSheet, 5 * 40 + 32, 1 * 40, -32, 30);
		kartTextureRegions[13] = new TextureRegion(kartTextureSheet, 4 * 40 + 32, 1 * 40, -32, 30);
		kartTextureRegions[14] = new TextureRegion(kartTextureSheet, 3 * 40 + 32, 1 * 40, -32, 30);
		kartTextureRegions[15] = new TextureRegion(kartTextureSheet, 2 * 40 + 32, 1 * 40, -32, 30);
		kartTextureRegions[16] = new TextureRegion(kartTextureSheet, 1 * 40 + 32, 1 * 40, -32, 30);
		kartTextureRegions[17] = new TextureRegion(kartTextureSheet, 0 * 40 + 32, 1 * 40, -32, 30);
		kartTextureRegions[18] = new TextureRegion(kartTextureSheet, 5 * 40 + 32, 0 * 40, -32, 30);
		kartTextureRegions[19] = new TextureRegion(kartTextureSheet, 4 * 40 + 32, 0 * 40, -32, 30);
		kartTextureRegions[20] = new TextureRegion(kartTextureSheet, 3 * 40 + 32, 0 * 40, -32, 30);
		kartTextureRegions[21] = new TextureRegion(kartTextureSheet, 2 * 40 + 32, 0 * 40, -32, 30);

		groundDecal = Decal.newDecal(100, 100, new TextureRegion(groundTexture));
		kartDecal = Decal.newDecal(1, 0.9375f, kartTextureRegions[0], true);

		playerKart = new Kart(100f, new Vector2(0, 0), 0);

		otherKarts = new ArrayList<Kart>();
		otherKartDecal = new ArrayList<Decal>();

		for (int i = 0; i < 120; i++) {
			otherKarts.add(new Kart(0f, new Vector2((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50), (float) Math.random() * 360));
		}
		for (int i = 0; i < 120; i++) {
			otherKartDecal.add(Decal.newDecal(1, 0.9375f, kartTextureRegions[0], true));
		}

		//inputProcessor = new MyInputProcessor();
		//Gdx.input.setInputProcessor(inputProcessor);
	}

	@Override
	public void resize(int width, int height) {

	}

	int degreesToFrame(float angle, int range) {
		return (((int)Math.floor((angle+360/(range*2))/(360/range))) % range + range ) % range;
	}

	@Override
	public void render(float deltaTime) {

        //float deltaTime = Gdx.graphics.getDeltaTime();
		stateTime += deltaTime;

		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			playerKart.brake();
		}
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
			playerKart.accelerate();
		}
		playerKart.update(deltaTime);

		cameraAngle = MathUtils.radiansToDegrees*MathUtils.lerpAngle(MathUtils.degreesToRadians*cameraAngle, MathUtils.degreesToRadians*playerKart.getRotation(), 0.05f);

		camera.position.set(new Vector3(playerKart.getPosition(), 0).add(-7f, 0f, CAMERA_HEIGHT));
		camera.up.set(0f, 0f, 1f);
		camera.rotateAround(new Vector3(playerKart.getPosition(), 0), new Vector3(0f, 0f, 1f), cameraAngle);
		camera.lookAt(new Vector3(playerKart.getPosition(), CAMERA_HEIGHT-1.5f));
		camera.update();


		Gdx.gl.glClearColor(0.4f, 0.5f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		decalBatch.add(groundDecal);
		kartDecal.setTextureRegion(kartTextureRegions[degreesToFrame(cameraAngle - playerKart.getRotation(), 22)]);
		kartDecal.setPosition(playerKart.getPosition().x, playerKart.getPosition().y, 0.5f);
		kartDecal.setRotation(camera.direction.cpy().scl(-1), Vector3.Z);

		decalBatch.add(kartDecal);
		for (int i=0; i<120; i++) {
			otherKarts.get(i).accelerate();
			otherKarts.get(i).turn(Kart.Direction.LEFT, deltaTime, true);
			otherKarts.get(i).update(deltaTime);
			otherKarts.get(i).stopAccelerating();
			otherKarts.get(i).stopBraking();

			otherKartDecal.get(i).setTextureRegion(kartTextureRegions[degreesToFrame(cameraAngle - otherKarts.get(i).getRotation(), 22)]);
			otherKartDecal.get(i).setPosition(otherKarts.get(i).getPosition().x, otherKarts.get(i).getPosition().y, 0.5f);
			otherKartDecal.get(i).setRotation(camera.direction.cpy().scl(-1), Vector3.Z);
			decalBatch.add(otherKartDecal.get(i));
		}
		decalBatch.flush();
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
	public void hide() {

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
