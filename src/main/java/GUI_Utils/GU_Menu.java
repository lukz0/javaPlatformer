package GUI_Utils;

import Game.View;

import java.util.HashMap;

public class GU_Menu {
    private final View view;

    // This constructor is for GU_Button only menus
    public GU_Menu(View view, HashMap<GU_Button.Textures, GU_Button.EnterEventHandler> buttonList, float z_index) {
        this.view = view;
        ButtonPositionCounter posCount = new ButtonPositionCounter();
        buttonList.forEach((textures, enterEventHandler) -> {
            float position = posCount.getPosition();
            new GU_Button(this.view, 0.5f, 5.5f, position, position-1, z_index, textures, enterEventHandler);
        });
    }

    private static class ButtonPositionCounter {
        private float currentPosition = 10f;
        float getPosition() {
            this.currentPosition -= 1.5f;
            return this.currentPosition;
        }
    }

    public static interface Item {
        void activate(View view);
        void deactivate(View view);
        void left_event(View view);
        void right_event(View view);
        void enter_event(View view);
    }
}
