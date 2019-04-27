package PauseMenu;

import GUI_Utils.GU_Button;
import GUI_Utils.GU_Menu;
import Game.Async;
import Game.Gameloop;
import Game.Texture;
import Game.View;

import java.util.Arrays;
import java.util.HashMap;


public class Main {
    private static final long TICK_DELAY = Long.divideUnsigned(Long.divideUnsigned(1000, Gameloop.TICKDURATION), 2);

    private final View view;
    private final HashMap<String, Async<Texture>> textures;

    public Main(View view) {
        this.view = view;
        this.textures = new HashMap<String, Async<Texture>>() {
            {
                this.put("exit_active", view.loadTexture("resources/GUI/Buttons/exit_active.png"));
                this.put("exit_inactive", view.loadTexture("resources/GUI/Buttons/exit_inactive.png"));
                this.put("resume_active", view.loadTexture("resources/GUI/Buttons/resume_active.png"));
                this.put("resume_inactive", view.loadTexture("resources/GUI/Buttons/resume_inactive.png"));
            }
        };

        new GU_Menu(this.view, new HashMap<GU_Button.Textures, GU_Button.EnterEventHandler>() {
            {
                this.put(new GU_Button.Textures(textures.get("resume_inactive"), textures.get("resume_active")), new ResumeHandler());
                this.put(new GU_Button.Textures(textures.get("exit_inactive"), textures.get("exit_active")), new ExitHandler());
            }
        }, -0.9f);
    }

    public void tick(Gameloop gameloop) {

    }

    public void deleteMenu() {
        this.textures.values().forEach(view::unloadTexture);
    }

    private class ResumeHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            System.out.println("Resume event!");
        }
    }
    private class ExitHandler extends GU_Button.EnterEventHandler {
        public void enter() {
            System.out.println("Exit event!");
        }
    }
}
