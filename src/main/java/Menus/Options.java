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
    final Options that;

    static class SliderStates {
        boolean vsync, fullscreen;
        int width, height;
        SliderStates() {
            Renderer.Options options = JSONReader.ReadOptions();
            this.fullscreen = options.fullscreen;
            this.height = options.height;
            this.width = options.width;
            this.vsync = options.vsync;
        }
        boolean equalsInitial() {
            return this.vsync == Main.INITIAL_RENDERING_OPTIONS.vsync &&
                    this.fullscreen == Main.INITIAL_RENDERING_OPTIONS.fullscreen &&
                    this.width == Main.INITIAL_RENDERING_OPTIONS.width &&
                    this.height == Main.INITIAL_RENDERING_OPTIONS.height;
        }
    }
    SliderStates sliderStates = new SliderStates();

    Options(Menu parent, Gameloop gameloop, View view) {
        that = this;
        this.parent = parent;
        this.view = view;
        this.gameloop = gameloop;
        this.textures = new HashMap<String, Async<Texture>>() {
            {
                this.put("vsync_on_active", view.loadTexture("resources/GUI/Sliders/vsync_on_active.png"));
                this.put("vsync_on_inactive", view.loadTexture("resources/GUI/Sliders/vsync_on_inactive.png"));
                this.put("vsync_off_active", view.loadTexture("resources/GUI/Sliders/vsync_off_active.png"));
                this.put("vsync_off_inactive", view.loadTexture("resources/GUI/Sliders/vsync_off_inactive.png"));
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
                    this.add(new GU_Menu.SliderInfo(new EmptyEnterHandler(), new VsyncStateChanger(sliderStates), new ArrayList<GU_Button.Textures>() {
                        {
                            this.add(new GU_Button.Textures(textures.get("vsync_on_inactive"), textures.get("vsync_on_active")));
                            this.add(new GU_Button.Textures(textures.get("vsync_off_inactive"), textures.get("vsync_off_active")));
                        }
                    }, (that.sliderStates.vsync) ? 0 : 1));
                    this.add(new GU_Menu.SimpleButtonInfo(new ExitHandler(), textures.get("discard_inactive"), textures.get("discard_active")));
                    this.add(new GU_Menu.SimpleButtonInfo(new ApplyHandler(), textures.get("apply_inactive"), textures.get("apply_active")));
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
            parent.unPause();
            pause();
        }
    }
    private class ApplyHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            gameloop.setCurrentMenu(parent);
            parent.unPause();
            pause();
            if (!sliderStates.equalsInitial()) {
                JSONReader.WriteOptions(new Renderer.Options(sliderStates.width, sliderStates.height, sliderStates.fullscreen, sliderStates.vsync));
                System.out.println("Changes to config writte, please restart the game");
            }
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
    private static class VsyncStateChanger extends GU_Slider.SlideEventHandler {
        private final SliderStates states;
        VsyncStateChanger(SliderStates states) {
            this.states = states;
        }
        public void state(int state) {
            states.vsync = (state == 0);
        }
    }
}
