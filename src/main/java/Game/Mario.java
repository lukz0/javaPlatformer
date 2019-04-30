package Game;


import Level.Block.AbstractBlock;
import Level.Block.NonStaticAbstractBlock;
import Level.Chunk;
import Level.Entity;

import java.util.ArrayList;
import java.util.HashMap;

public class Mario extends Entity {
    private static final int STATE_MOVING_RIGHT = 1;
    private static final int STATE_MOVING_LEFT = 2;
    private static final int STATE_IDLE_RIGHT = 3;
    private static final int STATE_IDLE_LEFT = 4;
    private static final int STATE_JUMP_RIGHT = 5;
    private static final int STATE_JUMP_LEFT = 6;
    Vector3f translation = Vector3f.EMPTY, velocity = Vector3f.EMPTY;
    int currentState;
    private boolean grounded = true;

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
        if (!textures.containsKey("mario_jump.png")) {
            textures.put("mario_jump.png", view.loadTexture("resources/images/mario_jump.png"));
        }
        Async<Renderer.Drawable> movingRightSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("mario_right.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> movingLeftSprite = view.getNewAnimatedTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_right.png"),
                this.translation, this.velocity, 500, timestamp);
        Async<Renderer.Drawable> marioIdleRight = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("mario_stand.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioIdleLeft = view.getNewTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_stand.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioJumpRight = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("mario_jump.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioJumpLeft = view.getNewTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_jump.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(STATE_MOVING_RIGHT, movingRightSprite);
        states.put(STATE_MOVING_LEFT, movingLeftSprite);
        states.put(STATE_IDLE_RIGHT, marioIdleRight);
        states.put(STATE_IDLE_LEFT, marioIdleLeft);
        states.put(STATE_JUMP_RIGHT, marioJumpRight);
        states.put(STATE_JUMP_LEFT, marioJumpLeft);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = STATE_IDLE_RIGHT;
        view.setActiveState(this.drawableID, this.currentState);
    }


    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {
        System.out.println("Chunk index: "+ Integer.toString(this.chunkIndex));
        System.out.println("Xpos : "+ Double.toString(this.xPos));
        if (!this.isPaused) {
            if (gameloop.holdingLeft != gameloop.holdingRight) {
                if (gameloop.holdingLeft) {
                    this.xVelocity = -3 * (Gameloop.TICKDURATION / (double) 1000);
                    if (this.currentState != STATE_MOVING_LEFT && grounded) {
                        this.currentState = STATE_MOVING_LEFT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    } else if(!grounded) {
                        this.currentState = STATE_JUMP_LEFT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    }
                } else {
                    this.xVelocity = 3 * (Gameloop.TICKDURATION / (double) 1000);
                    if (this.currentState != STATE_MOVING_RIGHT && grounded) {
                        this.currentState = STATE_MOVING_RIGHT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    } else if(!grounded) {
                        this.currentState = STATE_JUMP_RIGHT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    }
                }
            } else {
                this.xVelocity = 0;
                if ((this.currentState == STATE_MOVING_LEFT || this.currentState == STATE_JUMP_LEFT) && grounded) {
                    this.currentState = STATE_IDLE_LEFT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                } else if ((this.currentState == STATE_MOVING_RIGHT || this.currentState == STATE_JUMP_RIGHT) && grounded) {
                    this.currentState = STATE_IDLE_RIGHT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            }
            if (gameloop.holdingSpace) {
                //System.out.println("Mario is grounded: " + grounded + " / His Y is " + this.yVelocity);
                //if(grounded) {
                    if (this.currentState == STATE_IDLE_LEFT || this.currentState == STATE_MOVING_LEFT) {
                        this.currentState = STATE_JUMP_LEFT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    } else if (this.currentState == STATE_IDLE_RIGHT || this.currentState == STATE_MOVING_RIGHT) {
                        this.currentState = STATE_JUMP_RIGHT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    }

                    // TODO: find appropriate value
                    this.yVelocity += 0.2f * (Gameloop.TICKDURATION/(double)1000);
                    this.grounded = false;
                //}
                /*else {
                    this.yVelocity = 0;
                }*/
            }

            // TODO: change
            this.yVelocity -= 0.05f * (Gameloop.TICKDURATION/(double)1000);
        }

        // TODO: add collision detection before adding velocity to position
        // TODO: changing chunks when translation < 0 or translation > 9


        //gameloop.view.updatePosition(this.drawableID, this.translation, this.velocity, tickStart);
    }

    @Override
    public void updatePos(){
        this.xPos += this.xVelocity;
        this.yPos += this.yVelocity;
    }

    @Override
    public boolean collisionEntEnt(Entity target) {
        double mXA = this.xPos + this.xVelocity;
        double mYA = this.yPos + this.yVelocity;
        if(target instanceof Brick) {
            if((mXA <= target.xPos + target.width) &&
                    (mXA + this.width >= target.xPos) &&
                    (mYA <= target.yPos + target.height) &&
                    (mYA + this.height >= target.yPos)) {
                this.yPos = this.yVelocity <= 0 ? mYA + 1 : mYA - 1;
                if(this.yVelocity <= 0) {
                    this.grounded = true;
                }
                this.yVelocity = 0;
                mYA = (int) this.yPos;

            }
            if(grounded && (mXA <= target.xPos + target.width) &&
                    (mXA + this.width >= target.xPos) &&
                    (mYA <= target.yPos + target.height) &&
                    (mYA + this.height >= target.yPos)) {
                this.xVelocity = 0;
                return true;
            }
        } else {
            if ((mXA <= target.xPos + target.width) &&
                    (mXA + this.width >= target.xPos) &&
                    (mYA <= target.yPos + target.height) &&
                    (mYA + this.height >= target.yPos)) {
                if (this.yVelocity < 0) {
                    //Goomba ded, Mario jumps
                    this.yVelocity += 0.5f * (Gameloop.TICKDURATION/(double)1000);
                } else {
                    //System.out.println("Mario has died. Please close the game.");
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target) {
        int mXA = (int) Math.floor(this.xPos + this.xVelocity);
        int mYA = (int) Math.floor(this.yPos + this.yVelocity);
        //System.out.println(mXA + " / " + mYA);

        if ((target.get(mYA).get(mXA) != null && !(target.get(mYA).get(mXA) instanceof NonStaticAbstractBlock))||
                (target.get(mYA).get(mXA + (int) this.width) != null && !(target.get(mYA).get(mXA + (int) this.width) instanceof NonStaticAbstractBlock)) ||
                (target.get(mYA + (int) this.height).get(mXA) != null && !(target.get(mYA + (int) this.height).get(mXA) instanceof NonStaticAbstractBlock)) ||
                (target.get(mYA + (int) this.height).get(mXA + (int) this.width) != null && !(target.get(mYA + (int) this.height).get(mXA + (int) this.width) instanceof NonStaticAbstractBlock))) {
            this.yPos = this.yVelocity <= 0 ? mYA + 1 : mYA - 1;
            if(this.yVelocity <= 0) {
                this.grounded = true;
            }
            this.yVelocity = 0;
            mYA = (int) this.yPos;
        }

        if ((target.get(mYA).get(mXA) != null && !(target.get(mYA).get(mXA) instanceof NonStaticAbstractBlock))||
                (target.get(mYA).get(mXA + (int) this.width) != null && !(target.get(mYA).get(mXA + (int) this.width) instanceof NonStaticAbstractBlock)) ||
                (target.get(mYA + (int) this.height).get(mXA) != null && !(target.get(mYA + (int) this.height).get(mXA) instanceof NonStaticAbstractBlock)) ||
                (target.get(mYA + (int) this.height).get(mXA + (int) this.width) != null && !(target.get(mYA + (int) this.height).get(mXA + (int) this.width) instanceof NonStaticAbstractBlock))) {
            this.xVelocity = 0;
            return true;
        }

        return this.grounded;
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