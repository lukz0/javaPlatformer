package Game;


import Level.Chunk;
import Level.Entity;

import java.util.ArrayList;
import java.util.HashMap;

public class Mario extends Entity {
    private static final int STATE_MOVING_RIGHT = 1;
    private static final int STATE_MOVING_LEFT = 2;
    private static final int STATE_IDLE_RIGHT = 3;
    private static final int STATE_IDLE_LEFT = 4;
    Async<Integer> drawableID;
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    int currentState;

    public Mario(View view, HashMap<String, Async<Texture>> textures, long timestamp, double xPos, double yPos, int chunkIndex) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.chunkIndex = chunkIndex;
        this.height = 1;
        this.width = 1;
        this.xVelocity = 0;
        this.yVelocity = 0;
        if (!textures.containsKey("mario_right.png")) {
            textures.put("mario_right.png", view.loadTexture("resources/images/mario_right.png"));
        }
        if (!textures.containsKey("mario_stand.png")) {
            textures.put("mario_stand.png", view.loadTexture("resources/images/mario_stand.png"));
        }
        /*if (!textures.containsKey("mario_jump.png")) {
            textures.put("mario_jump.png", view.loadTexture("resources/images/mario_jump.png"));
        }*/
        Async<Renderer.Drawable> movingRightSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("mario_right.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> movingLeftSprite = view.getNewAnimatedTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_right.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> marioIdleRight = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("mario_stand.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioIdleLeft = view.getNewTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_stand.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(this.STATE_MOVING_RIGHT, movingRightSprite);
        states.put(this.STATE_MOVING_LEFT, movingLeftSprite);
        states.put(this.STATE_IDLE_RIGHT, marioIdleRight);
        states.put(this.STATE_IDLE_LEFT, marioIdleLeft);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = this.STATE_IDLE_RIGHT;
        view.setActiveState(this.drawableID, this.currentState);
    }

    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {
        if (gameloop.holdingLeft != gameloop.holdingRight) {
            if (gameloop.holdingLeft) {
                this.xVelocity = -3 * (Gameloop.TICKDURATION/(double)1000);
                if (this.currentState != this.STATE_MOVING_LEFT) {
                    this.currentState = this.STATE_MOVING_LEFT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            } else {
                this.xVelocity = 3 * (Gameloop.TICKDURATION/(double)1000);
                if (this.currentState != this.STATE_MOVING_RIGHT) {
                    this.currentState = this.STATE_MOVING_RIGHT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            }
        } else {
            this.xVelocity = 0;
            if(this.currentState == this.STATE_MOVING_LEFT) {
                this.currentState = this.STATE_IDLE_LEFT;
                gameloop.view.setActiveState(this.drawableID, this.currentState);
            }
            else if (this.currentState == this.STATE_MOVING_RIGHT) {
                this.currentState = this.STATE_IDLE_RIGHT;
                gameloop.view.setActiveState(this.drawableID, this.currentState);
            }
        }

        //System.out.println("[MARIO] velocity: ".concat(Float.toString(this.velocity.values[0])));
        //System.out.println("[Mario] translation: ".concat(Float.toString(this.translation.values[0])));

        // TODO: add collision detection before adding velocity to position
        // TODO: changing chunks when translation < 0 or translation > 9
        this.xPos += this.xVelocity;

        this.velocity = new Vector3f((float)this.xVelocity, (float)this.yVelocity, 0);
        this.translation = new Vector3f((float)xPos, (float)yPos, 0);
        gameloop.view.updatePosition(this.drawableID, this.translation, this.velocity, tickStart);
    }
}