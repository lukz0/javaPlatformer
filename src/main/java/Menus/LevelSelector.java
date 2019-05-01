package Menus;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Menu;
import GUI_Utils.GU_Slider;
import Game.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class LevelSelector extends Menu {
    private final HashMap<String, Async<Texture>> textures;
    private final Gameloop gameloop;
    private final boolean hasCustomLevel;

    public LevelSelector(View view, Gameloop gameloop) {
        hasCustomLevel = new File("customLevel.json").isFile();
        this.gameloop = gameloop;
        this.view = view;
        this.textures = new HashMap<String, Async<Texture>>() {
            {
                this.put("level_1_active", view.loadTexture("resources/GUI/Sliders/level_1_active.png"));
                this.put("level_1_inactive", view.loadTexture("resources/GUI/Sliders/level_1_inactive.png"));
                this.put("level_2_active", view.loadTexture("resources/GUI/Sliders/level_2_active.png"));
                this.put("level_2_inactive", view.loadTexture("resources/GUI/Sliders/level_2_inactive.png"));
                this.put("level_3_active", view.loadTexture("resources/GUI/Sliders/level_3_active.png"));
                this.put("level_3_inactive", view.loadTexture("resources/GUI/Sliders/level_3_inactive.png"));
                this.put("level_4_active", view.loadTexture("resources/GUI/Sliders/level_4_active.png"));
                this.put("level_4_inactive", view.loadTexture("resources/GUI/Sliders/level_4_inactive.png"));
                this.put("level_5_active", view.loadTexture("resources/GUI/Sliders/level_5_active.png"));
                this.put("level_5_inactive", view.loadTexture("resources/GUI/Sliders/level_5_inactive.png"));
                this.put("level_custom_active", view.loadTexture("resources/GUI/Sliders/level_custom_active.png"));
                this.put("level_custom_inactive", view.loadTexture("resources/GUI/Sliders/level_custom_inactive.png"));

                this.put("background", view.loadTexture("resources/GUI/Backgrounds/1.png"));
            }
        };

        this.backgroundSpriteID = view.createBackground(0, textures.get("background"), Vector3f.EMPTY,  new Vector3f(-0.02f, 0.01f, 0), System.nanoTime(), 1);

        try {
            this.menu = new GU_Menu(this.view, new ArrayList<GU_Menu.MenuItemInfo>() {
                {
                    this.add(new GU_Menu.SliderInfo(new EnterLevelHandler(), new SliderStateChanger(), new ArrayList<GU_Button.Textures>() {
                        {
                            this.add(new GU_Button.Textures(textures.get("level_1_inactive"), textures.get("level_1_active")));
                            this.add(new GU_Button.Textures(textures.get("level_2_inactive"), textures.get("level_2_active")));
                            this.add(new GU_Button.Textures(textures.get("level_3_inactive"), textures.get("level_3_active")));
                            this.add(new GU_Button.Textures(textures.get("level_4_inactive"), textures.get("level_4_active")));
                            this.add(new GU_Button.Textures(textures.get("level_5_inactive"), textures.get("level_5_active")));
                            if (hasCustomLevel) {
                                this.add(new GU_Button.Textures(textures.get("level_custom_inactive"), textures.get("level_custom_active")));
                            }
                        }
                    }, 0));
                }
            }, -0.9f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteMenu() {
        pause();
        this.textures.values().forEach(this.view::unloadTexture);
    }

    public void tick(Gameloop gameloop) {
        this.menu.tick(gameloop);
    }

    private int sliderState = 0;

    private class SliderStateChanger extends GU_Slider.SlideEventHandler {
        public void state(int state) {
            sliderState = state;
        }
    }

    private class EnterLevelHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            enterLevel();
        }
    }
    private void enterLevel() {
        switch (this.sliderState) {
            case 0:
                this.gameloop.loadLevel("resources/maps/plain.json");
                this.gameloop.exitPause();
                break;
            case 1:
                this.gameloop.loadLevel("resources/maps/maptest.json");
                this.gameloop.exitPause();
                break;
            case 2:
                this.gameloop.loadLevel("resources/maps/bumpertest.json");
                this.gameloop.exitPause();
                break;
            case 3:
                this.gameloop.loadLevel("resources/maps/sebtest.json");
                this.gameloop.exitPause();
                break;
            case 4:
                this.gameloop.loadLevel("resources/maps/testtiles.json");
                this.gameloop.exitPause();
                break;
            case 5:
                if (this.hasCustomLevel) {
                    this.gameloop.loadLevel("customLevel.json");
                    this.gameloop.exitPause();
                }
                break;
        }
    }
}