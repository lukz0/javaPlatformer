package Game;

import Level.Block.AbstractBlock;
import Level.Chunk;
import Level.Entity;
import Level.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class Goomba extends Entity {
    private static final int STATE_MOVING_RIGHT = 1;
    private static final int STATE_MOVING_LEFT = 2;
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    int currentState;
    Level level;

    public Goomba(View view, HashMap<String, Async<Texture>> textures, long timestamp, double xPos, double yPos, int chunkIndex, Level level) {
        this.width = 1;
        this.height = 1;
        this.level = level;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVelocity = 3 * (Gameloop.TICKDURATION/(float)1000);
        this.yVelocity = 0;
        this.chunkIndex = chunkIndex;
        if (!textures.containsKey("primeGoomb_fwd.png")) {
            textures.put("primeGoomb_fwd.png", view.loadTexture("resources/images/primeGoomb_fwd.png"));
        }

        Async<Renderer.Drawable> movingRightSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("primeGoomb_fwd.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> movingLeftSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("primeGoomb_fwd.png"),
                this.translation, this.velocity, 500, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(Goomba.STATE_MOVING_RIGHT, movingRightSprite);
        states.put(Goomba.STATE_MOVING_LEFT, movingLeftSprite);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = Goomba.STATE_MOVING_RIGHT;
        view.setActiveState(this.drawableID, this.currentState);
    }

    @Override
    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {
        double nextXPos = this.xPos + this.xVelocity;
        if (nextXPos < 0) {
            this.xVelocity = 3 * (Gameloop.TICKDURATION/(float)1000);
            nextXPos = this.xPos + this.xVelocity;
        } else if (nextXPos > (9-this.width)) {
            this.xVelocity = -3 * (Gameloop.TICKDURATION/(float)1000);
            nextXPos = this.xPos + this.xVelocity;
        }

        //TODO: add logic regarding choice of direction
        /*Vector3f nextPos = this.translation.add(this.velocity);
        if(nextPos.values[0] < 0) {
            this.velocity = new Vector3f(3 * (gameloop.TICKDURATION/(float)1000), 0, 0);
            this.currentState = Goomba.STATE_MOVING_RIGHT;
        }
        else if(nextPos.values[0] > 15) {
            this.velocity = new Vector3f(-3 * (gameloop.TICKDURATION/(float)1000), 0, 0);
            this.currentState = Goomba.STATE_MOVING_LEFT;
        }*/


        // TODO: replace when we add collisions
        /*this.translation = this.translation.add(this.velocity);
        gameloop.view.updatePosition(this.drawableID, this.translation, this.velocity, tickStart);*/
    }

    @Override
    public void updatePos(){
        this.xPos += this.xVelocity;
    }

    @Override
    public boolean collisionEntBlc(AbstractBlock target) {
        double mXA = this.xPos + this.xVelocity;
        double mYA = this.yPos + this.yVelocity;
        /*
        if ((mXA <= target.xPos + 1) && (mXA + 1 >= target.xPos) && (mYA <= target.yPos + 1) && (mYA + 1 >= target.yPos)) {
            this.yVelocity = 0;
            mYA = this.yPos;

        }
        if ((mXA <= target.xPos + 1) && (mXA + 1 >= target.xPos) && (mYA <= target.yPos + 1) && (mYA + 1 >= target.yPos)) {
            this.xVelocity *= -1;
            return true;
        }
        */
        return false;
    }

    @Override
    public boolean collisionEntEnt(Entity target) {
        double mXA = this.xPos + this.xVelocity;
        double mYA = this.yPos + this.yVelocity;
        if ((mXA <= target.xPos + 1) && (mXA + 1 >= target.xPos) && (mYA <= target.yPos + 1) && (mYA + 1 >= target.yPos)) {
            if (target instanceof Mario) {
                //Do nothing, Mario handles stuff
                //Possibly die here if Mario moves downward?
            }
            else {
                this.xVelocity *= -1;
            }
            return true;
        }
        return false;
    }

    private boolean isPaused = false;
    private Async<Renderer.Drawable> pausedDrawable = null;

    public void pause(View view) {
        if (!this.isPaused) {
            this.isPaused = true;
            this.pausedDrawable = view.getDrawableByID(this.drawableID);
        }
    }

    public void unPause(View view) {
        if (this.isPaused) {
            this.isPaused = false;
            this.drawableID = view.addToStage(this.pausedDrawable);
        }
    }
}
