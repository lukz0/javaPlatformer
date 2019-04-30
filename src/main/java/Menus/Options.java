package Menus;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Menu;
import GUI_Utils.GU_Slider;
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
                this.put("discard_active", view.loadTexture("resources/GUI/Buttons/discard_active.png"));
                this.put("discard_inactive", view.loadTexture("resources/GUI/Buttons/discard_inactive.png"));
                this.put("apply_active", view.loadTexture("resources/GUI/Buttons/apply_active.png"));
                this.put("apply_inactive", view.loadTexture("resources/GUI/Buttons/apply_inactive.png"));
                this.put("background", view.loadTexture("resources/GUI/Backgrounds/1.png"));
            }
        };
        try {
            this.menu = new GU_Menu(this.view, new ArrayList<GU_Menu.MenuItemInfo>() {
                {
                    this.add(new GU_Menu.SimpleButtonInfo(new ExitHandler(), textures.get("discard_inactive"), textures.get("discard_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new ExitHandler(), textures.get("apply_inactive"), textures.get("apply_active")));
                    this.add(new GU_Menu.SliderInfo(new EmptyEnterHandler(), new StateChangePrinter(), new ArrayList<GU_Button.Textures>() {
                        {
                            this.add(new GU_Button.Textures(textures.get("discard_inactive"), textures.get("discard_active")));
                            this.add(new GU_Button.Textures(textures.get("apply_inactive"), textures.get("apply_active")));
                        }
                    }));
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
    private static class EmptyEnterHandler extends GU_Button.EnterEventHandler {
        public void enter() {}
    }
    private static class StateChangePrinter extends GU_Slider.SlideEventHandler {
        public void state(int state) {
            System.out.println("[OPTIONS] Changed state to ".concat(Integer.toString(state)));
        }
    }
}
