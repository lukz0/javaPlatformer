package GUI_Utils;

import Game.Gameloop;
import Game.View;

import java.util.ArrayList;
import java.util.HashMap;

public class GU_Menu {
    private static final long TICK_DELAY = Long.divideUnsigned(Long.divideUnsigned(1000, Gameloop.TICKDURATION), 3);
    private long delayLeft = 0;

    private final View view;
    private final ArrayList<Item> itemList = new ArrayList<>();
    private int activeItem = 0;

    // This constructor is for GU_Button only menus
    public GU_Menu(View view, HashMap<GU_Button.Textures, GU_Button.EnterEventHandler> buttonList, float z_index) throws Exception {
        this.view = view;
        ButtonPositionCounter posCount = new ButtonPositionCounter();
        buttonList.forEach((textures, enterEventHandler) -> {
            float position = posCount.getPosition();
            this.itemList.add(new GU_Button(this.view, 0.5f, 5.5f, position, position-1, z_index, textures, enterEventHandler));
        });
        if (this.itemList.size() == 0) {
            throw new Exception("buttonList cannot be empty");
        } else {
            this.itemList.get(this.activeItem).activate(view);
        }
    }

    public void tick(Gameloop gameloop) {
        if (delayLeft == 0) {
            if (gameloop.holdingEnter) {
                this.handlerEnter();
            } else if (gameloop.holdingUp != gameloop.holdingDown) {
                if (gameloop.holdingUp) {
                    this.handleUp();
                } else {
                    this.handleDown();
                }
            } else if (gameloop.holdingLeft != gameloop.holdingRight) {
                if (gameloop.holdingLeft) {
                    this.handleLeft();
                } else {
                    this.handleRight();
                }
            }
        } else {
            delayLeft--;
        }
    }

    private void handlerEnter() {
        this.itemList.get(this.activeItem).enter_event(this.view);
        this.delay();
    }
    private void handleUp() {
        this.itemList.get(this.activeItem).deactivate(this.view);
        this.activeItem = (this.activeItem == 0) ? this.itemList.size()-1 : this.activeItem - 1;
        this.itemList.get(this.activeItem).activate(this.view);
        this.delay();
    }
    private void handleDown() {
        this.itemList.get(this.activeItem).deactivate(this.view);
        this.activeItem = (this.activeItem == this.itemList.size()-1) ? 0 : this.activeItem + 1;
        this.itemList.get(this.activeItem).activate(this.view);
        this.delay();
    }
    private void handleLeft() {
        this.delay();
    }
    private void handleRight() {
        this.delay();
    }
    private void delay() {
        this.delayLeft = GU_Menu.TICK_DELAY;
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
        void pause(View view);
        void unPause(View view);
    }

    public void pause() {
        this.itemList.forEach(item -> item.pause(this.view));
    }
    public void unPause() {
        this.itemList.forEach(item -> item.unPause(this.view));
    }
}
