package GUI_Utils;

import Game.*;

import java.util.HashMap;

public class Digit {
    Async<Integer> spriteID;

    public Digit(View view, TextCreator tc, float height, float z_index, Vector3f translation) {
        HashMap<Integer, Async<Renderer.Drawable>> digitSymbols = new HashMap<>();
        for (int i = 0; i <= 9; i++) {
            digitSymbols.put(i ,view.getNewTexturedRectangle(0, height*0.5f, height, 0, z_index, tc.renderString(view, Integer.toString(i)),
                    Vector3f.EMPTY, Vector3f.EMPTY, 0));
        }

        this.spriteID = view.createPosUpdateableGroup(translation, Vector3f.EMPTY, digitSymbols, 0);
    }

    public void setState(View view, int i) {
        view.setActiveState(this.spriteID, i);
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
}
