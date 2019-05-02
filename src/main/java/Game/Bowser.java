package Game;

import Level.Block.AbstractBlock;
import Level.Chunk;
import Level.Entity;
import Level.Level;

import java.util.ArrayList;
import java.util.HashMap;

public class Bowser extends Entity {
    private static final int STATE_MOVING_RIGHT = 1;
    private static final int STATE_MOVING_LEFT = 2;
    private static final int STATE_DEAD = 3;
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    int currentState;
    Level level;
    Mario mario;

    public Bowser(View view, HashMap<String, Async<Texture>> textures, long timestamp, double xPos, double yPos, int chunkIndex, Level level) {
        this.width = 2;
        this.height = 2;
        this.level = level;
        this.xPos = xPos;
        this.yPos = yPos;
        this.xVelocity = 0;
        this.yVelocity = 0;
        this.chunkIndex = chunkIndex;
        this.mario = level.player;
        if (!textures.containsKey("notBowser.png")) {
            textures.put("notBowser.png", view.loadTexture("resources/images/notBowser.png"));
        }
        Async<Renderer.Drawable> movingRightSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("notBowser.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> movingLeftSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("notBowser.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> deadSprite = view.getNewTexturedRectangle(0, 1, 0, 1, -0.5f, textures.get("notBowser.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(Bowser.STATE_MOVING_RIGHT, movingRightSprite);
        states.put(Bowser.STATE_MOVING_LEFT, movingLeftSprite);
        states.put(Bowser.STATE_DEAD, deadSprite);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = Bowser.STATE_MOVING_LEFT;
        view.setActiveState(this.drawableID, this.currentState);
    }

    @Override
    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {
        int dist = (int) (this.mario.xPos - this.xPos); // 9*chunkindex + xPos så Bowser går mot Mario selv fra en annen chunk?
        if(Math.abs(dist) < 2) {
            if(dist < 0) {
                if(mario.yPos > this.yPos) {
                    xVelocity = -2.5f * (Gameloop.TICKDURATION/(float)1000);
                    this.currentState = Bowser.STATE_MOVING_LEFT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                } else {
                    //jump I guess
                    xVelocity = 2.5f * (Gameloop.TICKDURATION/(float)1000);
                    this.currentState = Bowser.STATE_MOVING_RIGHT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            } else {
                if(mario.yPos > this.yPos) {
                    xVelocity = 2.5f * (Gameloop.TICKDURATION/(float)1000);
                    this.currentState = Bowser.STATE_MOVING_RIGHT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                } else {
                    xVelocity = -2.5f * (Gameloop.TICKDURATION/(float)1000);
                    this.currentState = Bowser.STATE_MOVING_LEFT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            }
        } else {
            if(dist < 0) {
                xVelocity = 2.5f * (Gameloop.TICKDURATION/(float)1000);
                this.currentState = Bowser.STATE_MOVING_RIGHT;
                gameloop.view.setActiveState(this.drawableID, this.currentState);
            } else {
                xVelocity = -2.5f * (Gameloop.TICKDURATION/(float)1000);
                this.currentState = Bowser.STATE_MOVING_LEFT;
                gameloop.view.setActiveState(this.drawableID, this.currentState);
            }
        }


    }

    @Override
    public boolean collisionEntEnt(Entity target) {
        return false;
    }

    @Override
    public boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target) {
        return false;
    }

    @Override
    public void updatePos() {

    }
}