package org.sandholm.max.kart.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.sun.corba.se.spi.activation.BadServerDefinition;
import org.sandholm.max.kart.BaseMenuItem;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.Menu;
import org.sandholm.max.kart.UIController;

import java.util.ArrayList;

/**
 * Created by max on 12.12.2015.
 */
public class MainMenuScreen extends UIScreen implements Screen, UIController {

    Texture backgroundTexture;

    Menu mainMenu;

    private ArrayList<Group> menuLabels;

    private BitmapFont menuFont;

    private float menuDrawingOffset;

    float stateTime;

    public MainMenuScreen(KartGame game) {
        super(game);


        mainMenu = new Menu();
    }

    //Screen:
    @Override
    public void show() {

        backgroundTexture = new Texture(Gdx.files.internal("background.png")); //TODO: hardcoded placeholder background
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        mainMenu = new Menu();

        mainMenu.menuItems.add(new BaseMenuItem("Item 1"));
        mainMenu.menuItems.add(new BaseMenuItem("Item 2"));
        mainMenu.menuItems.add(new BaseMenuItem("Item 3"));
        mainMenu.menuItems.add(new StartGameMenuItem("Start game"));
        mainMenu.menuItems.add(new BaseMenuItem("Item 4"));
        mainMenu.menuItems.add(new BaseMenuItem("Item 5"));
        mainMenu.menuItems.add(new BaseMenuItem("Item 6"));

        menuFont = generateFont(0.07f);

        menuLabels = new ArrayList<>();

        for (BaseMenuItem item : mainMenu.menuItems) {
            Label tmpTitleLabel = new Label(item.getTitle(), new Label.LabelStyle(menuFont, Color.WHITE));
            tmpTitleLabel.setPosition(0,0, Align.center);
            Group menuLabel = new Group();
            menuLabel.addActor(tmpTitleLabel);
            menuLabel.setPosition(0, 0, Align.center);
            menuLabel.setOrigin(Align.center);
            menuLabels.add(menuLabel);
        }

    }

    class StartGameMenuItem extends BaseMenuItem {
        public StartGameMenuItem(String title) {
            super(title);
        }

        @Override
        public void select() {
            game.transitionTo(KartGame.Flow.GAME_SCREEN);
        }
    }

    @Override
    public void render(float deltaTime) {
        stateTime += deltaTime;

        super.render(deltaTime);

        menuDrawingOffset = MathUtils.lerp(menuDrawingOffset, screenHeight/2-mainMenu.getMenuIndex()*screenHeight/7, 0.1f);

        UIBatch.begin();
        UIBatch.draw(backgroundTexture, 0, 0, screenWidth, screenHeight,
                0+(stateTime%1), 0/*-(stateTime%1)*/+(MathUtils.sin(stateTime*4)/7),
                screenWidth/64+(stateTime%1), screenHeight/64/*-(stateTime%1)*/+(MathUtils.sin(stateTime*4)/7));

        for (int i=0; i<mainMenu.menuItems.size(); i++) {
            menuLabels.get(i).setPosition(screenWidth/2, screenHeight-(menuDrawingOffset+i*screenHeight/7));
            if (i==mainMenu.getMenuIndex()){
                menuLabels.get(i).draw(UIBatch, 1);
            } else {
                menuLabels.get(i).draw(UIBatch, 0.75f);
            }
        }
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

    //UIController:
    @Override
    public void upPressed() {
        mainMenu.previousItem();
    }

    @Override
    public void downPressed() {
        mainMenu.nextItem();
    }

    @Override
    public void leftPressed() {

    }

    @Override
    public void rightPressed() {

    }

    @Override
    public void OKPressed() {
        mainMenu.selectItem();
    }

    @Override
    public void backPressed() {

    }

    @Override
    public void pausePressed() {

    }
}
