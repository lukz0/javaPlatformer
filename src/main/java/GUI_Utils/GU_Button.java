package GUI_Utils;

import Game.*;

import java.util.HashMap;

public class GU_Button implements GU_Menu.Item {
    static final int INACTIVE = 0;
    static final int ACTIVE = 1;
    private Async<Integer> spriteID;
    private final EnterEventHandler enterEventHandler;

    public GU_Button(View view, float left, float right, float top, float bottom, float z_index, Textures textures, EnterEventHandler enterEventHandler) {
        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(GU_Button.INACTIVE, view.getNewTexturedRectangle(left, right, top, bottom, z_index, textures.texture_inactive,
                Vector3f.EMPTY, Vector3f.EMPTY, 0));
        states.put(GU_Button.ACTIVE, view.getNewTexturedRectangle(left, right, top, bottom, z_index, textures.texture_active,
                Vector3f.EMPTY, Vector3f.EMPTY, 0));
        this.spriteID = view.createPosUpdateableGroup(Vector3f.EMPTY, Vector3f.EMPTY, states, 0);
        this.enterEventHandler = enterEventHandler;
    }

    private boolean isPaused = false;
    private Async<Renderer.Drawable> pausedSprite = null;

    public void pause(View view) {
        if (!this.isPaused) {
            this.isPaused = true;
            this.pausedSprite = view.getDrawableByID(this.spriteID);
        }
    }

    public void unPause(View view) {
        if (this.isPaused) {
            this.isPaused = false;
            this.spriteID = view.addToStage(this.pausedSprite);
        }
    }

    public void activate(View view) {
        if (!this.isPaused) {
            view.setActiveState(this.spriteID, GU_Button.ACTIVE);
        }
    }

    public void deactivate(View view) {
        if (!this.isPaused) {
            view.setActiveState(this.spriteID, GU_Button.INACTIVE);
        }
    }

    public static class Textures {
        public final Async<Texture> texture_inactive, texture_active;

        public Textures(Async<Texture> texture_inactive, Async<Texture> texture_Active) {
            this.texture_inactive = texture_inactive;
            this.texture_active = texture_Active;
        }
    }

    public void left_event(View view) {
    }

    public void right_event(View view) {
    }

    public void enter_event(View view) {
        this.enterEventHandler.enter();
    }

    public static abstract class EnterEventHandler {
        public abstract void enter();
    }
}
