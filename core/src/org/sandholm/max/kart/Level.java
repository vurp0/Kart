package org.sandholm.max.kart;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Map;

/**
 * Defines a level (track) with a spawn point, ground texture, etc.
 */
public class Level {
    Texture groundTexture;
    Vector2 spawnPoint;
    float spawnRotation;
    float scale; //pixels per meter

    public ArrayList<Vector2[]> getCollisions() {
        return collisions;
    }

    ArrayList<Vector2[]> collisions;

    public Level(String levelName) {
        try {
            JSONParser parser = new JSONParser();
            Map obj = (Map)parser.parse(new FileReader("levels/"+levelName+"/level.json"));
            spawnPoint = new Vector2(((Number)((Map)obj.get("spawn")).get("x")).floatValue(), ((Number)(((Map)obj.get("spawn")).get("y"))).floatValue());
            spawnRotation = ((Number)((Map)obj.get("spawn")).get("angle")).floatValue();
            scale = ((Number)obj.get("scale")).floatValue();
            groundTexture = new Texture("levels/"+levelName+"/"+obj.get("ground"));

            collisions = new ArrayList<Vector2[]>();
            for (Object j : (JSONArray)obj.get("collisions")) {
                collisions.add(new Vector2[]{new Vector2(((Number)(((JSONObject)((JSONArray)j).get(0))).get("x")).floatValue(), ((Number)(((JSONObject)((JSONArray)j).get(0))).get("y")).floatValue()),
                        new Vector2(((Number)(((JSONObject)((JSONArray)j).get(1))).get("x")).floatValue(), ((Number)(((JSONObject)((JSONArray)j).get(1))).get("y")).floatValue()),
                        new Vector2(((Number)(((JSONObject)((JSONArray)j).get(2))).get("x")).floatValue(), ((Number)(((JSONObject)((JSONArray)j).get(2))).get("y")).floatValue()),
                        new Vector2(((Number)(((JSONObject)((JSONArray)j).get(3))).get("x")).floatValue(), ((Number)(((JSONObject)((JSONArray)j).get(3))).get("y")).floatValue())});
            }
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Vector2 getSpawnPoint() {return spawnPoint;}

    public float getSpawnRotation() {return spawnRotation;}

    public Texture getGroundTexture() {return groundTexture;}

    public float getScale() {
        return scale;
    }
}
