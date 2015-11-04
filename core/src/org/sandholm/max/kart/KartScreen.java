package org.sandholm.max.kart;

import com.badlogic.gdx.*;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class KartScreen extends UIScreen implements Screen, ContactListener {
	//KartGame game;

	World gameWorld;


	static float CAMERA_HEIGHT = 4f;
	static float SCREEN_SHAKE_SIZE = 0.5f;

	float stateTime = 0f;

	DecalBatch decalBatch;
	Texture groundTexture;
	PerspectiveCamera camera;
	float screenShake = 0;

	Level level;

	float cameraAngle;
	float cameraVerticalAngle;

	Decal groundDecal;
	ArrayList<Decal> kartDecal;

	Kart cameraFollowKart;
	ArrayList<Kart> karts;
	DumbAIGameController otherKartController;

	BitmapFont UIFont;

	public KartScreen(KartGame game) {
		super(game);
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

		for (Vector2[] box : level.getCollisions()) {
			shape.set(box);
			worldBody.createFixture(shape, 1);
		}

		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		groundTexture = level.getGroundTexture();
		groundDecal = Decal.newDecal(level.getGroundTexture().getWidth() / level.getScale(), level.getGroundTexture().getHeight() / level.getScale(), new TextureRegion(groundTexture));

		karts = new ArrayList<Kart>();
		kartDecal = new ArrayList<Decal>();

		otherKartController = new DumbAIGameController();

		Kart playerKart = new Kart("mario", level.getSpawnPoint().cpy(), level.getSpawnRotation(), gameWorld);
		playerKart.setController(game.kartGameController);
		cameraFollowKart = playerKart;

		cameraAngle = cameraFollowKart.getRotation();

		karts.add(playerKart);
		for (int i = 0; i < 20; i++) {
			Kart tempKart = new Kart("mario", new Vector2((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50), (float) Math.random() * 360, gameWorld);
			tempKart.setController(otherKartController);
			karts.add(tempKart);
		}
		for (int i = 0; i < 20; i++) {
			Kart tempKart = new Kart("tux", new Vector2((float) Math.random() * 100 - 50, (float) Math.random() * 100 - 50), (float) Math.random() * 360, gameWorld);
			tempKart.setController(otherKartController);
			karts.add(tempKart);
		}

		for (Kart k : karts) {
			kartDecal.add(Decal.newDecal(1.28f, 1.28f, k.getTextureRegionFromAngle(k.getRotation()), true));
		}

		UIFont = generateFont(0.08f);


	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	//TODO: this method could still be useful some day, and I should probably put it somewhere as a generic utility method
	/*static int degreesToFrame(float angle, int range) {
		return (((int)Math.floor((angle+360/(range*2))/(360/range))) % range + range ) % range;
	}*/

	@Override
	public void render(float deltaTime) {
		stateTime += deltaTime;

		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

		for (Kart k: karts) {
			k.update(deltaTime);
		}

		gameWorld.step(deltaTime, 6, 2);

		cameraAngle = MathUtils.radiansToDegrees*MathUtils.lerpAngle(MathUtils.degreesToRadians*cameraAngle, MathUtils.degreesToRadians*cameraFollowKart.getRotation(), 0.06f);
		cameraVerticalAngle = 10;
		camera.position.set(new Vector3(cameraFollowKart.getPosition(), 0).add(-8f, 0f, CAMERA_HEIGHT));
		camera.up.set(0f, 0f, 1f);
		camera.rotateAround(new Vector3(cameraFollowKart.getPosition(), 0), new Vector3(0f, 0f, 1f), cameraAngle);
		camera.lookAt(new Vector3(cameraFollowKart.getPosition(), CAMERA_HEIGHT));
		camera.rotate(camera.direction.cpy().rotate(camera.up, 90), cameraVerticalAngle);
		camera.update();

		UIBatch.begin();
		float rotatedOffset = (cameraAngle * level.backgroundRepetition) / (360);
		float screenRepetitions = level.backgroundRepetition*(camera.fieldOfView/360);
        float unitsPerDegree = screenRepetitions/camera.fieldOfView;
		UIBatch.draw(level.backgroundTexture, 0, 0, screenWidth, screenHeight,
				- rotatedOffset,
				0.5f+cameraVerticalAngle*unitsPerDegree*0.5f+((float)screenHeight/(float)screenWidth)*screenRepetitions*0.5f,
				screenRepetitions - rotatedOffset,
				0.5f+cameraVerticalAngle*unitsPerDegree*0.5f-((float)screenHeight/(float)screenWidth)*screenRepetitions*0.5f);
		UIBatch.end();

		for (int i=0; i<karts.size(); i++) {
			kartDecal.get(i).setTextureRegion(karts.get(i).getTextureRegionFromAngle(cameraAngle - karts.get(i).getRotation()));
			kartDecal.get(i).setPosition(karts.get(i).getPosition().x, karts.get(i).getPosition().y, kartDecal.get(i).getHeight() / 2);
			kartDecal.get(i).setRotation(camera.direction.cpy().scl(-1), Vector3.Z);
			decalBatch.add(kartDecal.get(i));
			karts.get(i).resetFrame();
		}
		decalBatch.add(groundDecal);
		decalBatch.flush();

		UIBatch.begin();
		drawText(UIFont, UIBatch, Gdx.graphics.getFramesPerSecond()+" fps", screenWidth, 0, Anchor.SE);
		UIBatch.end();
		//Gdx.graphics.setTitle(String.valueOf(Gdx.graphics.getFramesPerSecond()));
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

	//***
	//ContactListener
	//***

	@Override
	public void beginContact(Contact contact) {

	}

	@Override
	public void endContact(Contact contact) {

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {

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
