package Menus;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Menu;
import Game.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Options extends Menu {
    private final Menu parent;
    private final Gameloop gameloop;
    private final HashMap<String, Async<Texture>> textures;

    Options(Menu parent, Gameloop gameloop, View view) {
        this.parent = parent;
        this.view = view;
        this.gameloop = gameloop;
        this.textures = new HashMap<String, Async<Texture>>() {
            {
                this.put("discard_exit_active", view.loadTexture("resources/GUI/Buttons/discard_exit_active.png"));
                this.put("discard_exit_inactive", view.loadTexture("resources/GUI/Buttons/discard_exit_inactive.png"));
                this.put("apply_exit_active", view.loadTexture("resources/GUI/Buttons/apply_exit_active.png"));
                this.put("apply_exit_inactive", view.loadTexture("resources/GUI/Buttons/apply_exit_inactive.png"));
                this.put("background", view.loadTexture("resources/GUI/Backgrounds/1.png"));
            }
        };
        try {
            this.menu = new GU_Menu(this.view, new ArrayList<Map.Entry<GU_Button.Textures, GU_Button.EnterEventHandler>>() {
                {
                    this.add(new GU_Menu.SimpleButtonInfo(new ExitHandler(), textures.get("discard_exit_inactive"), textures.get("discard_exit_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new ExitHandler(), textures.get("apply_exit_inactive"), textures.get("apply_exit_active")));
                }
            }, -0.9f);
        } catch (Exception e) {
            e.printStackTrace();
            this.menu = null;
        }
        this.backgroundSpriteID = view.createBackground(0, this.textures.get("background"), Vector3f.EMPTY, new Vector3f(0.02f, -0.01f, 0), System.nanoTime(), 1);
    }

    public void deleteMenu() {
        this.view.deleteDrawable(this.backgroundSpriteID);
        this.menu.pause();
        this.textures.values().forEach(view::unloadTexture);
        this.parent.unPause();
        this.gameloop.setCurrentMenu(this.parent);
    }

    private long enterMenuDelay = Long.divideUnsigned(Long.divideUnsigned(1000, Gameloop.TICKDURATION), 3);
    public void tick(Gameloop gameloop) {
        if (enterMenuDelay == 0) {
            this.menu.tick(gameloop);
        } else {
            enterMenuDelay--;
        }
    }

    private class ExitHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            gameloop.setCurrentMenu(parent);
            pause();
            parent.unPause();
        }
    }
}
