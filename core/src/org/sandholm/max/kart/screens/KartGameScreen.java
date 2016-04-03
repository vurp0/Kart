package org.sandholm.max.kart.screens;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import org.sandholm.max.kart.*;
import org.sandholm.max.kart.gamecontroller.DumbAIGameController;
import org.sandholm.max.kart.tweenaccessors.MenuScreenAccessor;
import org.sandholm.max.kart.tweenaccessors.UIScreenAccessor;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class KartGameScreen extends UIScreen implements Screen, ContactListener, UIController {

    private enum GameState{RUNNING,PAUSED,STARTING}

    private World gameWorld;

    private String playerKartFileName;

    private float runningStateTime = 0f;
    private GameState gameState = GameState.STARTING;

    private static float CAMERA_HEIGHT = 2.17f;

    private ModelBatch quadBatch;

    private Quad groundQuad;
    private ArrayList<Quad> kartQuads;

    private SpriteBatch skyBatch;
    private PerspectiveCamera camera;

    private ShaderProgram postProcShaderProgram;
    private FrameBuffer frameBuffer1;
    private Mesh fullScreenMesh = createFullScreenQuad();

    private float cameraFOV = 45f;

    private Shake shake;

    private GameMap gameMap;

    private float cameraAngle;
    private float cameraVerticalAngle;


    private Kart cameraFollowKart;
    private ArrayList<Kart> karts;

    public KartGameScreen(KartGame game, String playerKartFileName) {
        super(game);
        this.playerKartFileName = playerKartFileName;
    }

    @Override
    public void show() {

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new PerspectiveCamera(cameraFOV, 1, h / w);
        camera.position.set(0f, 0f, 1f);
        camera.lookAt(0f, 1f, 1f);
        camera.near = 1f;
        camera.far = 1000f;
        camera.update();

        ShaderProgram.pedantic = false;

        String postProcVertexShader = Gdx.files.internal("shaders/kartgame.postproc.vert").readString();
        String postProcFragmentShader = Gdx.files.internal("shaders/kartgame.postproc.frag").readString();
        postProcShaderProgram = new ShaderProgram(postProcVertexShader, postProcFragmentShader);
        if (!postProcShaderProgram.isCompiled()) throw new IllegalArgumentException("couldn't compile postproc shader: " + postProcShaderProgram.getLog());

        String renderableVertexShader = Gdx.files.internal("shaders/renderablekartgame.vert").readString();
        String renderableFragmentShader = Gdx.files.internal("shaders/renderablekartgame.frag").readString();

        gameMap = new GameMap("lowrestest");
        gameWorld = new World(Vector2.Zero, true);

        Body worldBody = gameMap.createBody(gameWorld);
        worldBody.setTransform(0,0,0);
        gameWorld.setContactListener(this);

        quadBatch = new ModelBatch(new KartGameShaderProvider(renderableVertexShader, renderableFragmentShader));
        frameBuffer1 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        groundQuad = new Quad(new TextureRegion(gameMap.getGroundTexture()), gameMap.getGroundTexture().getWidth() / gameMap.getScale(), gameMap.getGroundTexture().getHeight() / gameMap.getScale());
        groundQuad.renderable.worldTransform.setToTranslation(groundQuad.getWidth()/2,groundQuad.getHeight()/2,0);

        skyBatch = new SpriteBatch();

        karts = new ArrayList<>();
        kartQuads = new ArrayList<>();

        DumbAIGameController otherKartController = new DumbAIGameController();

        Kart playerKart = new Kart(playerKartFileName, gameMap.getSpawnPoint().cpy(), gameMap.getSpawnRotation(), gameWorld);
        playerKart.setController(game.kartGameController);
        cameraFollowKart = playerKart;

        cameraAngle = cameraFollowKart.getRotation();

        karts.add(playerKart);
        for (int i = 0; i < 20; i++) {
            Kart tempKart = new Kart("mario-gameboy", new Vector2((float) Math.random() * 100, (float) Math.random() * 100), (float) Math.random() * 360, gameWorld);
            tempKart.setController(otherKartController);
            karts.add(tempKart);
        }
        for (int i = 0; i < 20; i++) {
            Kart tempKart = new Kart("tux-gameboy", new Vector2((float) Math.random() * 100, (float) Math.random() * 100), (float) Math.random() * 360, gameWorld);
            tempKart.setController(otherKartController);
            karts.add(tempKart);
        }

        kartQuads.addAll(karts.stream().map(k -> new Quad(k.getTextureRegionFromAngle(k.getRotation()), 1.28f, 1.28f)).collect(Collectors.toList()));

        shake = new Shake();

        frameBuffer1 = new FrameBuffer(Pixmap.Format.RGBA8888, game.LOWRES_WIDTH, game.LOWRES_HEIGHT, true);

        updateCamera(0);

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        skyBatch.setProjectionMatrix(UIProjection);

        camera.viewportWidth = width/(float)height;
        camera.viewportHeight = 1;
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
            case PAUSED:
                pausedRender();
                break;
            case STARTING:
                startingRender();
                break;
        }
    }

    private void gameRender(float deltaTime) {

        runningStateTime += deltaTime;

        for (Kart k : karts) {
            k.update(deltaTime);
        }

        gameWorld.step(deltaTime, 6, 2);

        updateCamera(deltaTime);

        drawKartSceneWithPostProc();
    }

    private void pausedRender() {
        drawKartSceneWithPostProc();
    }

    private void startingRender() {
        drawKartSceneWithPostProc();
    }

    @Override
    public void fadeInEnded() {
        gameState = GameState.RUNNING;
    }

    private void updateCamera(float deltaTime) { //TODO: this method is absolutely disgusting
        float FOVIntensifier = 0;//cameraFollowKart.getBody().getLinearVelocity().len() * 0.9f;
        cameraAngle = MathUtils.radiansToDegrees * MathUtils.lerpAngle(MathUtils.degreesToRadians * cameraAngle, MathUtils.degreesToRadians * cameraFollowKart.getRotation(), 0.06f);
        camera.fieldOfView = MathUtils.lerp(camera.fieldOfView, cameraFOV + FOVIntensifier, 0.1f);
        cameraVerticalAngle = 10;
        camera.position.set(new Vector3(cameraFollowKart.getPosition(), 0).add(-5.97f, 0f, CAMERA_HEIGHT));
        camera.up.set(0f, 0f, 1f);
        camera.rotateAround(new Vector3(cameraFollowKart.getPosition(), 0), new Vector3(0f, 0f, 1f), cameraAngle);
        camera.lookAt(new Vector3(cameraFollowKart.getPosition(), CAMERA_HEIGHT));
        camera.rotate(camera.direction.cpy().rotate(camera.up, 90), cameraVerticalAngle);
        shake.update(deltaTime, camera, camera.position);
        camera.update();

    }

    private void drawKartSceneWithPostProc() {
        frameBuffer1.begin();
            Gdx.gl.glCullFace(GL20.GL_BACK);
            Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
            Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
            Gdx.gl.glDepthMask(true);

            drawKartScene();
        frameBuffer1.end();

        postProcShaderProgram.begin();
        postProcShaderProgram.setUniformf("darkness", getFullScreenDarkness());
        postProcShaderProgram.end();

        frameBuffer1.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        frameBuffer1.getColorBufferTexture().bind();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            postProcShaderProgram.begin();
                fullScreenMesh.render(postProcShaderProgram, GL20.GL_TRIANGLES);

            postProcShaderProgram.end();

        drawUI();
    }

    private void drawKartScene() {

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
        quadBatch.render(groundQuad);
        for (int i=0; i<karts.size(); i++) {
            kartQuads.get(i).setTextureRegion(karts.get(i).getTextureRegionFromAngle(cameraAngle - karts.get(i).getRotation()));
            kartQuads.get(i).renderable.worldTransform.setToTranslation(karts.get(i).getPosition().x, karts.get(i).getPosition().y, kartQuads.get(i).getHeight() / 2);
            kartQuads.get(i).setRotation(camera.direction.cpy().scl(-1), Vector3.Z);
            quadBatch.render(kartQuads.get(i));
            karts.get(i).resetFrame();
        }
        quadBatch.end();

    }

    private void drawUI() {
        /*UIBatch.begin();
        drawText(UIFont, UIBatch, String.valueOf(Gdx.graphics.getFramesPerSecond())+" fps", screenWidth, 0, Anchor.SE);
        UIBatch.end();*/

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
            if (contact.getFixtureB().getUserData() == GameMap.GroundType.ROAD) {
                ((Kart)contact.getFixtureA().getBody().getUserData()).currentContacts += 1;
            } else if (contact.getFixtureB().getUserData() == GameMap.GroundType.HOLLOW) {
                ((Kart)contact.getFixtureA().getBody().getUserData()).resetPosition((float)Math.random()*64f, (float)Math.random()*64f, (float)Math.random()*360f);
            }
        } else if (contact.getFixtureB().getBody().getUserData() instanceof Kart) {
            if (contact.getFixtureA().getUserData() == GameMap.GroundType.ROAD) {
                ((Kart)contact.getFixtureB().getBody().getUserData()).currentContacts += 1;
            } else if (contact.getFixtureA().getUserData() == GameMap.GroundType.HOLLOW) {
                ((Kart)contact.getFixtureB().getBody().getUserData()).resetPosition((float)Math.random()*64f, (float)Math.random()*64f, (float)Math.random()*360f);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureA().getBody().getUserData() instanceof Kart) {
            if (contact.getFixtureB().getUserData() == GameMap.GroundType.ROAD) {
                ((Kart) contact.getFixtureA().getBody().getUserData()).currentContacts -= 1;
            }
        } else if (contact.getFixtureB().getBody().getUserData() instanceof Kart) {
            if (contact.getFixtureA().getUserData() == GameMap.GroundType.ROAD) {
                ((Kart) contact.getFixtureB().getBody().getUserData()).currentContacts -= 1;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        if(karts.get(0) == contact.getFixtureA().getBody().getUserData() || karts.get(0) == contact.getFixtureB().getBody().getUserData()){
            if (Gdx.input.isKeyPressed(Input.Keys.R)) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        if(cameraFollowKart == contact.getFixtureA().getBody().getUserData() || cameraFollowKart == contact.getFixtureB().getBody().getUserData()) {
            shake.shake(0.3f, Math.min(0.8f,impulse.getNormalImpulses()[0]/300));
        }
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
            gameState = GameState.PAUSED;
            setFullScreenDarkness(0.5f);
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.RUNNING;
            setFullScreenDarkness(1f);
        }

    }



}
