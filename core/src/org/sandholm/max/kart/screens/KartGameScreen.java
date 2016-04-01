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

public class KartGameScreen extends MenuScreen implements Screen, ContactListener, UIController {

    private enum GameState{RUNNING,PAUSING,PAUSED,UNPAUSING,STARTING}

    private World gameWorld;

    private String playerKartFileName;

    private float runningStateTime = 0f;
    private GameState gameState = GameState.STARTING;

    private static float CAMERA_HEIGHT = 2.17f;

    private ModelBatch quadBatch;

    private Quad groundQuad;
    private ArrayList<Quad> kartQuads;

    private SpriteBatch skyBatch;

    //Texture groundTexture;
    private PerspectiveCamera camera;

    private ShaderProgram postProcShaderProgram;
    private FrameBuffer frameBuffer1;
    private Mesh fullScreenMesh = createFullScreenQuad();

    private float gameSceneDarkness = 0f;

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

    private float cameraFOV = 45f;

    private Shake shake;

    private GameMap gameMap;

    private float cameraAngle;
    private float cameraVerticalAngle;


    private Kart cameraFollowKart;
    private ArrayList<Kart> karts;

    private BitmapFont UIFont;

    public KartGameScreen(KartGame game, String playerKartFileName) {
        super(game);
        this.playerKartFileName = playerKartFileName;
    }

