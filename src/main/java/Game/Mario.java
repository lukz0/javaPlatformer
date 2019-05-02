package Game;


import Level.Block.AbstractBlock;
import Level.Block.NonStaticAbstractBlock;
import Level.Block.StaticAbstractBlock;
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
        if (!this.isPaused) {
            if (gameloop.holdingLeft != gameloop.holdingRight && this.interactable) {
                if (gameloop.holdingLeft && (this.chunkIndex >= 0 && this.xPos >= 3 * (Gameloop.TICKDURATION / (double) 1000))) {
                    this.xVelocity = -3 * (Gameloop.TICKDURATION / (double) 1000);
                    if (this.currentState != STATE_MOVING_LEFT && grounded) {
                        this.currentState = STATE_MOVING_LEFT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    } else if(!grounded) {
                        this.currentState = STATE_JUMP_LEFT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    }
                } else if(gameloop.holdingRight) {
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
            if (gameloop.holdingSpace && this.interactable) {
                //if(grounded) {
                    if (this.currentState == STATE_IDLE_LEFT || this.currentState == STATE_MOVING_LEFT) {
                        this.currentState = STATE_JUMP_LEFT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    } else if (this.currentState == STATE_IDLE_RIGHT || this.currentState == STATE_MOVING_RIGHT) {
                        this.currentState = STATE_JUMP_RIGHT;
                        gameloop.view.setActiveState(this.drawableID, this.currentState);
                    }

                    // TODO: find appropriate value
                if(this.grounded) {
                    this.yVelocity = 6.5f * (Gameloop.TICKDURATION/(double)1000);
                    this.grounded = false;
                }
                //}
                /*else {
                    this.yVelocity = 0;
                }*/
            }

            // TODO: change
            this.yVelocity -= 0.15f * (Gameloop.TICKDURATION/(double)1000);
        }

        // TODO: add collision detection before adding velocity to position
        // TODO: changing chunks when translation < 0 or translation > 9


        //gameloop.view.updatePosition(this.drawableID, this.translation, this.velocity, tickStart);
    }

    @Override
    public void updatePos(){
        this.xPos += this.xVelocity;
        this.yPos += this.yVelocity;
        //System.out.println("[MARIO] Xp/Yp/Xv/Yv/cnk: " + this.xPos + "/" + this.yPos + "/" + this.xVelocity + "/" + this.yVelocity + "/" + this.chunkIndex);
    }

    @Override
    public boolean collisionEntEnt(Entity target) {
        double mXA = this.xPos + this.xVelocity;
        double mYA = this.yPos + this.yVelocity;

        if(!this.interactable) {return false;}

        if(target instanceof Brick) {
            if(this.xVelocity == 0) {
                if(this.yVelocity < 0) {
                    if(((mXA <= target.xPos + target.width) &&
                            (mXA + this.width >= target.xPos) &&
                            (mYA <= target.yPos + target.height) &&
                            (mYA + this.height >= target.yPos))) {
                        this.yVelocity = 0;
                        grounded = true;
                        return true;
                    }
                } else {
                    if(((mXA <= target.xPos + target.width) &&
                            (mXA + this.width >= target.xPos) &&
                            (mYA <= target.yPos + target.height) &&
                            (mYA + this.height >= target.yPos))) {
                        target.interactable = false;
                        //System.out.println("[MARIO] Brick has broken");
                        this.yVelocity = 0;
                        return true;
                    }
                }
            } else {
                if(((mXA <= target.xPos + target.width) &&
                        (mXA + this.width >= target.xPos) &&
                        (mYA <= target.yPos + target.height) &&
                        (mYA + this.height >= target.yPos)) &&

                        !((mXA <= target.xPos + target.width) &&
                                (mXA + this.width >= target.xPos) &&
                                (this.yPos <= target.yPos + target.height) &&
                                (this.yPos + this.height >= target.yPos))) {
                    this.yVelocity = 0;
                    this.grounded = true;
                    return true;
                } else if(((mXA <= target.xPos + target.width) &&
                        (mXA + this.width >= target.xPos) &&
                        (mYA <= target.yPos + target.height) &&
                        (mYA + this.height >= target.yPos)) &&

                        !((this.xPos <= target.xPos + target.width) &&
                                (this.xPos + this.width >= target.xPos) &&
                                (mYA <= target.yPos + target.height) &&
                                (mYA + this.height >= target.yPos))) {
                    this.xVelocity = 0;
                    return true;
                } else if((mXA <= target.xPos + target.width) &&
                        (mXA + this.width >= target.xPos) &&
                        (mYA <= target.yPos + target.height) &&
                        (mYA + this.height >= target.yPos)) {
                    this.xVelocity = 0;
                    this.yVelocity = 0;
                    return true;
                }
            }
        } else {
            if(!target.interactable) {return false;}
            if ((mXA <= target.xPos + target.width) &&
                    (mXA + this.width >= target.xPos) &&
                    (mYA <= target.yPos + target.height) &&
                    (mYA + this.height >= target.yPos)) {
                if (this.yVelocity < -0.1f * (Gameloop.TICKDURATION/(double)1000)) {
                    //Goomba ded, Mario jumps
                    this.yVelocity = 3 * (Gameloop.TICKDURATION/(double)1000);
                    target.interactable = false;
                } else {
                    //Mario has died.
                    this.yVelocity = 3 * (Gameloop.TICKDURATION/(double)1000);
                    this.interactable = false;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target) {
        this.grounded = false;
        if (!this.interactable) { return false; }
        boolean collided = false;

        int x = 0, y = 0;
        double thisMinX = this.xPos + ((this.xVelocity < 0) ? this.xVelocity : 0),
                thisMaxX = this.xPos + this.width + ((this.xVelocity > 0) ? this.xVelocity : 0),
                thisMinY = this.yPos + ((this.yVelocity < 0) ? this.yVelocity : 0),
                thisMaxY = this.yPos + this.height + ((this.yVelocity > 0) ? this.yVelocity : 0);


        for (ArrayList<AbstractBlock> row : target) {
            for (AbstractBlock block : row) {
                if (block instanceof StaticAbstractBlock) {
                    if (thisMinX < x+1 && thisMaxX > x) {
                        if (thisMinY < y+1 && thisMaxY > y) {
                            collided = true;
                            handleBlockCollision(x, y);
                        }
                    }
                }
                x++;
            }
            x = 0;
            y++;
        }

        return collided;
    }
    private void handleBlockCollision(int blockX, int blockY) {
        if (this.yPos < blockY + 1 && this.yPos+this.width > blockY) {
            // the player was on the say y as the block, thus to the side
            if (this.xPos > blockX) {
                // the player is on the right side of the block
                this.xVelocity = blockX+1 - this.xPos;
            } else {
                // the player is on the left side of the block
                this.xVelocity = blockX - (this.xPos + this.width);
            }
        } else if (this.xPos < blockX + 1 && this.xPos+this.width > blockX) {
            // the player was above or bellow the block
            if (this.yPos > blockY) {
                // the player is over the block
                this.grounded = true;
                this.yVelocity = blockY+1 - this.yPos;
            } else {
                // the player is below the block
                this.yVelocity = blockY - (this.yPos + this.height);
            }
        }
    }

    private boolean touchedGround = true;

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