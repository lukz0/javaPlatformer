package Menus;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Menu;
import Game.*;

import java.util.ArrayList;
import java.util.HashMap;


public class PauseMenu extends Menu {
    private final Gameloop gameloop;
    private final HashMap<String, Async<Texture>> textures;
    private final PauseMenu that;

    public PauseMenu(View view, Gameloop gameloop) {
        this.that = this;
        this.view = view;
        this.gameloop = gameloop;
        this.textures = new HashMap<String, Async<Texture>>() {
            {
                this.put("exit_active", view.loadTexture("resources/GUI/Buttons/exit_active.png"));
                this.put("exit_inactive", view.loadTexture("resources/GUI/Buttons/exit_inactive.png"));

                this.put("options_active", view.loadTexture("resources/GUI/Buttons/options_active.png"));
                this.put("options_inactive", view.loadTexture("resources/GUI/Buttons/options_inactive.png"));

                this.put("resume_active", view.loadTexture("resources/GUI/Buttons/resume_active.png"));
                this.put("resume_inactive", view.loadTexture("resources/GUI/Buttons/resume_inactive.png"));

                this.put("select_level_active", view.loadTexture("resources/GUI/Buttons/select_level_active.png"));
                this.put("select_level_inactive", view.loadTexture("resources/GUI/Buttons/select_level_inactive.png"));

                this.put("background", view.loadTexture("resources/GUI/Backgrounds/1.png"));
            }
        };

        try {
            this.menu = new GU_Menu(this.view, new ArrayList<GU_Menu.MenuItemInfo>() {
                {
                    this.add(new GU_Menu.SimpleButtonInfo(new ResumeHandler(), textures.get("resume_inactive"), textures.get("resume_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new LevelSelectorHandler(), textures.get("select_level_inactive"), textures.get("select_level_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new OptionsHandler(), textures.get("options_inactive"), textures.get("options_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new ExitHandler(), textures.get("exit_inactive"), textures.get("exit_active")));
                }
            }, -0.9f);
        } catch (Exception e) {
            e.printStackTrace();
            this.menu = null;
        }
        this.backgroundSpriteID = view.createBackground(0, this.textures.get("background"), Vector3f.EMPTY, new Vector3f(0.01f, 0.02f, 0), System.nanoTime(), 1);
    }

    public void tick(Gameloop gameloop) {
        if (this.isPaused) {
            System.err.println("Paused menu recieved tick!");
        }
        this.menu.tick(gameloop);
    }

    public void deleteMenu() {
        this.view.deleteDrawable(this.backgroundSpriteID);
        this.menu.pause();
        this.textures.values().forEach(view::unloadTexture);
    }

    private class ResumeHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            gameloop.exitPause();
        }
    }

    private class ExitHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            gameloop.stopGame(0);
        }
    }

    private class OptionsHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            Options optionsMenu = new Options(that, that.gameloop, that.view);
            that.gameloop.setCurrentMenu(optionsMenu);
            that.pause();
        }
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
}
