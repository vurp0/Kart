package org.sandholm.max.kart;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

/**
 * Defines a level (track) with a spawn point, ground texture, etc.
 */
public class Level {
    static enum GroundType{SOLID, SLOW, WATER};

    Texture groundTexture;
    Texture backgroundTexture;
    float backgroundRepetition;
    Vector2 spawnPoint;
    float spawnRotation;
    float scale; //pixels per meter

    TiledMap map;

    public Level(String levelName) {
        try {
            JSONParser parser = new JSONParser();
            Map obj = (Map)parser.parse(Gdx.files.internal("levels/"+levelName+"/level.json").reader());
            spawnPoint = new Vector2(((Number)((Map)obj.get("spawn")).get("x")).floatValue(), ((Number)(((Map)obj.get("spawn")).get("y"))).floatValue());
            spawnRotation = ((Number)((Map)obj.get("spawn")).get("angle")).floatValue();
            scale = ((Number)obj.get("scale")).floatValue();
            groundTexture = new Texture("levels/"+levelName+"/"+obj.get("ground"));
            backgroundTexture = new Texture(Gdx.files.internal("levels/" + levelName + "/" + ((Map) obj.get("background")).get("file")));
            backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            backgroundRepetition = ((Number)((Map)obj.get("background")).get("repetition")).floatValue();
            map = new TmxMapLoader().load("levels/"+levelName+"/"+obj.get("map"));

            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Body createBody(World world) {
        MapObjects objs = map.getLayers().get("Solid layer").getObjects();
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bd);

        for (MapObject obj: objs) {
            if (obj instanceof TextureMapObject) {
                continue;
            }

            Shape shape;

            if (obj instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject)obj);
            }
            else if (obj instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)obj);
            }
            else if (obj instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)obj);
            }
            else if (obj instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)obj);
            }
            else {
                continue;
            }

            Fixture solidFixture = body.createFixture(shape, 1);
            solidFixture.setUserData(GroundType.SOLID);
            shape.dispose();
        }

        objs = map.getLayers().get("Slow layer").getObjects();

        for (MapObject obj: objs) {
            if (obj instanceof TextureMapObject) {
                continue;
            }

            Array<Shape> shapes = new Array<>();

            if (obj instanceof RectangleMapObject) {
                shapes.add(getRectangle((RectangleMapObject)obj));
            }
            else if (obj instanceof PolygonMapObject) {
                //shape = getPolygon((PolygonMapObject)obj);
                float[] vertices = ((PolygonMapObject) obj).getPolygon().getTransformedVertices();
                Array<Vector2> vectors = new Array<>();
                for (int i=0; i<vertices.length; i+=2) {
                    vectors.add(new Vector2(vertices[i]/scale,vertices[i+1]/scale));
                }
                Array<Array<Vector2>> partitionedPolygon = BayazitDecomposer.ConvexPartition(vectors);
                //Array<PolygonShape> polygons = new Array<>();
                for (Array<Vector2> poly : partitionedPolygon) {
                    PolygonShape tmpShape = new PolygonShape();
                    tmpShape.set(poly.toArray(Vector2.class));
                    shapes.add(tmpShape);
                }

            }
            else if (obj instanceof PolylineMapObject) {
                shapes.add(getPolyline((PolylineMapObject)obj));
            }
            else if (obj instanceof CircleMapObject) {
                shapes.add(getCircle((CircleMapObject)obj));
            }
            else {
                continue;
            }

            for (Shape shape: shapes) {
                Fixture solidFixture = body.createFixture(shape, 1);
                solidFixture.setSensor(true);
                solidFixture.setUserData(GroundType.SLOW);
                shape.dispose();
            }
            //Fixture solidFixture = body.createFixture(shape, 1);
            //solidFixture.setUserData(GroundType.SOLID);
            //shape.dispose();
        }
        return body;
    }

    public Vector2 getSpawnPoint() {return spawnPoint;}

    public float getSpawnRotation() {return spawnRotation;}

    public Texture getGroundTexture() {return groundTexture;}

    public float getScale() {
        return scale;
    }

    /*
     * Following functions adapted from http://stackoverflow.com/questions/18039781/collision-detection-tmx-maps-using-libgdx-java
     */

    private PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / scale,
                (rectangle.y + rectangle.height * 0.5f ) / scale);
        polygon.setAsBox(rectangle.width * 0.5f / scale,
                rectangle.height * 0.5f / scale,
                size,
                0.0f);
        return polygon;
    }

    private CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / scale);
        circleShape.setPosition(new Vector2(circle.x / scale, circle.y / scale));
        return circleShape;
    }

    private PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / scale;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / scale;
            worldVertices[i].y = vertices[i * 2 + 1] / scale;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }
}
