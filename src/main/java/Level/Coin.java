package Level;

import Game.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Coin  extends Entity {
    private static final int STATE_IDLE = 1;
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    int currentState;
    Level level;
    boolean interactable = true;

    public Coin(View view, HashMap<String, Async<Texture>> textures, long timestamp, double xPos, double yPos, int chunkIndex, Level level) {
        this.width = 1;
        this.height = 1;
        this.level = level;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.chunkIndex = chunkIndex;
        if (!textures.containsKey("coin.png")) {
            textures.put("coin.png", view.loadTexture("resources/images/coin.png"));
        }
        Async<Renderer.Drawable> idleSprite = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("coin.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(STATE_IDLE, idleSprite);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = STATE_IDLE;
        view.setActiveState(this.drawableID, this.currentState);
    }


    @Override
    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {
    }

    @Override
    public void updatePos() {
        this.yPos += this.yVelocity;
    }
}
