package Menus;

import GUI_Utils.GU_Menu;
import Game.Async;
import Game.Gameloop;
import Game.Renderer;
import Game.View;

public abstract class Menu {
    Async<Integer> backgroundSpriteID;
    View view;
    GU_Menu menu;

    private Async<Renderer.Drawable> pausedBackground = null;
    public boolean isPaused = false;
    public void pause() {
        if (!this.isPaused) {
            this.isPaused = true;
            this.menu.pause();
            this.pausedBackground = this.view.getDrawableByID(this.backgroundSpriteID);
        }
    }

    public void unPause() {
        if (this.isPaused) {
            this.isPaused = false;
            this.menu.unPause();
            this.backgroundSpriteID = this.view.addToStage(this.pausedBackground);
        }
    }

    public abstract void deleteMenu();
    public abstract void tick(Gameloop gameloop);
}
