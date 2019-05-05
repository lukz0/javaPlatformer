package Menus;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Menu;
import Game.*;

import java.util.ArrayList;
import java.util.HashMap;

public class MainMenu extends Menu {
    private final MainMenu that;
    private HashMap<String, Async<Texture>> textures;
    private Gameloop gameloop;

    public MainMenu(Gameloop gameloop, View view) {
        this.that = this;
        super.view = view;
        this.gameloop = gameloop;
        this.textures = new HashMap<String, Async<Texture>>() {
            {
                this.put("select_level_active", view.loadTexture("resources/GUI/Buttons/select_level_active.png"));
                this.put("select_level_inactive", view.loadTexture("resources/GUI/Buttons/select_level_inactive.png"));

                this.put("options_active", view.loadTexture("resources/GUI/Buttons/options_active.png"));
                this.put("options_inactive", view.loadTexture("resources/GUI/Buttons/options_inactive.png"));

                this.put("exit_active", view.loadTexture("resources/GUI/Buttons/exit_active.png"));
                this.put("exit_inactive", view.loadTexture("resources/GUI/Buttons/exit_inactive.png"));

                this.put("background", view.loadTexture("resources/GUI/Backgrounds/1.png"));
            }
        };

        super.backgroundSpriteID = super.view.createBackground(0, this.textures.get("background"), Vector3f.EMPTY, new Vector3f(0.01f, 0.02f, 0), System.nanoTime(), 1);

        try {
            this.menu = new GU_Menu(this.view, new ArrayList<GU_Menu.MenuItemInfo>() {
                {
                    this.add(new GU_Menu.SimpleButtonInfo(new LevelSelectorHandler(), textures.get("select_level_inactive"), textures.get("select_level_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new OptionsHandler(), textures.get("options_inactive"), textures.get("options_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new ExitHandler(), textures.get("exit_inactive"), textures.get("exit_active")));
                }
            }, -0.9f);
        } catch (Exception e) {
            e.printStackTrace();
            this.menu = null;
        }
    }

    public void deleteMenu() {
        super.pause();
        this.textures.values().forEach(this.view::unloadTexture);
    }

    public void tick(Gameloop gameloop) {
        super.menu.tick(gameloop);
    }

    private class LevelSelectorHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            gameloop.setCurrentMenu(new LevelSelector(view, gameloop));
            deleteMenu();
        }
    }

    private class OptionsHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            gameloop.setCurrentMenu(new Options(that, gameloop, view));
            pause();
        }
    }

    private class ExitHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            gameloop.stopGame(0);
        }
    }
}