    @Override
    public void show() {

        super.show();

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new PerspectiveCamera(cameraFOV, 1, h / w);
        camera.position.set(0f, 0f, 1f);
        camera.lookAt(0f, 1f, 1f);
        camera.near = 1f;
        camera.far = 1000f;
        camera.update();

        ShaderProgram.pedantic = false;


        String skyBatchVertexShader = Gdx.files.internal("shaders/kartgame.vert").readString();
        String skyBatchFragmentShader = Gdx.files.internal("shaders/kartgame.frag").readString();
        ShaderProgram skyBatchShaderProgram = new ShaderProgram(skyBatchVertexShader, skyBatchFragmentShader);
        if (!skyBatchShaderProgram.isCompiled()) throw new IllegalArgumentException("couldn't compile shader: " + skyBatchShaderProgram.getLog());

        String postProcVertexShader = Gdx.files.internal("shaders/kartgame.postproc.vert").readString();
        String postProcFragmentShader = Gdx.files.internal("shaders/kartgame.postproc.frag").readString();
        postProcShaderProgram = new ShaderProgram(postProcVertexShader, postProcFragmentShader);
        if (!postProcShaderProgram.isCompiled()) throw new IllegalArgumentException("couldn't compile postproc shader: " + postProcShaderProgram.getLog());

        String renderableVertexShader = Gdx.files.internal("shaders/renderablekartgame.vert").readString();
        String renderableFragmentShader = Gdx.files.internal("shaders/renderablekartgame.frag").readString();

        UIDarkness = 0f;

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

        UIFont = generateFont(0.08f);
        shake = new Shake();
        skyBatch.setShader(skyBatchShaderProgram);

        menu.menuItems.add(new BaseMenuItem("Resume"));
        menu.menuItems.add(new BaseMenuItem("Quit"));
        updateMenuLabels();

        updateCamera(0);

/*        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
            }
        }, 2f);*/

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        skyBatch.setProjectionMatrix(UIProjection);

        camera.viewportWidth = width/(float)height;
        camera.viewportHeight = 1;

        //frameBuffer1 = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
        frameBuffer1 = new FrameBuffer(Pixmap.Format.RGBA8888, 64, 64, true);

        //postProcShaderProgram.begin();
        //postProcShaderProgram.setUniformf("u_resolution", screenWidth, screenHeight);
        //postProcShaderProgram.end();
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
                pausingRender();
                break;
            case PAUSED:
                pausedRender();
                break;
            case UNPAUSING:
                unpausingRender();
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



    private void pausingRender() {
        drawKartSceneWithPostProc();
        drawPauseMenu();

    }
    private class PausingTweenCallback implements TweenCallback {
        @Override
        public void onEvent(int eventType, BaseTween<?> source) {
            switch (eventType) {
                case TweenCallback.COMPLETE:
                    gameState = GameState.PAUSED;
            }
        }
    }

    private void pausedRender() {
        drawKartSceneWithPostProc();
        drawPauseMenu();

    }

    private void unpausingRender() {
        drawKartSceneWithPostProc();
        drawPauseMenu();
    }
    private class UnpausingTweenCallback implements TweenCallback {
        @Override
        public void onEvent(int eventType, BaseTween<?> source) {
            switch (eventType) {
                case TweenCallback.COMPLETE:
                    gameState = GameState.RUNNING;
            }
        }
    }

    private void startingRender() {
        drawKartSceneWithPostProc();
    }

    private void drawPauseMenu() {

        UIBatch.begin();

        drawMenuLabels();
        UIBatch.end();
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

        //postProcShaderProgram.begin();
        //postProcShaderProgram.setUniformf("fadeDark", getBackgroundDarkness());
        //postProcShaderProgram.end();

        frameBuffer1.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        frameBuffer1.getColorBufferTexture().bind();
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            postProcShaderProgram.begin();
                fullScreenMesh.render(postProcShaderProgram, GL20.GL_TRIANGLES);

            postProcShaderProgram.end();

        drawUI();
    }

    private static Mesh createFullScreenQuad(){
        float[] verts = new float[] {
            -1f,-1f,  0f, 0f,
             1f,-1f,  1f, 0f,
             1f, 1f,  1f, 1f,
            -1f, 1f,  0f, 1f
        };
        short[] indices = new short[] { 0, 1, 2, 2, 3, 0 };
        Mesh tmpMesh = new Mesh(true, 4, 6
                , new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position")
                , new VertexAttribute(VertexAttributes.Usage.TextureCoordinates
                , 2, "a_texCoord0"));
        tmpMesh.setVertices(verts);
        tmpMesh.setIndices(indices);
        return tmpMesh;
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
            gameState = GameState.PAUSING;
            Timeline.createSequence().beginParallel()
                        .push(Tween.to(this, UIScreenAccessor.BACKGROUND_DARKNESS, 0.5f).target(0.5f).ease(TweenEquations.easeInOutCubic))
                        .push(Tween.to(this, MenuScreenAccessor.MENU_DRAWING_OFFSET, 0.5f).target(screenHeight).ease(TweenEquations.easeInOutCubic))
                    .end()
                    .start(game.tweenManager)
                    .setCallback(new PausingTweenCallback()).setCallbackTriggers(TweenCallback.COMPLETE);
            //Tween.to(this, UIScreenAccessor.BACKGROUND_DARKNESS, 0.5f).target(0.5f).ease(TweenEquations.easeInOutCubic).start(game.tweenManager).setCallback(new PausingTweenCallback()).setCallbackTriggers(TweenCallback.COMPLETE);
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.UNPAUSING;
            Timeline.createSequence().beginParallel()
                        .push(Tween.to(this, UIScreenAccessor.BACKGROUND_DARKNESS, 0.5f).target(1f).ease(TweenEquations.easeInOutCubic))
                        .push(Tween.to(this, MenuScreenAccessor.MENU_DRAWING_OFFSET, 0.5f).target(0f).ease(TweenEquations.easeInOutCubic))
                    .end()
                    .start(game.tweenManager)
                    .setCallback(new UnpausingTweenCallback()).setCallbackTriggers(TweenCallback.COMPLETE);
            //Tween.to(this, UIScreenAccessor.BACKGROUND_DARKNESS, 0.5f).target(1f).ease(TweenEquations.easeInOutCubic).start(game.tweenManager).setCallback(new UnpausingTweenCallback()).setCallbackTriggers(TweenCallback.COMPLETE);
        }

    }



}
