package org.sandholm.max.kart.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.sandholm.max.kart.BaseMenuItem;
import org.sandholm.max.kart.Kart;
import org.sandholm.max.kart.KartGame;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by max on 2016-01-30.
 */
public class KartSelectScreen extends MenuScreen {

    float stateTime;

    Texture backgroundTexture;

    public KartSelectScreen(KartGame game) {
        super(game);
    }

    //Screen:
    @Override
    public void show() {
        super.show();


        try {
            JSONParser parser = new JSONParser();
            JSONArray obj = (JSONArray)parser.parse(Gdx.files.internal("karts/karts.json").reader());
            for (Object name : obj) {
                System.out.println((String)name);
                Kart tmpKart = new Kart((String)name, Vector2.Zero, 0, null);
                menu.menuItems.add(new KartMenuItem(tmpKart));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        backgroundTexture = new Texture(Gdx.files.internal("background.png")); //TODO: hardcoded placeholder background
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        updateMenuLabels();

    }

    @Override
    public void render(float deltaTime) {
        stateTime += deltaTime;

        super.render(deltaTime);

        menuDrawingOffset = MathUtils.lerp(menuDrawingOffset, screenWidth / 2 - menu.getMenuIndex() * screenWidth / 3, 0.1f);

        UIBatch.begin();
        UIBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight,
                0+(stateTime%1), 0+(MathUtils.sin(stateTime*4)/7),
                screenWidth/64+(stateTime%1), screenHeight/64+(MathUtils.sin(stateTime*4)/7));

        for (int i=0; i<menu.menuItems.size(); i++) {
            if (menu.menuItems.get(i) instanceof KartMenuItem) {
                float kartAngle;
                if (i == menu.getMenuIndex()) {
                    kartAngle = stateTime*90;
                    UIBatch.setColor(1,1,1,1);
                } else {
                    kartAngle = (menuDrawingOffset+i*screenWidth/3-screenWidth/2)/-10+180;
                    UIBatch.setColor(1,1,1,0.75f);
                }

                UIBatch.draw(((KartMenuItem)menu.menuItems.get(i)).kart.getTextureRegionFromAngle(kartAngle), menuDrawingOffset+i*screenWidth/3-(screenHeight/14), (screenHeight/2), screenHeight/7, screenHeight/7);
            }
            UIBatch.setColor(1,1,1,1f);
        }

        drawMenuLabels();

        UIBatch.end();
    }

    public void drawMenuLabels() {
        for (int i=0; i< menu.menuItems.size(); i++) {
            menuLabels.get(i).setPosition((menuDrawingOffset+i*screenWidth/3), screenHeight/3);
            if (i== menu.getMenuIndex()){
                menuLabels.get(i).draw(UIBatch, 1);
            } else {
                menuLabels.get(i).draw(UIBatch, 0.75f);
            }
        }
    }

    class KartMenuItem extends BaseMenuItem {
        public Kart kart;

        public KartMenuItem(Kart kart) {
            super(kart.getName());
            this.kart = kart;

        }

        @Override
        public void select() {
            return;
        }
    }

    //UIController:
    @Override
    public void upPressed() {
    }

    @Override
    public void downPressed() {
    }

    @Override
    public void leftPressed() {
        menu.previousItem();

    }

    @Override
    public void rightPressed() {
        menu.nextItem();

    }

    @Override
    public void OKPressed() {
        menu.selectItem();
    }

    @Override
    public void backPressed() {
        game.transitionTo(KartGame.Flow.MAIN_MENU_SCREEN);
    }

    @Override
    public void pausePressed() {

    }
}
