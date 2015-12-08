package org.sandholm.max.kart;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;

import java.util.ArrayList;

public class KartGameScreen extends UIScreen implements Screen, ContactListener, UIController {
    //KartGame game;
    enum GameState{RUNNING,PAUSING,PAUSED,UNPAUSING,STARTING}

    World gameWorld;

    float runningStateTime = 0f;
    GameState gameState = GameState.STARTING;

    static float CAMERA_HEIGHT = 4f;

    CameraGroupStrategy groupStrategy;
    DecalBatch decalBatch;
    Decal groundDecal;
    ArrayList<Decal> kartDecal;

    ShaderProgram kartGameShader;

    Texture groundTexture;
    PerspectiveCamera camera;
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

        gameMap = new GameMap("testlevel");

        gameWorld = new World(Vector2.Zero, true);

        Body worldBody = gameMap.createBody(gameWorld);
        worldBody.setTransform(0,0,0);
        gameWorld.setContactListener(this);


        groupStrategy = new KartGameCameraGroupStrategy(camera);
        decalBatch = new DecalBatch(groupStrategy);
        groundTexture = gameMap.getGroundTexture();
        groundDecal = Decal.newDecal(gameMap.getGroundTexture().getWidth() / gameMap.getScale(), gameMap.getGroundTexture().getHeight() / gameMap.getScale(), new TextureRegion(groundTexture));
        groundDecal.setPosition(groundDecal.getWidth()/2,groundDecal.getHeight()/2,0);
        karts = new ArrayList<Kart>();

        kartDecal = new ArrayList<Decal>();

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
            kartDecal.add(Decal.newDecal(1.28f, 1.28f, k.getTextureRegionFromAngle(k.getRotation()), true));
        }

        UIFont = generateFont(0.08f);
        shake = new Shake();


    }

    static Mesh newQuad(float width, float height) {
        Mesh quad = new Mesh(true, 4, 6,
                        new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));

        quad.setVertices(new float[] {
                        -width/2, -height/2, 0,        //define counter clock wise vertices
                        width/2, -height/2, 0,
                        width/2, height/2, 0,
                        -width/2, height/2, 0 });
        return quad;
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.viewportWidth = width/(float)height;
        camera.viewportHeight = 1;
    }

    //TODO: this method could still be useful some day, and I should probably put it somewhere as a generic utility method
    /*static int degreesToFrame(float angle, int range) {
        return (((int)Math.floor((angle+360/(range*2))/(360/range))) % range + range ) % range;
    }*/

    @Override
    public void render(float deltaTime) {
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

        drawKartScene();
    }



    public void pausingRender(float deltaTime) {
        gameState = GameState.PAUSED; //TODO: transition from running into paused mode
        drawKartScene();
    }

    public void pausedRender(float deltaTime) {
        drawKartScene();

    }

    public void unpausingRender(float deltaTime) {
        gameState = GameState.RUNNING; //TODO: transition from paused into running mode
        drawKartScene();
    }

    public void startingRender(float deltaTime) {
        gameState = GameState.RUNNING; //TODO: beginning snimation, then start game
        drawKartScene();

    }
    public void drawKartScene() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        UIBatch.begin();
        float rotatedOffset = (cameraAngle * gameMap.backgroundRepetition) / (360);
        float screenRepetitions = gameMap.backgroundRepetition*(camera.fieldOfView/360);
        float unitsPerDegree = screenRepetitions/camera.fieldOfView;
        UIBatch.draw(gameMap.backgroundTexture, 0, 0, screenWidth, screenHeight,
                -0.5f*screenRepetitions- rotatedOffset,
                0.5f+cameraVerticalAngle*unitsPerDegree*0.5f+((float)screenHeight/(float)screenWidth)*screenRepetitions*0.5f,
                0.5f*screenRepetitions - rotatedOffset,
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
        drawText(UIFont, UIBatch, "some text", screenWidth, 0, Anchor.SE);
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
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.UNPAUSING;
        }

    }
}