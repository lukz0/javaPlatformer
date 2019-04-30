package GUI_Utils;

import Game.Async;
import Game.Renderer;
import Game.Vector3f;
import Game.View;

import java.util.ArrayList;
import java.util.HashMap;

public class GU_Slider implements GU_Menu.Item {
    private final GU_Button.EnterEventHandler enterEventHandler;
    private final SlideEventHandler slideEventHandler;
    private final int stateAmount;

    private int currentState = 0;
    private Async<Integer> spriteID;

    public GU_Slider(View view, float left, float right, float top, float bottom, float z_index, ArrayList<GU_Button.Textures> textures, GU_Button.EnterEventHandler enterEventHandler, SlideEventHandler slideEventHandler) {
        this.enterEventHandler = enterEventHandler;
        this.slideEventHandler = slideEventHandler;
        this.stateAmount = textures.size();
        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        for (int i = 0; i < textures.size(); i++) {
            states.put(2*i, view.getNewTexturedRectangle(left, right, top, bottom, z_index, textures.get(i).texture_inactive,
                    Vector3f.EMPTY, Vector3f.EMPTY, 0));
            states.put(2*i+1, view.getNewTexturedRectangle(left, right, top, bottom, z_index, textures.get(i).texture_active,
                    Vector3f.EMPTY, Vector3f.EMPTY, 0));
        }
        this.spriteID = view.createPosUpdateableGroup(Vector3f.EMPTY, Vector3f.EMPTY, states, 0);
    }

    public void activate(View view) {
        if (!this.isPaused) {
            view.setActiveState(this.spriteID, this.currentState*2+1);
        }
    }

    public void deactivate(View view) {
        if (!this.isPaused) {
            view.setActiveState(this.spriteID, this.currentState*2);
        }
    }

    public void left_event(View view) {
        this.currentState = (this.currentState == 0) ? this.stateAmount - 1 : this.currentState - 1;
        this.slideEventHandler.state(this.currentState);
        view.setActiveState(this.spriteID, this.currentState*2+1);
    }

    public void right_event(View view) {
        this.currentState = (this.currentState == this.stateAmount - 1) ? 0 : this.currentState + 1;
        this.slideEventHandler.state(this.currentState);
        view.setActiveState(this.spriteID, this.currentState*2+1);
    }

    public void enter_event(View view) {
        this.enterEventHandler.enter();
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

    public static abstract class SlideEventHandler {
        public abstract void state(int state);
    }
    public static class EventHandlers {
        final GU_Button.EnterEventHandler enterEventHandler;
        final SlideEventHandler slideEventHandler;
        EventHandlers(GU_Button.EnterEventHandler enterEventHandler, SlideEventHandler slideEventHandler) {
            this.enterEventHandler = enterEventHandler;
            this.slideEventHandler = slideEventHandler;
        }
    }
}
