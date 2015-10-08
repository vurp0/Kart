package org.sandholm.max.kart;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Created by max on 5.10.2015.
 */
public class Level {
    Texture groundTexture;
    Vector2 spawnPoint;
    float spawnRotation;

    public Level(String levelName) {
        try {
            JSONParser parser = new JSONParser();
            Map obj = (Map)parser.parse(new FileReader(levelName+"/level.json"));
            spawnPoint = new Vector2(((Number)((Map)obj.get("spawn")).get("x")).floatValue(), ((Number)(((Map)obj.get("spawn")).get("y"))).floatValue());
            spawnRotation = ((Number)((Map)obj.get("spawn")).get("angle")).floatValue();
            groundTexture = new Texture(levelName+obj.get("ground"));
            
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Vector2 getSpawnPoint() {return spawnPoint;}

    public float getSpawnRotation() {return spawnRotation;}

    public Texture getGroundTexture() {return groundTexture;}

}
