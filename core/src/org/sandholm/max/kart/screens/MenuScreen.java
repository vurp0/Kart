package org.sandholm.max.kart.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import org.sandholm.max.kart.BaseMenuItem;
import org.sandholm.max.kart.KartGame;
import org.sandholm.max.kart.Menu;
import org.sandholm.max.kart.UIController;

import java.util.ArrayList;

/**
 * Created by max on 1/17/16.
 */
abstract public class MenuScreen extends UIScreen implements Screen, UIController {

    protected Menu menu;

    protected ArrayList<Group> menuLabels;

    private BitmapFont menuFont;

    public float getMenuDrawingOffset() {
        return menuDrawingOffset;
    }

    public void setMenuDrawingOffset(float menuDrawingOffset) {
        this.menuDrawingOffset = menuDrawingOffset;
    }

    protected float menuDrawingOffset;

    public MenuScreen(KartGame game) {
        super(game);

        menu = new Menu();
    }

    //Screen:
    @Override
    public void show() {
        menu = new Menu();

        menuFont = generateFont(0.07f);

        menuLabels = new ArrayList<>();
    }

    @Override
    public void render(float deltaTime) {

        super.render(deltaTime);

        //menuDrawingOffset = MathUtils.lerp(menuDrawingOffset, screenHeight / 2 - menu.getMenuIndex() * screenHeight / 7, 0.1f);
    }

    public void drawMenuLabels() {
        for (int i=0; i< menu.menuItems.size(); i++) {
            menuLabels.get(i).setPosition(screenWidth/2, screenHeight-(menuDrawingOffset+i*screenHeight/7));
            if (i== menu.getMenuIndex()){
                menuLabels.get(i).draw(UIBatch, 1);
            } else {
                menuLabels.get(i).draw(UIBatch, 0.75f);
            }
        }
    }

    public void updateMenuLabels() {
        menuFont = generateFont(0.07f);

        for (int i=0; i<menuLabels.size(); i++){
            ((Label)menuLabels.get(i).getChildren().get(0)).setText(menu.menuItems.get(i).getTitle());
            ((Label)menuLabels.get(i).getChildren().get(0)).setStyle(new Label.LabelStyle(menuFont, Color.WHITE));
            ((Label)menuLabels.get(i).getChildren().get(0)).setAlignment(Align.center);
        }
        for (int i=menuLabels.size(); i<menu.menuItems.size(); i++) {
            Label tmpTitleLabel = new Label(menu.menuItems.get(i).getTitle(), new Label.LabelStyle(menuFont, Color.WHITE));
            tmpTitleLabel.setPosition(0,0, Align.center);
            Group menuLabel = new Group();
            menuLabel.addActor(tmpTitleLabel);
            menuLabel.setPosition(0, 0, Align.center);
            menuLabel.setOrigin(Align.center);
            menuLabels.add(menuLabel);
        }
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
        menu.previousItem();
    }

    @Override
    public void downPressed() {
        menu.nextItem();
    }

    @Override
    public void leftPressed() {

    }

    @Override
    public void rightPressed() {

    }

    @Override
    public void OKPressed() {
        menu.selectItem();
    }

    @Override
    public void backPressed() {

    }

    @Override
    public void pausePressed() {

    }
}
