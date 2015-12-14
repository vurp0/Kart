package org.sandholm.max.kart.screens;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Timer;
import org.sandholm.max.kart.*;
import org.sandholm.max.kart.gamecontroller.DumbAIGameController;
import org.sandholm.max.kart.tweenaccessors.UIScreenAccessor;

import java.util.ArrayList;

public class KartGameScreen extends UIScreen implements Screen, ContactListener, UIController {
    //KartGame game;
    enum GameState{RUNNING,PAUSING,PAUSED,UNPAUSING,STARTING}

    World gameWorld;

    float runningStateTime = 0f;
    GameState gameState = GameState.STARTING;

    static float CAMERA_HEIGHT = 4f;

    CameraGroupStrategy groupStrategy;
    //DecalBatch decalBatch;
    ModelBatch quadBatch;
    //Decal groundDecal;
    //ArrayList<Decal> kartDecals;

    BaseQuad groundQuad;
    ArrayList<BaseQuad> kartQuads;

    SpriteBatch skyBatch;

    Texture groundTexture;
    PerspectiveCamera camera;
    ShaderProgram shaderProgram;

    float gameSceneDarkness = 0f;

    @Override
    public float getFullScreenDarkness() {
        return UIDarkness;
    }
    @Override
    public void setFullScreenDarkness(float darkness) {
        this.gameSceneDarkness = darkness;
        this.UIDarkness = darkness;
    }
    @Override
    public float getBackgroundDarkness() {
        return gameSceneDarkness;
    }
    @Override
    public void setBackgroundDarkness(float darkness) {
        this.gameSceneDarkness = darkness;
    }

    float cameraFOV = 45f;
    float FOVIntensifier = 0f;

    Shake shake;

    GameMap gameMap;

    float cameraAngle;
    float cameraVerticalAngle;


    Kart cameraFollowKart;
    ArrayList<Kart> karts;
    DumbAIGameController otherKartController;

    BitmapFont UIFont;

    public KartGameScreen(KartGame game) {
        super(game);
    }

