package GUI_Utils;

import Game.Async;
import Game.Gameloop;
import Game.Texture;
import Game.View;

import java.util.ArrayList;
import java.util.Map.Entry;

public class GU_Menu {
    private final View view;
    private final ArrayList<Item> itemList = new ArrayList<>();
    private int activeItem = 0;

    public interface MenuItemInfo {}

    public static class SimpleButtonInfo extends ArrayList<Object> implements Entry<GU_Button.Textures, GU_Button.EnterEventHandler>, MenuItemInfo {
        public SimpleButtonInfo(GU_Button.EnterEventHandler handler, Async<Texture> inactive, Async<Texture> active) {
            super(2);
            this.add(new GU_Button.Textures(inactive, active));
            this.add(handler);
        }
        public GU_Button.Textures getKey() {
            return (GU_Button.Textures)this.get(0);
        }
        public GU_Button.EnterEventHandler getValue() {
            return (GU_Button.EnterEventHandler)this.get(1);
        }
        public GU_Button.EnterEventHandler setValue(GU_Button.EnterEventHandler value) {return null;}
    }

    public static class SliderInfo extends ArrayList<Object> implements Entry<ArrayList<GU_Button.Textures>, GU_Slider.EventHandlers>, MenuItemInfo {
        public SliderInfo(GU_Button.EnterEventHandler enterEventHandler, GU_Slider.SlideEventHandler slideEventHandler, ArrayList<GU_Button.Textures> textures, int initialState) {
            super(3);
            this.add(new GU_Slider.EventHandlers(enterEventHandler, slideEventHandler));
            this.add(textures);
            this.add(initialState);
        }
        public ArrayList<GU_Button.Textures> getKey() {
            return (ArrayList<GU_Button.Textures>)this.get(1);
        }
        public GU_Slider.EventHandlers getValue() {
            return (GU_Slider.EventHandlers)this.get(0);
        }
        public int getInitialState() { return (int)this.get(2);}
        public GU_Slider.EventHandlers setValue(GU_Slider.EventHandlers value) {return null;}
    }

    public GU_Menu(View view, ArrayList<MenuItemInfo> itemList, float z_index) throws Exception {
        if (itemList.size() == 0) {
            throw new Exception("buttonList cannot be empty");
        } else {
            this.view = view;
            ButtonPositionCounter posCount = new ButtonPositionCounter();
            itemList.forEach(entry -> {
                float position = posCount.getPosition();
                if (entry instanceof SimpleButtonInfo) {
                    this.itemList.add(new GU_Button(this.view, 0.5f, 5.5f, position, position - 1, z_index,
                            ((SimpleButtonInfo) entry).getKey(), ((SimpleButtonInfo) entry).getValue()));
                } else if (entry instanceof SliderInfo) {
                    this.itemList.add(new GU_Slider(this.view, 0.5f, 10.5f, position, position - 1, z_index,
                            ((SliderInfo) entry).getKey(), ((SliderInfo) entry).getValue().enterEventHandler,
                            ((SliderInfo) entry).getValue().slideEventHandler, ((SliderInfo) entry).getInitialState()));
                } else {
                    try {
                        throw new Exception("Unknown type in itemList: " + entry);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            this.itemList.get(this.activeItem).activate(view);
        }
    }

    public void tick(Gameloop gameloop) {
            if (gameloop.holdingEnter) {
                gameloop.holdingEnter = false;
                this.handlerEnter();
            } else if (gameloop.holdingUp != gameloop.holdingDown) {
                if (gameloop.holdingUp) {
                    gameloop.holdingUp = false;
                    this.handleUp();
                } else {
                    gameloop.holdingDown = false;
                    this.handleDown();
                }
            } else if (gameloop.holdingLeft != gameloop.holdingRight) {
                if (gameloop.holdingLeft) {
                    gameloop.holdingLeft = false;
                    this.handleLeft();
                } else {
                    gameloop.holdingRight = false;
                    this.handleRight();
                }
            }
    }

    private void handlerEnter() {
        this.itemList.get(this.activeItem).enter_event(this.view);
    }
    private void handleUp() {
        this.itemList.get(this.activeItem).deactivate(this.view);
        this.activeItem = (this.activeItem == 0) ? this.itemList.size()-1 : this.activeItem - 1;
        this.itemList.get(this.activeItem).activate(this.view);
    }
    private void handleDown() {
        this.itemList.get(this.activeItem).deactivate(this.view);
        this.activeItem = (this.activeItem == this.itemList.size()-1) ? 0 : this.activeItem + 1;
        this.itemList.get(this.activeItem).activate(this.view);
    }
    private void handleLeft() {
        this.itemList.get(this.activeItem).left_event(this.view);
    }
    private void handleRight() {
        this.itemList.get(this.activeItem).right_event(this.view);
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
