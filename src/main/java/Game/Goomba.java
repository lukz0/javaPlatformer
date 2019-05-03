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
    private static final int STATE_DEAD = 3;
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    int currentState;
    Level level;

    public Goomba(View view, HashMap<String, Async<Texture>> textures, long timestamp, double xPos, double yPos, int chunkIndex, Level level) {
        this.width = 1;
        this.height = 1;
        this.level = level;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVelocity = 1 * (Gameloop.TICKDURATION/(float)1000);
        this.yVelocity = 0;
        this.chunkIndex = chunkIndex;
        if (!textures.containsKey("primeGoomb_fwd.png")) {
            textures.put("primeGoomb_fwd.png", view.loadTexture("resources/images/primeGoomb_fwd.png"));
        }
        if (!textures.containsKey("primeGoomb.png")) {
            textures.put("primeGoomb.png", view.loadTexture("resources/images/primeGoomb.png"));
        }

        Async<Renderer.Drawable> movingRightSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("primeGoomb_fwd.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> movingLeftSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("primeGoomb_fwd.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> deadSprite = view.getNewTexturedRectangle(0, 1, 0, 1, -0.5f, textures.get("primeGoomb.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(Goomba.STATE_MOVING_RIGHT, movingRightSprite);
        states.put(Goomba.STATE_MOVING_LEFT, movingLeftSprite);
        states.put(Goomba.STATE_DEAD, deadSprite);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = Goomba.STATE_MOVING_RIGHT;
        view.setActiveState(this.drawableID, this.currentState);
    }

    @Override
    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {
        //System.out.println("[GOOMBA] This Goomba's velocity is " + this.xVelocity);
        double nextXPos = this.xPos + this.xVelocity;

        if(!this.interactable) {
            this.yVelocity -= 0.1 * (Gameloop.TICKDURATION/(float)1000);
            this.currentState = Goomba.STATE_DEAD;
            gameloop.view.setActiveState(this.drawableID, this.currentState);

            return;
        }
        switch (this.moving){
            case MOVING_LEFT:
                this.xVelocity-=0.1f*(Gameloop.TICKDURATION/(float)1000);
                break;
            case MOVING_RIGHT:
                this.xVelocity+=0.1f*(Gameloop.TICKDURATION/(float)1000);
                break;
        }
        this.yVelocity-=0.1f * (Gameloop.TICKDURATION/(float)1000);
        /*
        if (nextXPos < 0) {
            this.xVelocity = 1 * (Gameloop.TICKDURATION/(float)1000);
            nextXPos = this.xPos + this.xVelocity;
        } else if (nextXPos > (9 - this.width)) {
            this.xVelocity = -1 * (Gameloop.TICKDURATION/(float)1000);
            nextXPos = this.xPos + this.xVelocity;
        }
        */

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
        this.yPos += this.yVelocity;
    }

    /*
    @Override
    public boolean collisionEntEnt(Entity target, Gameloop gameloop) {
        double mXA = this.xPos + this.xVelocity;
        double mYA = this.yPos + this.yVelocity;
        if (!(target instanceof Mario)) {
            if ((mXA <= target.xPos + target.width) && (mXA + this.width >= target.xPos) && (mYA <= target.yPos + target.height) && (mYA + this.height >= target.yPos)) {
                this.xVelocity *= -1;
            }
            return true;
        }
        return false;
    }*/
}
