package Level;

import Game.*;
import Level.Block.AbstractBlock;
import Level.Block.NonStaticAbstractBlock;
import Level.Block.StaticAbstractBlock;

import java.util.ArrayList;

public abstract class Entity {
    public int chunkIndex;
    public double xPos;
    public double yPos;
    public double xVelocity;
    public double yVelocity;
    public double width;
    public double height;
    public boolean interactable = true;
    public Async<Integer> drawableID;
    public Async<Renderer.Drawable> pausedDrawable = null;
    private boolean isPaused = false;
    public enum Moving {MOVING_LEFT,MOVING_RIGHT};
    public Moving moving = Moving.MOVING_LEFT;
    public abstract void doMove(ArrayList<Chunk> chunks, Gameloop gameloop, long tickStart);
    public void updateTranslation(double xChunkTranslation, double xChunkVelocity, View view, long tickstart) {
        view.updatePosition(this.drawableID,
                new Vector3f((float) (this.xPos + xChunkTranslation), (float) this.yPos, 0),
                new Vector3f((float) (this.xVelocity + xChunkVelocity), (float) this.yVelocity, 0),
                tickstart);
    }

    public boolean collisionEntEnt(Entity target, Gameloop gameloop, int chunkOffset){
        if(!this.interactable) {return false;}
        if(!target.interactable) {return false;}
        double thisMinX = this.xPos + ((this.xVelocity < 0) ? this.xVelocity : 0),
                thisMaxX = this.xPos + this.width + ((this.xVelocity > 0) ? this.xVelocity : 0),
                thisMinY = this.yPos + ((this.yVelocity < 0) ? this.yVelocity : 0),
                thisMaxY = this.yPos + this.height + ((this.yVelocity > 0) ? this.yVelocity : 0);
        if (thisMinX < target.xPos+target.width+9*chunkOffset && thisMaxX > target.xPos + 9*chunkOffset && thisMinY < target.yPos+target.height && thisMaxY > target.yPos) {
            HitDirection direction = handleBlockCollision((int) target.xPos+9*chunkOffset, (int) target.yPos);
            if (direction != null) {
                switch (direction) {
                    case FROM_BELOW:
                        this.yVelocity -= this.yVelocity;
                        break;
                    case FROM_RIGHT:
                        this.moving = Moving.MOVING_LEFT;
                        break;
                    case FROM_LEFT:
                        this.moving = Moving.MOVING_RIGHT;
                        break;
                }
                return true;
            }
        }
        return false;
    }


    public boolean collisionEntBlc(ArrayList<ArrayList<AbstractBlock>> target, int chunkOffset){
        if (!this.interactable) { return false; }
        boolean collided = false;

        int x = chunkOffset*9, y = 0;
        double thisMinX = this.xPos + ((this.xVelocity < 0) ? this.xVelocity : 0),
                thisMaxX = this.xPos + this.width + ((this.xVelocity > 0) ? this.xVelocity : 0),
                thisMinY = this.yPos + ((this.yVelocity < 0) ? this.yVelocity : 0),
                thisMaxY = this.yPos + this.height + ((this.yVelocity > 0) ? this.yVelocity : 0);


        for (ArrayList<AbstractBlock> row : target) {
            for (AbstractBlock block : row) {
                if (block instanceof StaticAbstractBlock) {
                    if (thisMinX < x+1 && thisMaxX > x && thisMinY < y+1 && thisMaxY > y) {
                        collided = true;
                        HitDirection hitdirection = handleBlockCollision(x, y);
                        if (hitdirection!=null){
                            switch (hitdirection){
                                case FROM_BELOW:
                                    this.yVelocity-=this.yVelocity;
                                    break;
                                case FROM_RIGHT:
                                    this.moving = Moving.MOVING_RIGHT;
                                    break;
                                case FROM_LEFT:
                                    this.moving = Moving.MOVING_LEFT;
                                    break;
                            }
                        }
                    }
                }
                x++;
            }
            x = chunkOffset*9;
            y++;
        }

        return collided;
    }

    public abstract void updatePos();

    // Called when the entity moves into a chunk that isn't loaded
    // Should be called before the entity is removed so that the entities drawable is not in the renderers stage
    public void pause(View view){
        if (!this.isPaused) {
            this.isPaused = true;
            this.pausedDrawable = view.getDrawableByID(this.drawableID);
        }
    }
    // Called when reloading an previously paused chunk
    public void unPause(View view){
        if (this.isPaused) {
            this.isPaused = false;
            this.drawableID = view.addToStage(this.pausedDrawable);
        }
    }

    public void moveToChunk(ArrayList<Chunk> chunks, int newChunkIndex, View view) {
        chunks.get(this.chunkIndex).removeEntity(this, view);
        chunks.get(newChunkIndex).addEntity(this, view);

        //this.xPos+=this.chunkIndex<newChunkIndex?-9:9;
        this.xPos += (this.chunkIndex-newChunkIndex)*9;
        this.chunkIndex=newChunkIndex;
    }

    public HitDirection handleBlockCollision(int blockX, int blockY) {
        if (this.yPos < blockY + 1 && this.yPos+this.width > blockY) {
            // the player was on the say y as the block, thus to the side
            if (this.xPos > blockX) {
                // the player is on the right side of the block
                this.xVelocity = blockX+1 - this.xPos;
                return HitDirection.FROM_RIGHT;
            } else {
                // the player is on the left side of the block
                this.xVelocity = blockX - (this.xPos + this.width);
                return HitDirection.FROM_LEFT;
            }
        } else if (this.xPos < blockX + 1 && this.xPos+this.width > blockX) {
            // the player was above or bellow the block
            if (this.yPos > blockY) {
                // the player is over the block
                this.yVelocity = blockY+1 - this.yPos;
                return HitDirection.FROM_ABOVE;
            } else {
                // the player is below the block
                this.yVelocity = blockY - (this.yPos + this.height);
                return HitDirection.FROM_BELOW;
            }
        }
        return null;
    }
}
