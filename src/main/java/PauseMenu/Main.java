package PauseMenu;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Menu;
import Game.*;

import java.util.Arrays;
import java.util.HashMap;


public class Main {
    private final View view;
    private final Gameloop gameloop;
    private final HashMap<String, Async<Texture>> textures;
    private GU_Menu menu;
    private Async<Integer> backgroundSpriteID;

    public Main(View view, Gameloop gameloop) {
        this.view = view;
        this.gameloop = gameloop;
        this.textures = new HashMap<String, Async<Texture>>() {
            {
                this.put("exit_active", view.loadTexture("resources/GUI/Buttons/exit_active.png"));
                this.put("exit_inactive", view.loadTexture("resources/GUI/Buttons/exit_inactive.png"));
                this.put("resume_active", view.loadTexture("resources/GUI/Buttons/resume_active.png"));
                this.put("resume_inactive", view.loadTexture("resources/GUI/Buttons/resume_inactive.png"));
                this.put("background", view.loadTexture("resources/GUI/Backgrounds/1.png"));
            }
        };

        try {
            this.menu = new GU_Menu(this.view, new HashMap<GU_Button.Textures, GU_Button.EnterEventHandler>() {
                {
                    this.put(new GU_Button.Textures(textures.get("resume_inactive"), textures.get("resume_active")), new ResumeHandler());
                    this.put(new GU_Button.Textures(textures.get("exit_inactive"), textures.get("exit_active")), new ExitHandler());
                }
            }, -0.9f);
        } catch (Exception e) {
            e.printStackTrace();
            this.menu = null;
        }
        this.backgroundSpriteID = view.createBackground(0, this.textures.get("background"), Vector3f.EMPTY, new Vector3f(0.01f, 0.02f, 0), System.nanoTime(), 1);
    }

    public void tick(Gameloop gameloop) {
        try {
            this.menu.tick(gameloop);
        } catch(NullPointerException e) {}
    }

    public void deleteMenu() {
        System.out.println("Deleting menu");
        this.view.deleteDrawable(this.backgroundSpriteID);
        this.menu.pause();
        this.textures.values().forEach(view::unloadTexture);
    }

    private class ResumeHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            System.out.println("Resume event!");
            gameloop.exitPause();
        }
    }
    private class ExitHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            System.out.println("Exit event!");
            gameloop.stopGame(0);
        }
    }
}
