package org.sandholm.max.kart;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class KartScreen implements Screen {
	KartGame game;

	World gameWorld;


	static float CAMERA_HEIGHT = 4f;

	float stateTime = 0f;

	DecalBatch decalBatch;
	Texture groundTexture;
	Texture kartTextureSheet;
	TextureRegion[] kartTextureRegions;
	PerspectiveCamera camera;

	Level level;

	float cameraAngle;

	Decal groundDecal;
	Decal kartDecal;
	ArrayList<Decal> otherKartDecal;

    MyInputProcessor inputProcessor;
	Controller playerController;

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

		level = new Level("testlevel");

		gameWorld = new World(Vector2.Zero, true);

		BodyDef worldBodyDef = new BodyDef();
		worldBodyDef.type = BodyDef.BodyType.StaticBody;
		worldBodyDef.position.set(0,0);
		PolygonShape shape = new PolygonShape();
		Body worldBody = gameWorld.createBody(worldBodyDef);
		shape.set(new Vector2[]{new Vector2(-63,-64), new Vector2(-64, -64), new Vector2(-64, 64), new Vector2(-63, 64)});
		worldBody.createFixture(shape, 1);
		shape.set(new Vector2[]{new Vector2(-64,-64), new Vector2(64,-64), new Vector2(64,-63), new Vector2(-64,-63)});
		worldBody.createFixture(shape, 1);
		shape.set(new Vector2[]{new Vector2(-64, 64), new Vector2(64, 64), new Vector2(64, 63), new Vector2(-64, 63)});
		worldBody.createFixture(shape, 1);
		shape.set(new Vector2[]{new Vector2(63,-64), new Vector2(64, -64), new Vector2(64, 64), new Vector2(63, 64)});
		worldBody.createFixture(shape, 1);

		//spriteBatch = new SpriteBatch();
		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		groundTexture = level.getGroundTexture();
		kartTextureSheet = new Texture("mario.png");
		kartTextureRegions = new TextureRegion[22];
		for (int i=0; i<=11; i++){  //make TextureRegions from the normal sprites
			kartTextureRegions[i] = new TextureRegion(kartTextureSheet, i*32, 0, 32, 32);
		}
		for (int i=12; i<=21; i++){ //make TextureRegions from the sprites flipped to make the complete circle
			kartTextureRegions[i] = new TextureRegion(kartTextureSheet, (22-i)*32+32, 0, -32, 32);
		}

		groundDecal = Decal.newDecal(128, 128, new TextureRegion(groundTexture));
		kartDecal = Decal.newDecal(1.28f, 1.28f, kartTextureRegions[0], true);

		playerKart = new Kart(level.getSpawnPoint().cpy(), level.getSpawnRotation(), gameWorld);

		otherKarts = new ArrayList<Kart>();
		otherKartDecal = new ArrayList<Decal>();

		for (int i = 0; i < 20; i++) {
			otherKarts.add(new Kart(new Vector2((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50), (float) Math.random() * 360, gameWorld));
		}
		for (int i = 0; i < otherKarts.size(); i++) {
			otherKartDecal.add(Decal.newDecal(1.28f, 1.28f, kartTextureRegions[0], true));
		}

		if (Controllers.getControllers().size >= 1) {
			playerController = Controllers.getControllers().first();
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

		/*if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			playerKart.brake();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerKart.turn(1, deltaTime, false);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerKart.turn(-1, deltaTime, false);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
			playerKart.turn(1, deltaTime, true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.E)) {
			playerKart.turn(-1, deltaTime, true);
		}
		if (Gdx.input.isKeyPressed(Input.Keys.W)) {
			playerKart.accelerate();
		}*/

		playerKart.turn((Math.abs(playerController.getAxis(0)) > 0.1f ? -playerController.getAxis(0) : 0), deltaTime, playerController.getButton(5));
		if (playerController.getButton(1)){
			playerKart.accelerate();
		}
		if (playerController.getButton(2)){
			playerKart.brake();
		}

		playerKart.update(deltaTime);

		gameWorld.step(deltaTime, 6, 2);

		//cameraAngle = playerKart.getRotation();
		cameraAngle = MathUtils.radiansToDegrees*MathUtils.lerpAngle(MathUtils.degreesToRadians*cameraAngle, MathUtils.degreesToRadians*playerKart.getRotation(), 0.06f);

		camera.position.set(new Vector3(playerKart.getPosition(), 0).add(-8f, 0f, CAMERA_HEIGHT));
		camera.up.set(0f, 0f, 1f);
		camera.rotateAround(new Vector3(playerKart.getPosition(), 0), new Vector3(0f, 0f, 1f), cameraAngle);
		camera.lookAt(new Vector3(playerKart.getPosition(), CAMERA_HEIGHT - 1.5f));
		camera.update();

		Gdx.gl.glClearColor(0.4f, 0.5f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		decalBatch.add(groundDecal);
		kartDecal.setTextureRegion(kartTextureRegions[degreesToFrame(cameraAngle - playerKart.getRotation(), 22)]);
		kartDecal.setPosition(playerKart.getPosition().x, playerKart.getPosition().y, kartDecal.getHeight()/2);
		kartDecal.setRotation(camera.direction.cpy().scl(-1), Vector3.Z);


		decalBatch.add(kartDecal);
		for (int i=0; i<otherKarts.size(); i++) {
			otherKarts.get(i).accelerate();
			otherKarts.get(i).turn(-1, deltaTime, false);
			otherKarts.get(i).update(deltaTime);
			otherKarts.get(i).stopAccelerating();
			otherKarts.get(i).stopBraking();

			otherKartDecal.get(i).setTextureRegion(kartTextureRegions[degreesToFrame(cameraAngle - otherKarts.get(i).getRotation(), 22)]);
			otherKartDecal.get(i).setPosition(otherKarts.get(i).getPosition().x, otherKarts.get(i).getPosition().y, kartDecal.getHeight()/2);
			otherKartDecal.get(i).setRotation(camera.direction.cpy().scl(-1), Vector3.Z);
			decalBatch.add(otherKartDecal.get(i));
		}
		decalBatch.flush();
		Gdx.graphics.setTitle(String.valueOf(Gdx.graphics.getFramesPerSecond()));

		playerKart.stopAccelerating();
		playerKart.stopBraking();
		playerKart.resetFrame();

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
