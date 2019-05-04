package Menus;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Digit;
import GUI_Utils.GU_Menu;
import GUI_Utils.GU_Number;
import Game.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GameOver extends Menu {
    private HashMap<String, Async<Texture>> textures;
    private final Gameloop gameloop;
    private GU_Number score, highScore;
    private Async<Integer> scoreDrawableID, highScoreDrawableID;

    public GameOver(View view, Gameloop gameloop) {
        int highScore = JSONReader.ReadHighscore(gameloop.currentLevel);

        super.view = view;
        this.gameloop = gameloop;
        this.textures = new HashMap<String, Async<Texture>>() {
            {
                this.put("background", view.loadTexture("resources/GUI/Backgrounds/gameover.png"));

                this.put("select_level_active", view.loadTexture("resources/GUI/Buttons/select_level_active.png"));
                this.put("select_level_inactive", view.loadTexture("resources/GUI/Buttons/select_level_inactive.png"));

                this.put("exit_active", view.loadTexture("resources/GUI/Buttons/exit_active.png"));
                this.put("exit_inactive", view.loadTexture("resources/GUI/Buttons/exit_inactive.png"));
            }
        };
        this.backgroundSpriteID = super.view.createBackground(0, this.textures.get("background"), Vector3f.EMPTY, Vector3f.EMPTY, 0, 16f/9f);

        try {
            this.menu = new GU_Menu(super.view, new ArrayList<GU_Menu.MenuItemInfo>() {
                {
                    this.add(new GU_Menu.SimpleButtonInfo(new LevelSelectorHandler(), textures.get("select_level_inactive"), textures.get("select_level_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new ExitHandler(), textures.get("exit_inactive"), textures.get("exit_active")));
                }
            }, -0.9f);
        } catch (Exception e) {
            e.printStackTrace();
            this.menu = null;
        }
        //GU_Number gun = new GU_Number(this.view, new TextCreator((int)(200* GU_Digit.KERNING), 200, Color.BLACK), 5, 1, -0.9f,
        //        new Vector3f(13.5f, 8, 0));
        TextCreator tcWhite = new TextCreator((int)(200* GU_Digit.KERNING), 200, Color.WHITE);
        TextCreator tcColor = new TextCreator((int)(4*200), 200, new Color(255, 0, 68));

        this.highScore = new GU_Number(this.view, tcWhite, 5, 1, -0.9f,
                new Vector3f(13.5f, 1, 0));
        this.highScore.setNumber(this.view, highScore);
        textures.put("highScore", tcColor.renderString(this.view, "high-score:"));
        this.highScoreDrawableID = view.createStaticTexturedRectangle(9.5f, 13.5f, 2, 1, -0.9f, textures.get("highScore"));
    }

    public void deleteMenu() {
        super.menu.pause();
        super.view.deleteDrawable(this.backgroundSpriteID);
        super.view.deleteDrawable(this.highScoreDrawableID);
        this.highScore.pause(super.view);
        this.textures.values().forEach(super.view::unloadTexture);
    }

    public void tick(Gameloop gameloop) {
        super.menu.tick(gameloop);
    }

    private void selectLevel() {
        this.gameloop.holdingEnter = false;
        Menu levelSelector = new LevelSelector(super.view, this.gameloop);
        gameloop.setCurrentMenu(levelSelector);
        this.deleteMenu();
    }
    private class LevelSelectorHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            selectLevel();
        }
    }

    private void exitGame() {
        gameloop.stopGame(0);
    }
    private class ExitHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            exitGame();
        }
    }
}