    @Override
    public void show() {

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new PerspectiveCamera(cameraFOV, 1, h / w);
        camera.position.set(0f, 0f, 1f);
        camera.lookAt(0f, 1f, 1f);
        camera.near = 0.1f;
        camera.far = 10000f;
        camera.update();

        String vertexShader = Gdx.files.internal("shaders/kartgame.vert").readString();
        String fragmentShader = Gdx.files.internal("shaders/kartgame.frag").readString();
        shaderProgram = new ShaderProgram(vertexShader, fragmentShader);
        if (!shaderProgram.isCompiled()) throw new IllegalArgumentException("couldn't compile shader: " + shaderProgram.getLog());


        UIDarkness = 0f;

        gameMap = new GameMap("testlevel");
        gameWorld = new World(Vector2.Zero, true);

        Body worldBody = gameMap.createBody(gameWorld);
        worldBody.setTransform(0,0,0);
        gameWorld.setContactListener(this);

        //groupStrategy = new KartGameCameraGroupStrategy(camera, shaderProgram);
        //decalBatch = new DecalBatch(groupStrategy);
        quadBatch = new ModelBatch();
        groundTexture = gameMap.getGroundTexture();
        //groundDecal = Decal.newDecal(gameMap.getGroundTexture().getWidth() / gameMap.getScale(), gameMap.getGroundTexture().getHeight() / gameMap.getScale(), new TextureRegion(groundTexture));
        groundQuad = new BaseQuad(new TextureRegion(groundTexture), gameMap.getGroundTexture().getWidth() / gameMap.getScale(), gameMap.getGroundTexture().getHeight() / gameMap.getScale());
        groundQuad.worldTransform.setToTranslation(groundQuad.getWidth()/2,groundQuad.getHeight()/2,0);
        //groundDecal.setPosition();
        Shader shader = new DefaultShader(groundQuad, new DefaultShader.Config(vertexShader, fragmentShader));
        //shader

        skyBatch = new SpriteBatch();

        karts = new ArrayList<>();
        //kartDecals = new ArrayList<>();
        kartQuads = new ArrayList<>();

        otherKartController = new DumbAIGameController();

        Kart playerKart = new Kart("mario", gameMap.getSpawnPoint().cpy(), gameMap.getSpawnRotation(), gameWorld);
        playerKart.setController(game.kartGameController);
        cameraFollowKart = playerKart;

        cameraAngle = cameraFollowKart.getRotation();

        karts.add(playerKart);
        for (int i = 0; i < 20; i++) {
            Kart tempKart = new Kart("mario", new Vector2((float) Math.random() * 100, (float) Math.random() * 100), (float) Math.random() * 360, gameWorld);
            tempKart.setController(otherKartController);
            karts.add(tempKart);
        }
        for (int i = 0; i < 20; i++) {
            Kart tempKart = new Kart("tux", new Vector2((float) Math.random() * 100, (float) Math.random() * 100), (float) Math.random() * 360, gameWorld);
            tempKart.setController(otherKartController);
            karts.add(tempKart);
        }

        for (Kart k : karts) {
            //kartDecals.add(Decal.newDecal(1.28f, 1.28f, k.getTextureRegionFromAngle(k.getRotation()), true));
            kartQuads.add(new BaseQuad(k.getTextureRegionFromAngle(k.getRotation()), 1.28f, 1.28f));
        }

        UIFont = generateFont(0.08f);
        shake = new Shake();
        skyBatch.setShader(shaderProgram);

        updateCamera(0);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
            }
        }, 2f);

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        skyBatch.setProjectionMatrix(UIProjection);

        camera.viewportWidth = width/(float)height;
        camera.viewportHeight = 1;

        shaderProgram.begin();
        shaderProgram.setUniformf("u_resolution", width, height);
        shaderProgram.end();
    }

    //TODO: this method could still be useful some day, and I should probably put it somewhere as a generic utility method
    /*static int degreesToFrame(float angle, int range) {
        return (((int)Math.floor((angle+360/(range*2))/(360/range))) % range + range ) % range;
    }*/

    @Override
    public void render(float deltaTime) {
        super.render(deltaTime);
        switch (gameState) {
            case RUNNING:
                gameRender(deltaTime);
                break;
            case PAUSING:
                pausingRender(deltaTime);
                break;
            case PAUSED:
                pausedRender(deltaTime);
                break;
            case UNPAUSING:
                unpausingRender(deltaTime);
                break;
            case STARTING:
                startingRender(deltaTime);
                break;
        }
    }

    public void gameRender(float deltaTime) {

        runningStateTime += deltaTime;

        for (Kart k : karts) {
            k.update(deltaTime);
        }

        gameWorld.step(deltaTime, 6, 2);

        updateCamera(deltaTime);

        drawKartScene();
    }



    public void pausingRender(float deltaTime) {
        drawKartScene();
    }
    class PausingTweenCallback implements TweenCallback {
        @Override
        public void onEvent(int eventType, BaseTween<?> source) {
            switch (eventType) {
                case TweenCallback.COMPLETE:
                    gameState = GameState.PAUSED;
            }
        }
    }

    public void pausedRender(float deltaTime) {
        drawKartScene();

    }

    public void unpausingRender(float deltaTime) {
        drawKartScene();
    }
    class UnpausingTweenCallback implements TweenCallback {
        @Override
        public void onEvent(int eventType, BaseTween<?> source) {
            switch (eventType) {
                case TweenCallback.COMPLETE:
                    gameState = GameState.RUNNING;
            }
        }
    }

    public void startingRender(float deltaTime) {
        drawKartScene();
    }
    @Override
    public void fadeInEnded() {
        gameState = GameState.RUNNING;
    }

    public void updateCamera(float deltaTime) {
        FOVIntensifier = cameraFollowKart.getBody().getLinearVelocity().len()*0.9f;
        cameraAngle = MathUtils.radiansToDegrees * MathUtils.lerpAngle(MathUtils.degreesToRadians * cameraAngle, MathUtils.degreesToRadians * cameraFollowKart.getRotation(), 0.06f);
        camera.fieldOfView = MathUtils.lerp(camera.fieldOfView, cameraFOV + FOVIntensifier, 0.1f);
        cameraVerticalAngle = 10;
        camera.position.set(new Vector3(cameraFollowKart.getPosition(), 0).add(-8f, 0f, CAMERA_HEIGHT));
        camera.up.set(0f, 0f, 1f);
        camera.rotateAround(new Vector3(cameraFollowKart.getPosition(), 0), new Vector3(0f, 0f, 1f), cameraAngle);
        camera.lookAt(new Vector3(cameraFollowKart.getPosition(), CAMERA_HEIGHT));
        camera.rotate(camera.direction.cpy().rotate(camera.up, 90), cameraVerticalAngle);
        shake.update(deltaTime, camera, camera.position);
        camera.update();

    }

    public void drawKartScene() {
        shaderProgram.begin();
        shaderProgram.setUniformf("fadeDark", getBackgroundDarkness());
        shaderProgram.end();

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        skyBatch.begin();
        float aspectRatio = (float)screenHeight/screenWidth;
        float rotatedOffset = (cameraAngle/360) * gameMap.backgroundRepetition;
        float screenRepetitions = ((camera.fieldOfView/aspectRatio)/360f)*gameMap.backgroundRepetition;
        float unitsPerDegree = (screenRepetitions*aspectRatio)/camera.fieldOfView;
        skyBatch.draw(gameMap.backgroundTexture, 0, 0, screenWidth, screenHeight,
                -0.5f*screenRepetitions- rotatedOffset,
                0.5f+((camera.fieldOfView*unitsPerDegree)/2 + cameraVerticalAngle*unitsPerDegree),
                0.5f*screenRepetitions - rotatedOffset,
                0.5f-((camera.fieldOfView*unitsPerDegree)/2 - cameraVerticalAngle*unitsPerDegree));
        skyBatch.end();

        quadBatch.begin(camera);
        for (int i=0; i<karts.size(); i++) {
            /*kartDecals.get(i).setTextureRegion(karts.get(i).getTextureRegionFromAngle(cameraAngle - karts.get(i).getRotation()));
            kartDecals.get(i).setPosition(karts.get(i).getPosition().x, karts.get(i).getPosition().y, kartDecals.get(i).getHeight() / 2);
            kartDecals.get(i).setRotation(camera.direction.cpy().scl(-1), Vector3.Z);
            decalBatch.add(kartDecals.get(i));*/
            kartQuads.get(i).setTextureRegion(karts.get(i).getTextureRegionFromAngle(cameraAngle - karts.get(i).getRotation()));
            kartQuads.get(i).worldTransform.setToTranslation(karts.get(i).getPosition().x, karts.get(i).getPosition().y, kartQuads.get(i).getHeight() / 2);
            kartQuads.get(i).setDecalRotation(camera.direction.cpy().scl(-1), Vector3.Z);
            //kartQuads.get(i).worldTransform.rotate(camera.up.cpy().rotate(camera.direction, 90), 90);
            //kartQuads.get(i).worldTransform.setToRotation(Vector3.Z, cameraAngle-90);
            quadBatch.render(kartQuads.get(i));
            karts.get(i).resetFrame();
        }
        //decalBatch.add(groundDecal);
        quadBatch.render(groundQuad);
        quadBatch.end();
        //decalBatch.flush();

        UIBatch.begin();
        drawText(UIFont, UIBatch, String.valueOf(Gdx.graphics.getFramesPerSecond())+" fps", screenWidth, 0, Anchor.SE);
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
        quadBatch.dispose();
    }

    //***
    //ContactListener
    //***

    @Override
    public void beginContact(Contact contact) {

        if (contact.getFixtureA().getBody().getUserData() instanceof Kart) {
            if (contact.getFixtureB().getUserData() == GameMap.GroundType.SLOW) {
                ((Kart)contact.getFixtureA().getBody().getUserData()).currentContacts += 1;
            } else if (contact.getFixtureB().getUserData() == GameMap.GroundType.HOLLOW) {
                ((Kart)contact.getFixtureA().getBody().getUserData()).resetPosition((float)Math.random()*64f, (float)Math.random()*64f, (float)Math.random()*360f);
            }
        } else if (contact.getFixtureB().getBody().getUserData() instanceof Kart) {
            if (contact.getFixtureA().getUserData() == GameMap.GroundType.SLOW) {
                ((Kart)contact.getFixtureB().getBody().getUserData()).currentContacts += 1;
            } else if (contact.getFixtureA().getUserData() == GameMap.GroundType.HOLLOW) {
                ((Kart)contact.getFixtureB().getBody().getUserData()).resetPosition((float)Math.random()*64f, (float)Math.random()*64f, (float)Math.random()*360f);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getBody().getUserData() instanceof Kart) {
            if (contact.getFixtureB().getUserData() == GameMap.GroundType.SLOW) {
                ((Kart)contact.getFixtureA().getBody().getUserData()).currentContacts -= 1;
            }
        } else if (contact.getFixtureB().getBody().getUserData() instanceof Kart) {
            if (contact.getFixtureA().getUserData() == GameMap.GroundType.SLOW) {
                ((Kart)contact.getFixtureB().getBody().getUserData()).currentContacts -= 1;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        if(compareAB(karts.get(0), contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getBody().getUserData())){
        //if(contact.getFixtureA().getBody().getUserData() == karts.get(0)||contact.getFixtureB().getBody().getUserData() == karts.get(0)) {
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        if(compareAB(cameraFollowKart, contact.getFixtureA().getBody().getUserData(), contact.getFixtureB().getBody().getUserData())) {
        //if(contact.getFixtureA().getBody().getUserData() == cameraFollowKart || contact.getFixtureB().getBody().getUserData() == cameraFollowKart) {
            shake.shake(0.3f, Math.min(0.8f,impulse.getNormalImpulses()[0]/300));
        }
    }

    static boolean compareAB(Object compare, Object A, Object B) {
        return (compare == A || compare == B);
    }


    //UIController
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
        if (gameState == GameState.RUNNING) {
            gameState = GameState.PAUSING;
            Tween.to(this, UIScreenAccessor.BACKGROUND_DARKNESS, 0.5f).target(0.5f).ease(TweenEquations.easeInOutCubic).start(game.tweenManager).setCallback(new PausingTweenCallback()).setCallbackTriggers(TweenCallback.COMPLETE);
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.UNPAUSING;
            Tween.to(this, UIScreenAccessor.BACKGROUND_DARKNESS, 0.5f).target(1f).ease(TweenEquations.easeInOutCubic).start(game.tweenManager).setCallback(new UnpausingTweenCallback()).setCallbackTriggers(TweenCallback.COMPLETE);
        }

    }



}