package Menus;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Menu;
import Game.*;

import java.util.ArrayList;
import java.util.HashMap;

public class GameOver extends Menu {
    private HashMap<String, Async<Texture>> textures;
    private final Gameloop gameloop;

    public GameOver(View view, Gameloop gameloop) {
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
    }

    public void deleteMenu() {
        super.menu.pause();
        super.view.deleteDrawable(this.backgroundSpriteID);
        this.textures.values().forEach(super.view::unloadTexture);
    }

    public void tick(Gameloop gameloop) {
        super.menu.tick(gameloop);
    }

    private void selectLevel() {
        Menu levelSelector = new LevelSelector(super.view, this.gameloop, true);
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
