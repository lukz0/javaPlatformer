package Game;


import Level.Block.AbstractBlock;
import Level.Block.NonStaticAbstractBlock;
import Level.Block.StaticAbstractBlock;
import Level.Chunk;
import Level.Coin;
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
    private static final int STATE_DEAD = 7;
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
        if (!textures.containsKey("mario_dead.png")) {
            textures.put("mario_dead.png", view.loadTexture("resources/images/mario_dead.png"));
        }
        Async<Renderer.Drawable> movingRightSprite = view.getNewAnimatedTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("mario_right.png"),
                this.translation, this.velocity, 100, timestamp);
        Async<Renderer.Drawable> movingLeftSprite = view.getNewAnimatedTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_right.png"),
                this.translation, this.velocity, 100, timestamp);
        Async<Renderer.Drawable> marioIdleRight = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("mario_stand.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioIdleLeft = view.getNewTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_stand.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioJumpRight = view.getNewTexturedRectangle(0, 1, 1, 0, -0.5f, textures.get("mario_jump.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioJumpLeft = view.getNewTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_jump.png"),
                this.translation, this.velocity, timestamp);
        Async<Renderer.Drawable> marioDead = view.getNewTexturedRectangle(1, 0, 1, 0, -0.5f, textures.get("mario_dead.png"),
                this.translation, this.velocity, timestamp);

        HashMap<Integer, Async<Renderer.Drawable>> states = new HashMap<>();
        states.put(STATE_MOVING_RIGHT, movingRightSprite);
        states.put(STATE_MOVING_LEFT, movingLeftSprite);
        states.put(STATE_IDLE_RIGHT, marioIdleRight);
        states.put(STATE_IDLE_LEFT, marioIdleLeft);
        states.put(STATE_JUMP_RIGHT, marioJumpRight);
        states.put(STATE_JUMP_LEFT, marioJumpLeft);
        states.put(STATE_DEAD, marioDead);

        this.drawableID = view.createPosUpdateableGroup(this.translation, this.velocity, states, timestamp);
        this.currentState = STATE_IDLE_RIGHT;
        view.setActiveState(this.drawableID, this.currentState);
    }


    public void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart) {
        if (!this.isPaused) {
            if (gameloop.holdingLeft != gameloop.holdingRight && this.interactable) {  // Mario is either going left or right
                if (gameloop.holdingLeft) { // Holding and moving left
                    this.xVelocity = -3 * (Gameloop.TICKDURATION / (double) 1000);
                    if (this.currentState != STATE_MOVING_LEFT && grounded) {
                        this.currentState = STATE_MOVING_LEFT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    } else if (!grounded) {
                        this.currentState = STATE_JUMP_LEFT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    }
                } else {  // Holding and moving right
                    this.xVelocity = 3 * (Gameloop.TICKDURATION / (double) 1000);
                    if (this.currentState != STATE_MOVING_RIGHT && grounded) {
                        this.currentState = STATE_MOVING_RIGHT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    } else if (!grounded) {
                        this.currentState = STATE_JUMP_RIGHT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    }
                }
            } else {  // Either both or none of the buttons are pressed; Mario doesn't move
                this.xVelocity = 0;
                if ((this.currentState == STATE_MOVING_LEFT || this.currentState == STATE_JUMP_LEFT) && grounded) {
                    this.currentState = STATE_IDLE_LEFT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                } else if ((this.currentState == STATE_MOVING_RIGHT || this.currentState == STATE_JUMP_RIGHT) && grounded) {
                    this.currentState = STATE_IDLE_RIGHT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }
            }
            if (gameloop.holdingSpace && this.interactable) {  // Mario jumps, if he isn't dead
                if (this.currentState == STATE_IDLE_LEFT || this.currentState == STATE_MOVING_LEFT) {
                    this.currentState = STATE_JUMP_LEFT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                } else if (this.currentState == STATE_IDLE_RIGHT || this.currentState == STATE_MOVING_RIGHT) {
                    this.currentState = STATE_JUMP_RIGHT;
                    gameloop.view.setActiveState(this.drawableID, this.currentState);
                }

                if (this.grounded) {  // Jumping only increases his Y-velocity if on the ground
                    this.yVelocity = 9f * (Gameloop.TICKDURATION / (double) 1000);
                }
            }  // Gravity
            this.yVelocity -= 0.3f * (Gameloop.TICKDURATION / (double) 1000);

            // Mario dies from an enemy
            if (!this.interactable) {
                this.currentState = STATE_DEAD;
                gameloop.view.setActiveState(this.drawableID, this.currentState);
            }
        }
        this.grounded = false;
        if (this.yPos < -9f) {  // Mario dies from falling
            gameloop.marioDied();
        }
    }

    @Override
    public void updatePos() {
        this.xPos += this.xVelocity;
        this.yPos += this.yVelocity;
    }

    @Override
    public boolean collisionEntEnt(Entity target, Gameloop gameloop, int chunkOffset) {
        double mXA = this.xPos + this.xVelocity;
        double mYA = this.yPos + this.yVelocity;

        if (!this.interactable) {
            return false;
        }

        if (target instanceof Brick) {  // Handles collision like static blocks
            Brick targetBrick = (Brick) target;
            double thisMinX = this.xPos + ((this.xVelocity < 0) ? this.xVelocity : 0),
                    thisMaxX = this.xPos + this.width + ((this.xVelocity > 0) ? this.xVelocity : 0),
                    thisMinY = this.yPos + ((this.yVelocity < 0) ? this.yVelocity : 0),
                    thisMaxY = this.yPos + this.height + ((this.yVelocity > 0) ? this.yVelocity : 0);
            if (thisMinX < targetBrick.xPos + 1 + 9 * chunkOffset && thisMaxX > targetBrick.xPos + 9 * chunkOffset && thisMinY < targetBrick.yPos + 1 && thisMaxY > targetBrick.yPos) {
                HitDirection direction = handleBlockCollision((int) targetBrick.xPos + 9 * chunkOffset, (int) targetBrick.yPos);
                if (direction != null) {
                    switch (direction) {
                        case FROM_BELOW:
                            targetBrick.interactable = false;
                            break;
                        case FROM_ABOVE:
                            this.grounded = true;
                            break;
                    }
                }
            }
        } else if (target instanceof BreakableBrick) {  // Handles like static blocks, until it breaks
            BreakableBrick targetBrick = (BreakableBrick) target;
            if (targetBrick.isBroken()) {
                return false;
            } else {
                double thisMinX = this.xPos + ((this.xVelocity < 0) ? this.xVelocity : 0),
                        thisMaxX = this.xPos + this.width + ((this.xVelocity > 0) ? this.xVelocity : 0),
                        thisMinY = this.yPos + ((this.yVelocity < 0) ? this.yVelocity : 0),
                        thisMaxY = this.yPos + this.height + ((this.yVelocity > 0) ? this.yVelocity : 0);
                if (thisMinX < targetBrick.xPos + 1 + 9 * chunkOffset && thisMaxX > targetBrick.xPos + 9 * chunkOffset && thisMinY < targetBrick.yPos + 1 && thisMaxY > targetBrick.yPos) {
                    HitDirection direction = handleBlockCollision((int) targetBrick.xPos + 9 * chunkOffset, (int) targetBrick.yPos);
                    if (direction != null) {
                        switch (direction) {
                            case FROM_BELOW:
                                targetBrick.interactable = false;
                                break;
                            case FROM_ABOVE:
                                this.grounded = true;
                                break;
                        }
                    }
                }
            }
        } else if (target instanceof Coin && target.interactable) {  // Collide with coins to pick them up
            if ((mXA <= target.xPos + target.width + 9 * chunkOffset) &&
                    (mXA + this.width >= target.xPos + 9 * chunkOffset) &&
                    (mYA <= target.yPos + target.height) &&
                    (mYA + this.height >= target.yPos)) {
                target.interactable = false;
                gameloop.score += 50;
            }

        } else {  // An enemy, like Goombas and Bowser
            if (!target.interactable) {
                return false;
            }
            if ((mXA <= target.xPos + target.width + 9 * chunkOffset) &&
                    (mXA + this.width >= target.xPos + 9 * chunkOffset) &&
                    (mYA <= target.yPos + target.height) &&
                    (mYA + this.height >= target.yPos)) {
                if (this.yVelocity <= -0.15f * (Gameloop.TICKDURATION / (double) 1000) && this.yPos > target.yPos + target.height) {
                    // The Goomba diedd, Mario bounces off
                    this.yVelocity = 3 * (Gameloop.TICKDURATION / (double) 1000);
                    target.interactable = false;
                    gameloop.score += 100;
                } else {
                    //Mario dies
                    this.yVelocity = 6 * (Gameloop.TICKDURATION / (double) 1000);
                    this.interactable = false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target, int chunkOffset) {
        if (!this.interactable) {
            return false;
        }
        boolean collided = false;

        int x = chunkOffset * 9, y = 0;
        double thisMinX = this.xPos + ((this.xVelocity < 0) ? this.xVelocity : 0),
                thisMaxX = this.xPos + this.width + ((this.xVelocity > 0) ? this.xVelocity : 0),
                thisMinY = this.yPos + ((this.yVelocity < 0) ? this.yVelocity : 0),
                thisMaxY = this.yPos + this.height + ((this.yVelocity > 0) ? this.yVelocity : 0);


        for (ArrayList<AbstractBlock> row : target) {
            for (AbstractBlock block : row) {
                if (block instanceof StaticAbstractBlock) {
                    if (thisMinX < x + 1 && thisMaxX > x && thisMinY < y + 1 && thisMaxY > y) {
                        collided = true;
                        if (handleBlockCollision(x, y) == HitDirection.FROM_ABOVE) {
                            this.grounded = true;
                        }
                    }
                }
                x++;
            }
            x = chunkOffset * 9;
            y++;
        }

        return collided;
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

    public double getxPos() {
        return this.xPos;
    }

    public int getChunkIndex() {
        return this.chunkIndex;
    }

    public double getxVelocity() {
        return this.xVelocity;
    }
}