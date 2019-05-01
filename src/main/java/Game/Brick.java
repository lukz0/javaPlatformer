package Game;

import Level.Block.AbstractBlock;
import Level.Chunk;
import Level.Entity;
import Level.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class Brick extends Entity {
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    private static final int STATE_IDLE = 1;
    int currentState;
    Level level;

    public Brick(View view, HashMap<String, Async<Texture>> textures, long timestamp, double xPos, double yPos, int chunkIndex, Level level) {
        this.width = 1;
        this.height = 1;
        this.level = level;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.chunkIndex = chunkIndex;
        if (!textures.containsKey("brick.png")) {
            textures.put("brick.png", view.loadTexture("resources/images/brick.png"));
        }

        Async<Renderer.Drawable> idleSprite = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("brick.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(Game.Brick.STATE_IDLE, idleSprite);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = Brick.STATE_IDLE;
        view.setActiveState(this.drawableID, this.currentState);
    }

    @Override
    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {}

    @Override
    public boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target) {
        return false;
    }

    @Override
    public void updatePos() {}

    @Override
    public boolean collisionEntEnt(Entity target) {
        return false;
    }
}
